package com.learning.app.course.resource;

import com.google.gson.JsonElement;
import com.learning.app.category.exception.CategoryNotFoundException;
import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.json.JsonUtils;
import com.learning.app.common.json.JsonWriter;
import com.learning.app.common.json.OperationResultJsonWriter;
import com.learning.app.common.model.OperationResult;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.ResourceMessage;
import com.learning.app.course.exception.CourseNotFoundException;
import com.learning.app.course.model.Course;
import com.learning.app.course.model.filter.CourseFilter;
import com.learning.app.course.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static com.learning.app.common.model.StandardsOperationResult.*;

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class CourseResource
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("course");

    @Inject
    CourseService courseService;

    @Inject
    CourseJsonConverter courseJsonConverter;

    @Context
    UriInfo uriInfo;

    @GET
    public Response findByFilter()
    {
        CourseFilter courseFilter = new CourseFilterExtractorFromURL(uriInfo).getFilter();
        logger.debug("Finding courses using filter: {}", courseFilter);

        PaginatedData<Course> courses = courseService.findByFilter(courseFilter);

        logger.debug("Found {} courses", courses.getNumberOfRows());

        JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(courses,
                courseJsonConverter);

        return Response
                .status(Response.Status.OK)
                .entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id)
    {
        logger.debug("Find course: {}", id);
        Response.ResponseBuilder responseBuilder;
        try
        {
            Course course = courseService.findById(id);
            OperationResult result = OperationResult.success(courseJsonConverter.convertToJsonElement(course));
            responseBuilder = Response
                    .status(Response.Status.OK)
                    .entity(OperationResultJsonWriter.toJson(result));
            logger.debug("Course found: {}", course);
        } catch (CourseNotFoundException e)
        {
            logger.error("No course found for id", id);
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
        }

        return responseBuilder.build();
    }

    @POST
    public Response add(String body)
    {
        logger.debug("Adding a new course with body {}", body);
        Course course = courseJsonConverter.convertFrom(body);

        Response.Status status = Response.Status.CREATED;
        OperationResult result;
        try
        {
            course = courseService.add(course);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(course.getId()));
        } catch (FieldInvalidException e)
        {
            status = Response.Status.BAD_REQUEST;
            logger.error("One of the fields of the course is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (CategoryNotFoundException e)
        {
            status = Response.Status.NOT_FOUND;
            logger.error("No category found for course", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "category");
        }

        logger.debug("Returning the operation result after adding course: {}", result);
        return Response
                .status(status)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, String body)
    {
        logger.debug("Updating course {} with body {}", body);
        Course course = courseJsonConverter.convertFrom(body);
        course.setId(id);

        Response.Status status = Response.Status.OK;
        OperationResult result;
        try
        {
            courseService.update(course);
            result = OperationResult.success();
        } catch (CourseNotFoundException e)
        {
            status = Response.Status.NOT_FOUND;
            logger.error("The course was not found");
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        } catch (FieldInvalidException e)
        {
            status = Response.Status.BAD_REQUEST;
            logger.error("One of the fields of the course is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (CategoryNotFoundException e)
        {
            status = Response.Status.NOT_FOUND;
            logger.error("No category found for course", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "category");
        }

        logger.debug("Returning the operation result after updating course: {}", result);
        return Response
                .status(status)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id)
    {
        logger.debug("Deleting course with id", id);

        Response.Status responseStatus = Response.Status.OK;
        OperationResult result;

        try
        {
            courseService.deleteById(id);
            result = OperationResult.success();
        } catch (CourseNotFoundException e)
        {
            logger.error("No course found for the given id", e);
            responseStatus = Response.Status.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after deleting course: {}");
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }
}
