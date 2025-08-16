package com.cboard.marketplace.marketplace_backend.controller;


import com.cboard.marketplace.marketplace_backend.model.Dto.CategoryDto;
import com.cboard.marketplace.marketplace_backend.model.Dto.ItemDto;
import com.cboard.marketplace.marketplace_backend.model.Dto.ItemMetadata.ItemTypeSchema;
import com.cboard.marketplace.marketplace_backend.service.CategoryService;
import com.cboard.marketplace.marketplace_backend.service.ItemService;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("item")
@Validated
public class ItemController
{
    @Autowired
    private Validator validator;

    @Autowired
    ItemService service;

    @Autowired
    private CategoryService categoryService;

    //returns all available items
    @GetMapping("all")
    public ResponseEntity<Page<ItemDto>> getAllItems(@PageableDefault(size=6) Pageable pageable)
    {
        //could potentially throw a runtime exception if cant map an item, consider handling this later?
        return service.getAllItems(pageable);
    }

    @GetMapping("search")
    public ResponseEntity<Page<ItemDto>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @PageableDefault(size=6) Pageable pageable)
    {
        return service.searchItems(keyword, category, pageable);
    }

/*    @GetMapping("search")
    public ResponseEntity<Page<ItemDto>> searchItems(@RequestParam String keyword, @PageableDefault(size=6) Pageable pageable)
    {
        return service.searchItems(keyword, pageable);
    }
    @GetMapping("category")
    public ResponseEntity<Page<ItemDto>> itemsByCat(@RequestParam String category, @PageableDefault(size=6) Pageable pageable)
    {
        return service.itemsByCat(category, pageable);
    }*/

    @GetMapping("{id}")
    public ResponseEntity<?> getItem(@PathVariable("id") int itemId)
    {
        return service.getItem(itemId);
    }

    @PostMapping("add")
    public ResponseEntity<String> addItem(@Valid @RequestBody ItemDto itemDto)
    {
        //could potentially throw a runtime exception if cant map an item, consider handling this later?
        return service.addItem(itemDto);

    }

    @PutMapping("update")
    public ResponseEntity<?> updateItem(@Valid @RequestBody ItemDto dto)
    {
        return service.updateItem(dto);
    }


    @PutMapping(
            value = "update/with-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> updateItemWithImage(@Valid @RequestPart("item") ItemDto dto, @RequestPart(value = "image", required = false) MultipartFile image)
    {
        System.out.println(
                dto.getSpecificFields()
        );

        try
        {
            return service.updateItemWithImage(dto, image);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("{id}/owner")
    public ResponseEntity<Page<ItemDto>> getItemByOwner(@PathVariable("id") int userId, @PageableDefault(size=6) Pageable pageable)
    {
        return service.getItemByOwner(userId, pageable);

    }

    //upload an image
    @PostMapping("{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable("id") int itemId, @RequestParam("file")MultipartFile file)
    {
        try
        {
            return service.uploadImage(itemId, file);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }


    //upload image and item at same time
    @PostMapping(
            value = "add/with-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> addItemWithImage(
            @RequestPart("item") @Valid ItemDto dto,
            @RequestPart("image") MultipartFile image)
    {
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (a,b)->a
                    ));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try
        {
            return service.addItemWithImage(dto, image);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("schema")
    public ResponseEntity<List<ItemTypeSchema>> getItemScehmas(){
        return service.getAllschemas();
    }



    //soft deletes an item -- instead of actually deleting an item, turns its available attribute to false
    @DeleteMapping("{id}/delete")
    public ResponseEntity<String> deleteItem(@PathVariable("id") int itemId)
    {
        return service.deleteItem(itemId);
    }

    @GetMapping("types")
    public ResponseEntity<List<String>> getItemTypes() {
        List<String> itemTypes = List.of("Product", "Service", "Request");
        return ResponseEntity.ok(itemTypes);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAll();
    }
}