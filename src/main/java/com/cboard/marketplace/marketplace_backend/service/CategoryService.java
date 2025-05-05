package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.CategoryDao;
import com.cboard.marketplace.marketplace_backend.model.Category;
import com.cboard.marketplace.marketplace_common.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService
{
    @Autowired
    CategoryDao dao;
    public Category findByName(String name)
    {
        return dao.findByName(name);
    }

    public List<CategoryDto> getAll() {
        return dao.findAll().stream()
                .map(cat -> new CategoryDto(cat.getCategoryId(), cat.getName()))
                .collect(Collectors.toList());
    }
}