package com.learning.app.category.service;

import com.learning.app.category.exception.CategoryNotFoundException;
import com.learning.app.category.model.Category;
import com.learning.app.category.repository.CategoryRepository;
import com.learning.app.common.utils.DataValidation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.List;

@Stateless
public class CategoryServiceImpl implements CategoryService
{
    @Inject
    Validator validator;

    @Inject
    CategoryRepository categoryRepository;


    @Override
    public Category add(Category category)
    {
        DataValidation.validateEntityFields(validator, category);
        return categoryRepository.add(category);
    }

    @Override
    public void update(Category category)
    {
        DataValidation.validateEntityFields(validator, category);

        if (!categoryRepository.idExists(category.getId()))
        {
            throw new CategoryNotFoundException();
        }

        categoryRepository.update(category);
    }

    @Override
    public Category findById(Long id)
    {
        Category category = categoryRepository.findById(id);

        if (category == null)
        {
            throw new CategoryNotFoundException();
        }

        return category;
    }

    @Override
    public List<Category> findAll()
    {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteById(Long id)
    {
        if (!categoryRepository.idExists(id))
        {
            throw new CategoryNotFoundException();
        }

        categoryRepository.delete(id);
    }
}
