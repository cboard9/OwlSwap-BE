package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.*;

import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotAvailableException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemMetadata.FieldSchema;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemMetadata.ItemTypeSchema;
import com.cboard.owlswap.owlswap_backend.model.Dto.ProductDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.RequestDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ServiceDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto.DtoToItemFactory;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.owlswap.owlswap_backend.model.Item;
import com.cboard.owlswap.owlswap_backend.model.User;
import com.cboard.owlswap.owlswap_backend.model.Location;
import com.cboard.owlswap.owlswap_backend.model.Category;

import com.cboard.owlswap.owlswap_backend.model.ItemImage;
import com.cboard.owlswap.owlswap_backend.model.context.ItemMappingContext;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {
    @Autowired
    ItemDao dao;
    @Autowired
    UserService userService;
    @Autowired
    CurrentUser currentUser;
    @Autowired
    ItemImageDao itemImageDao;
    private final UserDao userDao;
    private final LocationDao locationDao;
    private final CategoryDao categoryDao;
    private final ItemToDtoFactory toDtoFactory;
    private final DtoToItemFactory fromDtoFactory;

    //this automatically adds all the different dto mappers through injection
/*    public ItemService(ItemDao dao,

                       ItemToDtoFactory toDtoFactory,
                       DtoToItemFactory fromDtoFactory) {
        this.dao = dao;
        this.toDtoFactory = toDtoFactory;
        this.fromDtoFactory = fromDtoFactory;
    }*/

    public ItemService(UserDao userDao,
                       LocationDao locationDao,
                       CategoryDao categoryDao, ItemToDtoFactory toDtoFactory,
                       DtoToItemFactory fromDtoFactory) {
        this.userDao = userDao;
        this.locationDao = locationDao;
        this.categoryDao = categoryDao;
        this.toDtoFactory = toDtoFactory;
        this.fromDtoFactory = fromDtoFactory;
    }

    public Page<ItemDto> getAllItems(Pageable pageable) {
        Integer userId = currentUser.userId(); //from JWT
        Page<Item> items = dao.findByAvailableTrueAndUserUserIdNot(userId, pageable);

        return items
                .map(item -> {
                            try {
                                return toDtoFactory.toDto(item);
                            } catch (IllegalArgumentException e) {
                                throw new DtoMappingException("Failed to map Item to DTO. itemId=" + item.getItemId(), e);
                            }
                        }
                );
    }

    //returns an item from its id if it exists and it is available
    public ItemDto getItem(int itemId) {
        Item item = dao.findByItemId(itemId);

        if(item == null)
            throw new NotFoundException("Item not found, item id: " + itemId);

        if (!item.isAvailable())
            throw new NotAvailableException("Item not available.");

        try
        {
            return toDtoFactory.toDto(item);
        }
        catch (IllegalArgumentException e)
        {
            throw new DtoMappingException("Failed to map Item to DTO. itemId=" + item.getItemId(), e);
        }

    }


    public ResponseEntity<Page<ItemDto>> getItemByOwner(int userId, Pageable pageable) {
        Page<ItemDto> dtos = null;
        try {
            Page<Item> items = dao.findByUserUserIdAndAvailableTrue(userId, pageable);
            dtos =
            //List<ItemDto> items = dao.findAllByUser_UserId(userId).stream().filter(Item::isAvailable)
                    items.map(item -> {
                                try {
                                    return toDtoFactory.toDto(item);
                                } catch (IllegalArgumentException e) {
                                    throw new DtoMappingException("Error converting item to DTO: " + item, e);
                                }
                            }
                    )
                    ;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);

    }

    @Transactional
    public ItemDto addItem(ItemDto itemDto)
    {
        User user = userDao.findById(currentUser.userId())
                .orElseThrow(() -> new NotFoundException("User not found."));

        Location location = locationDao.findById(itemDto.getLocationId())
                .orElseThrow(() -> new NotFoundException("Location not found."));

        Category category = categoryDao.findByName(itemDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found. name=" + itemDto.getCategory()));

        ItemMappingContext ctx = new ItemMappingContext(user, category, location);

        try {
            Item item = fromDtoFactory.fromDto(itemDto, ctx);

            if (item.getReleaseDate() == null)
                item.setReleaseDate(String.valueOf(LocalDate.now()));

            //item.setUser(currentUser.user()); //later remove the user mapping from the mappers since setting here

            item.setItemId(0); //forcing this to be a true add
            Item saved = dao.save(item);
            return toDtoFactory.toDto(saved);

        } catch (IllegalArgumentException e) {
            throw new DtoMappingException("Failed to map DTO to Item.", e);
        }

    }


    public ItemDto updateItem(ItemDto dto) {
        Item existing = dao.findById(dto.getItemId())
                .orElseThrow( () -> new NotFoundException("Item not found, can not update."));

        Integer currentUserId = currentUser.userId();
        Integer ownerId = existing.getUser().getUserId();

        if (!ownerId.equals(currentUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot update this item.");
            // or throw your own ForbiddenException and handle -> 403 ApiError
        }



        try {
            Location location = locationDao.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException("Location not found."));

            Category category = categoryDao.findByName(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found. name=" + dto.getCategory()));

            ItemMappingContext ctx = new ItemMappingContext(existing.getUser(), category, location);

            Item item = fromDtoFactory.fromDto(dto, ctx);

            // prevent type switching
            if (!existing.getClass().equals(item.getClass())) {
                throw new IllegalArgumentException("Cannot change item type during update.");
            }

            item.setItemId(dto.getItemId());

            //item.setUser(existing.getUser());
            Item updated = dao.save(item);
            return toDtoFactory.toDto(updated);

        } catch (Exception e) {
            throw new DtoMappingException("Failed to map DTO to Item.", e);
        }

    }

    public ResponseEntity<String> softDeleteItem(int itemId) {
        if (!dao.existsById(itemId))
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);

        dao.softDeleteItem(itemId);
        return new ResponseEntity<>("Item deleted", HttpStatus.OK);
    }

    public void deleteItem(int itemId) {
        Item item = dao.findByItemId(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found: " + itemId);
        }

        Integer currentUserId = currentUser.userId();
        Integer ownerId = item.getUser().getUserId();

        if (!ownerId.equals(currentUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot delete this item.");
            // or throw your own ForbiddenException and handle -> 403 ApiError
        }

        //originally soft deleted but availability being used for sold or not, consider changing this later
        //item.setAvailable(false);
        //dao.save(item);

        dao.delete(item);
    }

    @Transactional
    public ItemDto addItemWithImages(ItemDto dto, List<MultipartFile> images) throws IOException {

        User user = userDao.findById(currentUser.userId())
                .orElseThrow(() -> new NotFoundException("User not found."));

        Location location = locationDao.findById(dto.getLocationId())
                .orElseThrow(() -> new NotFoundException("Location not found."));

        Category category = categoryDao.findByName(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found. name=" + dto.getCategory()));

        ItemMappingContext ctx = new ItemMappingContext(user, category, location);


        try {
            Item item = fromDtoFactory.fromDto(dto, ctx);

            if (item.getReleaseDate() == null || item.getReleaseDate().isBlank())
                item.setReleaseDate(String.valueOf(LocalDate.now()));


            if(images != null) {
                for (MultipartFile file : images) {
                    if (file == null || file.isEmpty()) continue;

                    ItemImage image = new ItemImage();
                    image.setImage_name(file.getOriginalFilename());
                    image.setImage_type(file.getContentType());
                    image.setImage_date(file.getBytes());
                    //ItemImage savedImage = itemImageDao.save(image);
                    item.addImage(image);
                }
            }

            item.setItemId(0); //enforce this being a new item
            Item saved = dao.save(item);

            return toDtoFactory.toDto(saved);

        } catch (IllegalArgumentException e) {
            throw new DtoMappingException("Failed to map DTO to Item.", e);
        }
    }


    public ItemDto updateItemWithImages(ItemDto dto, List<MultipartFile> images) throws IOException {
        int itemId = dto.getItemId();
        Item existing = dao.findByItemId(itemId);
        if (existing == null)
            throw new NotFoundException("Item not found, can not updated. id=" + itemId);

        Integer currentUserId = currentUser.userId();
        Integer ownerId = existing.getUser().getUserId();

        if (!ownerId.equals(currentUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot update this item.");
            // or throw your own ForbiddenException and handle -> 403 ApiError
        }

        try {
            Location location = locationDao.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException("Location not found."));

            Category category = categoryDao.findByName(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found. name=" + dto.getCategory()));

            ItemMappingContext ctx = new ItemMappingContext(existing.getUser(), category, location);

            Item mapped = fromDtoFactory.fromDto(dto, ctx);

            // Copy over fields you allow updating
            existing.setName(mapped.getName());
            existing.setDescription(mapped.getDescription());
            existing.setPrice(mapped.getPrice());
            existing.setCategory(mapped.getCategory());
            existing.setLocation(mapped.getLocation());
            existing.setItemType(mapped.getItemType());

            // Preserve fields you DON'T want reset by DTO mapping
            // existing.setAvailable(existing.isAvailable()); // already preserved since we never touched it
            // existing.setUser(existing.getUser());         // preserved
            // existing.setReleaseDate(existing.getReleaseDate()); // preserved

            // Image behavior: replace only if new images provided
            if (images != null && !images.isEmpty()) {
                // orphanRemoval=true means these DB rows will be deleted
                existing.getImages().clear();

                for (MultipartFile file : images) {
                    ItemImage img = new ItemImage();
                    img.setImage_name(file.getOriginalFilename());
                    img.setImage_type(file.getContentType());
                    img.setImage_date(file.getBytes());

                    // sets img.item = existing and adds to list
                    existing.addImage(img);
                }
            }

            dao.save(existing);

            return toDtoFactory.toDto(existing);

        } catch (IllegalArgumentException e) {
            throw new DtoMappingException("Failed to map ItemDto to Item for update. id=" + dto.getItemId(), e);
        }
    }

    public Page<ItemDto> searchItems(String keyword, String category, Pageable pageable) {
        Integer userId = currentUser.userId();
        Page<Item> items;
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (hasKeyword && hasCategory) {
            items = dao.findByUserUserIdNotAndAvailableTrueAndNameContainingIgnoreCaseAndCategoryNameOrUserUserIdNotAndAvailableTrueAndDescriptionContainingIgnoreCaseAndCategoryName(
                    userId, keyword, category, userId, keyword, category, pageable);

        } else if (hasKeyword) {
            items = dao.findByUserUserIdNotAndNameContainingIgnoreCaseAndAvailableTrueOrUserUserIdNotAndDescriptionContainingIgnoreCaseAndAvailableTrue(userId, keyword, userId, keyword, pageable);
        } else if (hasCategory) {
            items = dao.findByCategoryNameAndAvailableTrueAndUserUserIdNot(category, userId, pageable);
        } else {
            items = dao.findByAvailableTrueAndUserUserIdNot(userId, pageable);
        }

        return items
                .map(item -> {
                            try {
                                return toDtoFactory.toDto(item);
                            } catch (IllegalArgumentException e) {
                                throw new DtoMappingException("Failed to map Item to DTO. itemId=" + item.getItemId(), e);
                            }
                        }
                );
    }


    public List<ItemTypeSchema> getAllSchemas() {
        List<ItemTypeSchema> schemas = new ArrayList<>();

        for (Class<? extends ItemDto> cls : List.of(ProductDto.class, ServiceDto.class, RequestDto.class)) {
            ItemDto instance = instantiateDto(cls);

            String typeName = instance.getSimpleName();
            Map<String, Serializable> spec = instance.getSpecificFields();

            List<FieldSchema> fieldList = new ArrayList<>();
            for (Map.Entry<String, Serializable> entry : spec.entrySet()) {
                String key = entry.getKey();
                Serializable value = entry.getValue();

                fieldList.add(new FieldSchema(
                        key,
                        key,
                        spec.get(key).getClass().getSimpleName()
                        //normalizeType(value) //consider normalizing later if
                ));
            }

            schemas.add(new ItemTypeSchema(typeName, fieldList));
        }

        return schemas;
    }

    private ItemDto instantiateDto(Class<? extends ItemDto> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // This is a server configuration/code issue, not client fault
            throw new RuntimeException("Failed to instantiate DTO type for schema: " + cls.getName(), e);
        }
    }

    private String normalizeType(Serializable value) {
        if (value == null) return "string"; // or "unknown"
        if (value instanceof Number) return "number";
        if (value instanceof Boolean) return "boolean";
        // you can add LocalDate, Instant, etc. if you use them in defaults
        return "string";
    }

}
