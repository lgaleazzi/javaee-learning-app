package com.learning.app.category.repository;

import com.learning.app.category.model.Category;
import com.learning.app.commontests.repository.TestBaseRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.learning.app.commontests.data.CategoryData.*;
import static com.learning.app.commontests.data.CategoryData.categoryList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class CategoryRepositoryTest extends TestBaseRepository
{
    private CategoryRepository categoryRepository;

    @Before
    public void setUp()
    {
        initializeTestDB();

        categoryRepository = new CategoryRepository();
        categoryRepository.em = em;
    }

    @After
    public void tearDown()
    {
        closeEntityManager();
    }

    @Test
    public void add_ShouldPersistCategory() throws Exception
    {
        Category addedCategory = transactionExecutor.executeCommandWithResult(
                () -> categoryRepository.add(new Category("Chess"))
        );

        assertThat(addedCategory.getId(), is(notNullValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void add_WithExistingNameFails() throws Exception
    {
        transactionExecutor.executeCommandWithResult(
                () -> categoryRepository.add(new Category("Chess"))
        );

        transactionExecutor.executeCommandWithResult(
                () -> categoryRepository.add(new Category("Chess"))
        );
    }

    @Test
    public void update_ShouldPersistCategory() throws Exception
    {
        Category categoryAdded = transactionExecutor.executeCommandWithResult(
                () -> categoryRepository.add(new Category("Soft Skills"))
        );

        categoryAdded.setName("Communication");
        transactionExecutor.executeCommandWithNoResult(
                () -> categoryRepository.update(categoryAdded)
        );

        Category categoryUpdated = categoryRepository.findById(categoryAdded.getId());
        assertThat(categoryUpdated.getName(), is(equalTo("Communication")));
    }

    @Test
    public void findAll_ShouldReturn3() throws Exception
    {
        transactionExecutor.executeCommandWithNoResult(
                () -> categoryList().forEach(categoryRepository::add)
        );

        assertEquals(categoryRepository.findAll().size(), 3);
    }

    @Test
    public void findById_ShouldReturnNullIfIdNotExist() throws Exception
    {
        Category category = categoryRepository.findById(999L);
        assertThat(category, is(nullValue()));
    }

    @Test
    public void findById_ShouldReturnCategoryIfExist() throws Exception
    {
        Category addedCategory = transactionExecutor.executeCommandWithResult(
                () -> categoryRepository.add(new Category("Chess"))
        );

        Category categoryFoundById = categoryRepository.findById(addedCategory.getId());
        assertThat(categoryFoundById, is(equalTo(addedCategory)));
    }

    @Test
    public void delete_ShouldRemoveCategory() throws Exception
    {
        Category addedCategory = transactionExecutor.executeCommandWithResult(
                () -> categoryRepository.add(new Category("Chess"))
        );

        transactionExecutor.executeCommandWithNoResult(
                () -> categoryRepository.delete(addedCategory.getId())
        );

        assertThat(categoryRepository.findById(addedCategory.getId()), is(nullValue()));
    }











}