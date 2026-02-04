package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.ItemDao;

import com.cboard.owlswap.owlswap_backend.dao.ItemImageDao;
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
    ItemImageDao itemImageDao;
    private final ItemToDtoFactory toDtoFactory;
    private final DtoToItemFactory fromDtoFactory;

    //this automatically adds all the different dto mappers through injection
    public ItemService(ItemDao dao, ItemToDtoFactory toDtoFactory, DtoToItemFactory fromDtoFactory) {
        this.dao = dao;
        this.toDtoFactory = toDtoFactory;
        this.fromDtoFactory = fromDtoFactory;
    }

    public ResponseEntity<Page<ItemDto>> getAllItems(Pageable pageable) {
        Integer userId = userService.getProfile().getBody().getUserId();
        Page<Item> items = dao.findByAvailableTrueAndUserUserIdNot(userId, pageable);

        Page<ItemDto> dtoPage = items
                .map(item -> {
                            try {
                                return toDtoFactory.toDto(item);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                throw new RuntimeException("Error converting item to DTO: " + item, e);
                            }
                        }
                );

        /*List<ItemDto> items = dao.findAll().stream()
                .filter(Item::isAvailable)
                .map(item -> {
                    try
                    {
                        return toDtoFactory.toDto(item);
                    }
                    catch(IllegalAccessException e)
                    {
                        e.printStackTrace();
                        throw new RuntimeException("Error converting item to DTO: " + item, e);
                    }
                        }
                )
                .toList();*/

        return new ResponseEntity<>(dtoPage, HttpStatus.OK);

    }

    //returns an item from its id if it exists and it is available
    public ResponseEntity<?> getItem(int itemId) {
        try {
            Item item = dao.findByItemId(itemId);

            if (!item.isAvailable())
                return new ResponseEntity<>("Item not available...", HttpStatus.NOT_FOUND);

            ItemDto dto = toDtoFactory.toDto(item);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred", HttpStatus.BAD_REQUEST);
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

    public ResponseEntity<String> addItem(ItemDto itemDto) {
        try {
            Item item = fromDtoFactory.fromDto(itemDto);
            if (item.getReleaseDate() == null)
                item.setReleaseDate(String.valueOf(LocalDate.now()));
            dao.save(item);
            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Error converting DTO to item: " + itemDto, e);
        }

    }


    public ResponseEntity<String> updateItem(ItemDto dto) {
        if (!dao.existsById(dto.getItemId()))
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);

        try {
            Item item = fromDtoFactory.fromDto(dto);
            item.setItemId(dto.getItemId());

            dao.save(item);
            return new ResponseEntity<>("Item updated", HttpStatus.OK);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Illegal access error", HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<String> softDeleteItem(int itemId) {
        if (!dao.existsById(itemId))
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);

        dao.softDeleteItem(itemId);
        return new ResponseEntity<>("Item deleted", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteItem(int itemId) {
        if (!dao.existsById(itemId))
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);

        dao.delete(dao.findByItemId(itemId));
        return new ResponseEntity<>("Item deleted", HttpStatus.OK);
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
    public ResponseEntity<String> addItemWithImages(ItemDto dto, List<MultipartFile> images) throws IOException {
        try {
            Item item = fromDtoFactory.fromDto(dto);
            if (item.getReleaseDate() == null)
                item.setReleaseDate(String.valueOf(LocalDate.now()));

            for(MultipartFile file : images)
            {
                ItemImage image = new ItemImage();
                image.setImage_name(file.getOriginalFilename());
                image.setImage_type(file.getContentType());
                image.setImage_date(file.getBytes());
                //ItemImage savedImage = itemImageDao.save(image);
                item.addImage(image);
            }
            dao.save(item);

            return new ResponseEntity<>("Item created with image!", HttpStatus.CREATED);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Error converting DTO to item: " + dto, e);
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

    public ResponseEntity<String> updateItemWithImages(ItemDto dto, List<MultipartFile> images) throws IOException {
        int itemId = dto.getItemId();
        Item existingItem = dao.findByItemId(itemId);
        if (existingItem == null)
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);

        try {

            Item item = fromDtoFactory.fromDto(dto);
            item.setItemId(dto.getItemId());

            if(images != null && !images.isEmpty()) {
                for(MultipartFile file : images)
                {
                    ItemImage image = new ItemImage();
                    image.setImage_name(file.getOriginalFilename());
                    image.setImage_type(file.getContentType());
                    image.setImage_date(file.getBytes());
                    ItemImage savedImage = itemImageDao.save(image);
                    item.addImage(savedImage);
                }
            }
            else
            {
                for(ItemImage image : existingItem.getImages())
                    item.addImage(image);
            }

            dao.save(item);
            return new ResponseEntity<>("Item updated", HttpStatus.OK);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Illegal access error", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Page<ItemDto>> searchItems(String keyword, String category, Pageable pageable) {
        Integer userId = userService.getProfile().getBody().getUserId();
        Page<Item> items;

        if (keyword != null && category != null) {
            //items = dao.findByUserUserIdNotAndAvailableTrueAndNameContainingIgnoreCaseOrUserUserIdNotAndAvailableTrueAndDescriptionContainingIgnoreCaseOrUserUserIdNotAndAvailableTrueAndCategoryName(
                    //userId, keyword, userId, keyword, userId, category, pageable);
            items = dao.findByUserUserIdNotAndAvailableTrueAndNameContainingIgnoreCaseAndCategoryNameOrUserUserIdNotAndAvailableTrueAndDescriptionContainingIgnoreCaseAndCategoryName(
                    userId, keyword, category, userId, keyword, category, pageable);

        } else if (keyword != null) {
            items = dao.findByUserUserIdNotAndNameContainingIgnoreCaseAndAvailableTrueOrUserUserIdNotAndDescriptionContainingIgnoreCaseAndAvailableTrue(userId, keyword, userId, keyword, pageable);
        } else if (category != null) {
            items = dao.findByCategoryNameAndAvailableTrueAndUserUserIdNot(category, userId, pageable);
        } else {
            items = dao.findByAvailableTrueAndUserUserIdNot(userId, pageable);
        }

        if (items.isEmpty())
            return new ResponseEntity<>(Page.empty(pageable), HttpStatus.OK);

        Page<ItemDto> dtoPage = items
                .map(item -> {
                            try {

                                return toDtoFactory.toDto(item);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                throw new RuntimeException("Error converting item to DTO: " + item, e);
                            }
                        }
                );

        return new ResponseEntity<>(dtoPage, HttpStatus.OK);
    }

    public ResponseEntity<List<ItemTypeSchema>> getAllschemas() {
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
    }
}
