package com.learning.app.category.service;

import com.learning.app.category.model.Category;

import javax.ejb.Local;
import java.util.List;

@Local
public interface CategoryService
{
    Category add(Category category);

    void update(Category category);

    Category findById(Long id);

    List<Category> findAll();

    void deleteById(Long id);
}
