package com.learning.app.category.service;

import com.learning.app.category.exception.CategoryNotFoundException;
import com.learning.app.category.model.Category;
import com.learning.app.category.repository.CategoryRepository;
import com.learning.app.common.exception.FieldInvalidException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;

import static com.learning.app.commontests.data.CategoryData.categoryListWithId;
import static com.learning.app.commontests.data.CategoryData.categoryWithId;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class CategoryServiceTest
{
    private CategoryService categoryService;
    private CategoryRepository categoryRepository;
    private Validator validator;

    @Before
    public void setUp()
    {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        categoryRepository = mock(CategoryRepository.class);

        categoryService = new CategoryServiceImpl();
        ((CategoryServiceImpl) categoryService).validator = validator;
        ((CategoryServiceImpl) categoryService).categoryRepository = categoryRepository;
    }


    @Test(expected = FieldInvalidException.class)
    public void addCategoryWithNullName_ShouldThrowException() {
        categoryService.add(new Category(null));
    }

    @Test(expected = FieldInvalidException.class)
    public void addCategoryWithShortName_ShouldThrowException() {
        categoryService.add(new Category("A"));
    }

    @Test(expected = FieldInvalidException.class)
    public void addCategoryWithLongName_ShouldThrowException() {
        categoryService.add(new Category("This is a long name that will cause an exception to be thrown"));
    }

    @Test
    public void addValidCategory_ShouldReturnPersistedCategory()
    {
        Category newCategory = new Category("Project Management");
        when(categoryRepository.add(newCategory))
                .thenReturn(categoryWithId("Project Management", 1L));

        Category categoryAdded = categoryService.add(newCategory);

        assertThat(categoryAdded.getId(), is(equalTo(1L)));
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCategoryWithNullName_ShouldThrowException() {
        categoryService.update(new Category(null));
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCategoryWithShortName_ShouldThrowException() {
        categoryService.update(new Category("A"));
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCategoryWithLongName_ShouldThrowException() {
        categoryService.update(new Category("This is a long name that will cause an exception to be thrown"));
    }

    @Test(expected = CategoryNotFoundException.class)
    public void updateNonExistingCategory_ShouldThrowException()
    {
        when(categoryRepository.idExists(1L)).thenReturn(false);

        categoryService.update(categoryWithId("Project Management", 1L));
    }

    @Test
    public void updateValidCategory_ShouldCallUpdateInRepository() {
        when(categoryRepository.idExists(1L)).thenReturn(true);

        categoryService.update(categoryWithId("Project Management", 1L));

        verify(categoryRepository).update(categoryWithId("Project Management", 1L));
    }

    @Test
    public void findCategoryById_ShouldReturnCorrectCategory() {
        Category category = categoryWithId("Project Management", 1L);
        when(categoryRepository.findById(1L)).thenReturn(category);

        Category categoryFound = categoryService.findById(1L);
        assertThat(categoryFound, is(notNullValue()));
        assertThat(categoryFound.getId(), is(equalTo(1L)));
        assertThat(categoryFound.getName(), is(equalTo(category.getName())));
    }

    @Test(expected = CategoryNotFoundException.class)
    public void findCategoryById_ShouldThrowExceptionIfNotFound()
    {
        when(categoryRepository.findById(999L)).thenReturn(null);

        categoryService.findById(999L);
    }

    @Test
    public void findAll_ShouldReturn3()
    {
        when(categoryRepository.findAll()).thenReturn(categoryListWithId());

        List<Category> categoriesFound = categoryService.findAll();

        assertEquals(categoriesFound.size(), 3);
    }

    @Test
    public void deleteExistingCategory_ShouldCallDeleteInRepository()
    {
        when(categoryRepository.idExists(1L)).thenReturn(true);

        categoryService.deleteById(1L);

        verify(categoryRepository).delete(1L);
    }

    @Test(expected = CategoryNotFoundException.class)
    public void deleteNonExistingCategory_ShouldThrowException()
    {
        categoryService.deleteById(999L);
    }
}