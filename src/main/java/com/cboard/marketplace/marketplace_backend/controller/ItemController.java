package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.Item;
import com.cboard.marketplace.marketplace_backend.model.Product;
import com.cboard.marketplace.marketplace_backend.model.Request;
import com.cboard.marketplace.marketplace_backend.model.Service;
import com.cboard.marketplace.marketplace_backend.service.ItemService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("item")
public class ItemController
{
    @Autowired
    ItemService service;

    @GetMapping("allItems")
    public ResponseEntity<List<Item>> getAllItems()
    {
        List<Item> items = service.getAllItems();

        for (Item item : items) {
            if (item instanceof Product) {
                Product product = (Product) item;
                System.out.println("Type: " + product.getItemType());
                System.out.println("Product: " + product.getQuantity());
                System.out.println("Brand: " + product.getBrand());
            } else if (item instanceof Service) {
                Service service = (Service) item;
                System.out.println("Type: " + service.getItemType());
                System.out.println("Service duration: " + service.getDurationMinutes() + " minutes");
            } else if (item instanceof Request) {
                Request request = (Request) item;
                System.out.println("Type: " + request.getItemType());
                System.out.println("Request deadline: " + request.getDeadline());
            }
        }

        //return service.getAllItems();
        return new ResponseEntity<>(service.getAllItems(), HttpStatus.OK);
    }


}
