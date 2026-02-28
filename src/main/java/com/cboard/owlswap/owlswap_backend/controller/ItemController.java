package com.cboard.owlswap.owlswap_backend.controller;


import com.cboard.owlswap.owlswap_backend.model.Dto.CategoryDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemMetadata.ItemTypeSchema;
import com.cboard.owlswap.owlswap_backend.service.CategoryService;
import com.cboard.owlswap.owlswap_backend.service.ItemService;
import jakarta.validation.*;
import org.apache.catalina.connector.Response;
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

import java.io.IOException;
import java.net.URI;
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
        //return service.getAllItems(pageable);
        return ResponseEntity.ok(service.getAllItems(pageable));
    }

    @GetMapping("search")
    public ResponseEntity<Page<ItemDto>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @PageableDefault(size=6) Pageable pageable)
    {
        return ResponseEntity.ok(service.searchItems(keyword, category, pageable));
    }

    @GetMapping("{id}")
    public ResponseEntity<ItemDto> getItem(@PathVariable("id") int itemId)
    {
        return ResponseEntity.ok(service.getItem(itemId));
    }

    @PostMapping("add")
    public ResponseEntity<ItemDto> addItem(@Valid @RequestBody ItemDto itemDto)
    {
        ItemDto saved = service.addItem(itemDto);

        URI location = URI.create("/item/" + saved.getItemId());

        return ResponseEntity.created(location).body(saved);

    }

    @PutMapping("update")
    public ResponseEntity<ItemDto> updateItem(@Valid @RequestBody ItemDto dto)
    {
        ItemDto updated = service.updateItem(dto);
        return ResponseEntity.ok(updated);
    }


/*    @PutMapping(
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
    }*/
    @PutMapping(
            value = "update/with-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ItemDto> updateItemWithImages(
            @Valid @RequestPart("item") ItemDto dto,
            @RequestPart(value = "image", required = false) List<MultipartFile> images)
    throws IOException
    {
        //System.out.println(dto.getSpecificFields());

        ItemDto updated = service.updateItemWithImages(dto, images);
        return ResponseEntity.ok(updated);

    }

    @GetMapping("{id}/owner")
    public ResponseEntity<Page<ItemDto>> getItemByOwner(@PathVariable("id") int userId, @PageableDefault(size=6) Pageable pageable)
    {
        return service.getItemByOwner(userId, pageable);

    }


    @PostMapping(
            value = "add/with-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ItemDto> addItemWithImages(
            @RequestPart("item") @Valid ItemDto dto,
            @RequestPart("images") List<MultipartFile> images) throws IOException
    {

        ItemDto saved = service.addItemWithImages(dto, images);

        URI location = URI.create("/item/" + saved.getItemId());

        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("schema")
    public ResponseEntity<List<ItemTypeSchema>> getItemSchemas(){
        return ResponseEntity.ok(service.getAllSchemas());
    }

    @DeleteMapping("{id}/delete")
    public ResponseEntity<String> deleteItem(@PathVariable("id") int itemId)
    {
        service.deleteItem(itemId);
        return ResponseEntity.ok("Item deleted");

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