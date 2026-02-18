package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.ItemDao;

import com.cboard.owlswap.owlswap_backend.dao.ItemImageDao;
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
import com.cboard.owlswap.owlswap_backend.model.ItemImage;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    private final ItemToDtoFactory toDtoFactory;
    private final DtoToItemFactory fromDtoFactory;

    //this automatically adds all the different dto mappers through injection
    public ItemService(ItemDao dao, ItemToDtoFactory toDtoFactory, DtoToItemFactory fromDtoFactory) {
        this.dao = dao;
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
                            } catch (IllegalAccessException e) {
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
        catch (IllegalAccessException e)
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
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException("Error converting item to DTO: " + item, e);
                                }
                            }
                    )
                    ;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);

    }

    public ItemDto addItem(ItemDto itemDto) {
        try {
            Item item = fromDtoFactory.fromDto(itemDto);

            if (item.getReleaseDate() == null)
                item.setReleaseDate(String.valueOf(LocalDate.now()));

            item.setUser(currentUser.user()); //later remove the user mapping from the mappers since setting here

            Item saved = dao.save(item);
            return toDtoFactory.toDto(saved);

        } catch (IllegalAccessException e) {
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
            Item item = fromDtoFactory.fromDto(dto);
            item.setItemId(dto.getItemId());

            item.setUser(existing.getUser());
            Item updated = dao.save(item);
            return toDtoFactory.toDto(updated);
        } catch (IllegalAccessException e) {
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

/*    public ResponseEntity<String> uploadImage(int itemId, MultipartFile file) throws IOException {
        Item item = dao.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setImage_name(file.getOriginalFilename());
        item.setImage_type(file.getContentType());
        item.setImage_date(file.getBytes());

        dao.save(item);
        return ResponseEntity.ok("Image uploaded");
    }*/

/*    public ResponseEntity<String> addItemWithImage(ItemDto dto, MultipartFile image) throws IOException {
        try {
            Item item = fromDtoFactory.fromDto(dto);
            if (item.getReleaseDate() == null)
                item.setReleaseDate(String.valueOf(LocalDate.now()));

            item.setImage_name(image.getOriginalFilename());
            item.setImage_type(image.getContentType());
            item.setImage_date(image.getBytes());
            dao.save(item);

            return new ResponseEntity<>("Item created with image!", HttpStatus.CREATED);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Error converting DTO to item: " + dto, e);
        }
    }*/
    public ItemDto addItemWithImages(ItemDto dto, List<MultipartFile> images) throws IOException {
        try {
            Item item = fromDtoFactory.fromDto(dto);

            if (item.getReleaseDate() == null)
                item.setReleaseDate(String.valueOf(LocalDate.now()));

            item.setUser(currentUser.user()); //later remove the user mapping from the mappers since setting here

            for(MultipartFile file : images)
            {
                ItemImage image = new ItemImage();
                image.setImage_name(file.getOriginalFilename());
                image.setImage_type(file.getContentType());
                image.setImage_date(file.getBytes());
                //ItemImage savedImage = itemImageDao.save(image);
                item.addImage(image);
            }

            Item saved = dao.save(item);

            return toDtoFactory.toDto(saved);

        } catch (IllegalAccessException e) {
            throw new DtoMappingException("Failed to map DTO to Item.", e);
        }
    }

/*    public ResponseEntity<String> updateItemWithImage(ItemDto dto, MultipartFile image) throws IOException {
        int itemId = dto.getItemId();
        Item existingItem = dao.findByItemId(itemId);
        if (existingItem == null)
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);

        try {

            Item item = fromDtoFactory.fromDto(dto);
            item.setItemId(dto.getItemId());

            if(image != null && !image.isEmpty()) {
                item.setImage_name(image.getOriginalFilename());
                item.setImage_type(image.getContentType());
                item.setImage_date(image.getBytes());
            }
            else
            {
                item.setImage_name(existingItem.getImage_name());
                item.setImage_type(existingItem.getImage_type());
                item.setImage_date(existingItem.getImage_date());
            }

            dao.save(item);
            return new ResponseEntity<>("Item updated", HttpStatus.OK);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Illegal access error", HttpStatus.BAD_REQUEST);
        }
    }*/

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
            Item mapped = fromDtoFactory.fromDto(dto);

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

        } catch (IllegalAccessException e) {
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
                            } catch (IllegalAccessException e) {
                                throw new DtoMappingException("Failed to map Item to DTO. itemId=" + item.getItemId(), e);
                            }
                        }
                );
    }

/*    public List<ItemTypeSchema> getAllSchemas() {
        List<ItemTypeSchema> schemas = new ArrayList<>();

        // for each concrete class you want to support...
        for (Class<? extends ItemDto> cls : List.of(ProductDto.class, ServiceDto.class, RequestDto.class)) {
            //String typeName = cls.getSimpleName(); // “Product”, “ServiceItem”, etc.

            try {
                // instantiate a “blank” instance (you’ll need a no-arg ctor or use defaults)
                ItemDto instance = cls.getDeclaredConstructor().newInstance();

                String typeName = instance.getSimpleName();

                // call its getSpecificFields() → Map<String,String>
                Map<String, Serializable> spec = instance.getSpecificFields();

                // turn that into a List<FieldSchema>
                List<FieldSchema> fieldList = new ArrayList<>();
                for (String key : spec.keySet()) {
                    System.out.println(spec.get(key).getClass().getSimpleName());
                    System.out.println("Key: " + key);
                    System.out.println("Value: " + spec.get(key));
                    fieldList.add(new FieldSchema(
                            key,
                            key,        // if you need a prettier label, store it in the class or use a naming strategy
                            spec.get(key).getClass().getSimpleName() // you could guess “number” if the value is numeric, etc.

                    ));
                }

                schemas.add(new ItemTypeSchema(typeName, fieldList));
            } catch (Exception ex) {
                System.out.println("Error finding itemSchemas");
            }
        }

        System.out.println("Schemas: " + schemas);
        return new ResponseEntity<>(schemas, HttpStatus.OK);
    }*/

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
