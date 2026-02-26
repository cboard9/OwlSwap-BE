package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.CategoryDao;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Category;
import com.cboard.owlswap.owlswap_backend.model.Dto.CategoryDto;
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
        return dao.findByName(name)
                .orElseThrow(() -> new NotFoundException("Category not found by that name. name=" + name));
    }

    public List<CategoryDto> getAll() {
        return dao.findAll().stream()
                .map(cat -> new CategoryDto(cat.getCategoryId(), cat.getName()))
                .collect(Collectors.toList());
    }
}