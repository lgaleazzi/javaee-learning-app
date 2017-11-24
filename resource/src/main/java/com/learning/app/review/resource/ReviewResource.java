package com.learning.app.review.resource;

import com.google.gson.JsonElement;
import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.json.JsonUtils;
import com.learning.app.common.json.JsonWriter;
import com.learning.app.common.json.OperationResultJsonWriter;
import com.learning.app.common.model.OperationResult;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.ResourceMessage;
import com.learning.app.course.exception.CourseNotFoundException;
import com.learning.app.review.exception.ReviewNotFoundException;
import com.learning.app.review.model.Review;
import com.learning.app.review.model.filter.ReviewFilter;
import com.learning.app.review.service.ReviewService;
import com.learning.app.user.exception.UserNotFoundException;
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

@Path("/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class ReviewResource
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("review");

    @Inject
    ReviewService reviewService;

    @Inject
    ReviewJsonConverter reviewJsonConverter;

    @Context
    UriInfo uriInfo;

    @GET
    public Response findByFilter()
    {
        //get filter from URL parameters
        ReviewFilter reviewFilter = new ReviewFilterExtractorFromURL(uriInfo).getFilter();
        logger.debug("Finding reviews using filter: {}", reviewFilter);

        //get paginated data
        PaginatedData<Review> reviews = reviewService.findByFilter(reviewFilter);
        logger.debug("Found {} reviews", reviews.getNumberOfRows());

        //create JsonElement from paginated data
        JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(reviews,
                reviewJsonConverter);

        return Response
                .status(Response.Status.OK)
                .entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id)
    {
        logger.debug("Find review: {}", id);
        Response.ResponseBuilder responseBuilder;

        try
        {
            Review review = reviewService.findById(id);
            //Convert Review Object to json and create OperationResult object
            OperationResult result = OperationResult.success(reviewJsonConverter.convertToJsonElement(review));

            responseBuilder = Response
                    .status(Response.Status.OK)
                    .entity(OperationResultJsonWriter.toJson(result));
            logger.debug("Review found: {}", review);
        } catch (ReviewNotFoundException e)
        {
            logger.error("No review found for id", id);
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
        }

        return responseBuilder.build();
    }

    @POST
    public Response add(String body)
    {
        logger.debug("Adding a new review with body {}", body);
        Review review = reviewJsonConverter.convertFrom(body);

        Response.Status status = Response.Status.CREATED;
        OperationResult result;

        try
        {
            review = reviewService.add(review);
            //Create OperationResult object for added review
            result = OperationResult.success(JsonUtils.getJsonElementWithId(review.getId()));
        } catch (FieldInvalidException e)
        {
            //set status to bad request if a field is invalid and create OperationResult object with exception
            status = Response.Status.BAD_REQUEST;
            logger.error("One of the fields of the review is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (CourseNotFoundException e)
        {
            //set status to not found if the associated course doesn't exist and create OperationResult object with
            // exception
            status = Response.Status.NOT_FOUND;
            logger.error("No course found for review", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "course");
        } catch (UserNotFoundException e)
        {
            //set status to not found if the associated user doesn't exist and create OperationResult object with
            // exception
            status = Response.Status.NOT_FOUND;
            logger.error("No user found for review", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "user");
        }

        logger.debug("Returning the operation result after adding review: {}", result);

        return Response
                .status(status)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, String body)
    {
        logger.debug("Updating the review {} with body {}", id, body);
        //Create Review object from json
        Review review = reviewJsonConverter.convertFrom(body);
        review.setId(id);

        //set status to OK
        Response.Status status = Response.Status.OK;
        OperationResult result;
        try
        {
            //Update review
            reviewService.update(review);
            //Set result to success
            result = OperationResult.success();
        } catch (ReviewNotFoundException e)
        {
            //set status to not found if review cannot be found
            status = Response.Status.NOT_FOUND;
            logger.error("No review found for the given id", e);
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        } catch (FieldInvalidException e)
        {
            //set status to bad request if a field is invalid and create OperationResult object with exception
            status = Response.Status.BAD_REQUEST;
            logger.error("One of the fields of the review is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (CourseNotFoundException e)
        {
            //set status to not found if the associated course doesn't exist and create OperationResult object with
            // exception
            status = Response.Status.NOT_FOUND;
            logger.error("No course found for review", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "course");
        } catch (UserNotFoundException e)
        {
            //set status to not found if the associated user doesn't exist and create OperationResult object with
            // exception
            status = Response.Status.NOT_FOUND;
            logger.error("No user found for review", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "user");
        }

        logger.debug("Returning the operation result after updating review: {}", result);

        return Response
                .status(status)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id)
    {
        logger.debug("Deleting review with id", id);

        //Set status to OK
        Response.Status responseStatus = Response.Status.OK;
        OperationResult result;

        try
        {
            //Delete review and create OperationResult object
            reviewService.deleteById(id);
            result = OperationResult.success();
        } catch (ReviewNotFoundException e)
        {
            //Set status to not found if review is not found
            logger.error("No review found for the given id", e);
            responseStatus = Response.Status.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after deleting course: {}");
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }
}
