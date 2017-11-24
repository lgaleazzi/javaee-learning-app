package com.learning.app.category.resource;

import com.learning.app.category.exception.CategoryNotFoundException;
import com.learning.app.category.model.Category;
import com.learning.app.category.service.CategoryService;
import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.commontests.utils.ResourceDefinitions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;

import static com.learning.app.commontests.data.CategoryData.categoryWithId;
import static com.learning.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.learning.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.learning.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class CategoryResourceTest
{
    private CategoryResource categoryResource;

    private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();

    @Mock
    private CategoryService categoryService;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        categoryResource = new CategoryResource();

        categoryResource.categoryService = categoryService;
        categoryResource.categoryJsonConverter = new CategoryJsonConverter();
    }

    @Test
    public void addValidCategory_ShouldReturnCategoryId() {
        //Set up service to return category when called
        when(categoryService.add(new Category("Coding"))).thenReturn(categoryWithId("Coding", 1L));

        Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "newCategory.json")));

        //Assert response status is Created and response entity contains id of created category
        assertThat(response.getStatus(), is(equalTo(Response.Status.CREATED.getStatusCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addCategoryWithNullName_ShouldReturnError() {
        //Set up service to throw exception when called
        when(categoryService.add(new Category())).thenThrow(new FieldInvalidException("name", "may not be null"));

        Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "categoryWithNullName.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "categoryErrorNullName.json"));
    }

    @Test
    public void updateValidCategory_ShouldReturnSuccess() {
        Response response = categoryResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));

        //Assert response status is OK and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct category is passed to CategoryService
        verify(categoryService).update(categoryWithId("Coding", 1L));
    }

    @Test
    public void updateCategoryWithNullName_ShouldReturnError() {
        //Throw exception when service is called
        doThrow(new FieldInvalidException("name", "may not be null"))
                .when(categoryService)
                .update(categoryWithId(null, 1L)
                );

        Response response = categoryResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "categoryErrorNullName.json"));
    }

    @Test
    public void updateCategoryNotFound_ShouldReturnError() {
        //Throw exception when service is called
        doThrow(new CategoryNotFoundException())
                .when(categoryService)
                .update(categoryWithId("Coding", 2L));

        Response response = categoryResource.update(2L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));

        //Assert status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "categoryNotFound.json"));
    }

    @Test
    public void findCategory_ShouldReturnCorrectCategory() {
        //Set up service to return category when called
        when(categoryService.findById(1L)).thenReturn(categoryWithId("Coding", 1L));

        Response response = categoryResource.findById(1L);

        //Assert response status is OK and response entity contains correct category data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "categoryFound.json"));
    }

    @Test
    public void findCategoryNotFound_ShouldReturnError() {
        //Set up service to throw exception when called
        when(categoryService.findById(1L)).thenThrow(new CategoryNotFoundException());

        Response response = categoryResource.findById(1L);

        //Assert status response is Not Found
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void findAllNoCategory_ShouldReturnEmptyList() {
        //Set up service to return empty list
        when(categoryService.findAll()).thenReturn(new ArrayList<>());

        Response response = categoryResource.findAll();

        //Assert response status is OK and response entity contains empty list
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "emptyListOfCategories.json"));
    }

    @Test
    public void findAllTwoCategories_ShouldReturn2Categories() {
        //Set up service to return two categories
        when(categoryService.findAll()).thenReturn(
                Arrays.asList(
                        categoryWithId("Coding", 1L),
                        categoryWithId("Languages", 2L)
                )
        );

        Response response = categoryResource.findAll();

        //Assert response status is OK and response entity contains correct category data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "twoCategories.json"));
    }

    @Test
    public void deleteValidCategory_ShouldReturnSuccess() throws Exception
    {
        Response response = categoryResource.delete(1L);

        //Assert response status is OK and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct id was passed to CategoryService
        verify(categoryService).deleteById(1L);
    }

    @Test
    public void deleteCategoryNotFound_ShouldReturnError() throws Exception
    {
        //Throw exception when service is called
        doThrow(CategoryNotFoundException.class).when(categoryService).deleteById(1L);

        Response response = categoryResource.delete(1L);

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "categoryNotFound.json"));
    }
}