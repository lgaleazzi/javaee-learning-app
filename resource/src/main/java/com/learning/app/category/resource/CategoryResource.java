package com.learning.app.category.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.learning.app.category.exception.CategoryNotFoundException;
import com.learning.app.category.model.Category;
import com.learning.app.category.service.CategoryService;
import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.json.JsonUtils;
import com.learning.app.common.json.JsonWriter;
import com.learning.app.common.json.OperationResultJsonWriter;
import com.learning.app.common.model.OperationResult;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.ResourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.learning.app.common.model.StandardsOperationResult.getOperationResultInvalidField;
import static com.learning.app.common.model.StandardsOperationResult.getOperationResultNotFound;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class CategoryResource
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

    @Inject
    CategoryService categoryService;

    @Inject
    CategoryJsonConverter categoryJsonConverter;

    @GET
    public Response findAll()
    {
        logger.debug("Find all categories");

        List<Category> categories = categoryService.findAll();
        logger.debug("Found {} categories", categories.size());

        //generate paginated data and convert to json
        JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(
                new PaginatedData<Category>(categories.size(), categories), categoryJsonConverter
        );

        return Response
                .status(Response.Status.OK)
                .entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id)
    {
        logger.debug("Find category: {}", id);
        Response.ResponseBuilder responseBuilder;

        try
        {
            Category category = categoryService.findById(id);
            String categoryJson = new Gson().toJson(category);
            responseBuilder = Response
                    .status(Response.Status.OK)
                    .entity(categoryJson);
            logger.debug("Category found: {}", category);
        } catch (CategoryNotFoundException e)
        {
            logger.error("No category found for id", id);
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
        }

        return responseBuilder.build();
    }

    @POST
    public Response add(String body)
    {
        logger.debug("Adding a new category with body {}", body);
        Category category = categoryJsonConverter.convertFrom(body);

        Response.Status responseStatus = Response.Status.CREATED;
        OperationResult result;
        try
        {
            category = categoryService.add(category);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
        } catch (FieldInvalidException e)
        {
            logger.error("One of the fields of the category is not valid", e);
            responseStatus = Response.Status.BAD_REQUEST;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);

        }

        logger.debug("Returning the operation result after adding category: {}", result);
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, String body)
    {
        logger.debug("Updating the category {} with body {}", id, body);
        Category category = categoryJsonConverter.convertFrom(body);
        category.setId(id);

        Response.Status responseStatus = Response.Status.OK;
        OperationResult result;

        try
        {
            categoryService.update(category);
            result = OperationResult.success();
        } catch (FieldInvalidException e)
        {
            logger.error("One of the field of the category is not valid", e);
            responseStatus = Response.Status.BAD_REQUEST;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (CategoryNotFoundException e)
        {
            logger.error("No category found for the given id", e);
            responseStatus = Response.Status.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating category: {}", result);
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id)
    {
        logger.debug("Deleting category with id", id);

        Response.Status responseStatus = Response.Status.OK;
        OperationResult result;

        try
        {
            categoryService.deleteById(id);
            result = OperationResult.success();
        } catch (CategoryNotFoundException e)
        {
            logger.error("No category found for the given id", e);
            responseStatus = Response.Status.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after deleting category: {}");
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

}
