package com.learning.app.user.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.json.JsonReader;
import com.learning.app.common.json.JsonUtils;
import com.learning.app.common.json.JsonWriter;
import com.learning.app.common.json.OperationResultJsonWriter;
import com.learning.app.common.model.OperationResult;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.ResourceMessage;
import com.learning.app.user.exception.UserNotFoundException;
import com.learning.app.user.model.User;
import com.learning.app.user.model.filter.UserFilter;
import com.learning.app.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import static com.learning.app.common.model.StandardsOperationResult.getOperationResultInvalidField;
import static com.learning.app.common.model.StandardsOperationResult.getOperationResultNotFound;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("user");

    @Inject
    UserService userService;

    @Inject
    UserJsonConverter userJsonConverter;

    @Context
    SecurityContext securityContext;

    @Context
    UriInfo uriInfo;

    @POST
    public Response add(String body)
    {
        logger.debug("Adding a new user with body {}", body);
        //Create a user object from the json body
        User user = userJsonConverter.convertFrom(body);

        Response.Status responseStatus = Response.Status.CREATED;
        OperationResult result;
        try
        {
            //if adding the user is successful, create operation result with success = true and user id
            user = userService.add(user);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(user.getId()));
        } catch (FieldInvalidException e)
        {
            //If a field is invalid, set response status to Bad Request
            responseStatus = Response.Status.BAD_REQUEST;
            logger.error("One of the fields of the user is not valid", e);
            //Create operation result with Invalid Field error message
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        }

        logger.debug("Returning the operation result after adding user: {}", result);
        return Response
                .status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id}")
    @PermitAll
    public Response update(@PathParam("id") Long id, String body)
    {
        logger.debug("Updating the user {} with body {}", id, body);

        //Check that the logged user is either an Admin, or updating his own data
        if (!securityContext.isUserInRole(User.Role.ADMIN.name()))
        {
            if (!isLoggedUser(id))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }

        //Create a user object from the json body
        User user = userJsonConverter.convertFrom(body);
        user.setId(id);

        Response.Status responseStatus = Response.Status.OK;
        OperationResult result;
        try
        {
            //if update is successful, create operation result with success = true
            userService.update(user);
            result = OperationResult.success();
        } catch (FieldInvalidException e)
        {
            //If a field is invalid, set response status to Bad Request
            responseStatus = Response.Status.BAD_REQUEST;
            logger.error("One of the fields of the user is not valid", e);
            //Create operation result with Invalid Field error message
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (UserNotFoundException e)
        {
            //If user cannot be found, set response status to Not Found
            responseStatus = Response.Status.NOT_FOUND;
            logger.error("No user found for the given id", e);
            //Create operation result with Not Found error message
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating user: {}", result);
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id}/password")
    @PermitAll
    public Response updatePassword(@PathParam("id") Long id, String body)
    {
        logger.debug("Updating the password for user {}", id);

        //Check that the logged user is either an Admin, or updating his own data
        if (!securityContext.isUserInRole(User.Role.ADMIN.name()))
        {
            if (!isLoggedUser(id))
            {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }

        Response.Status responseStatus = Response.Status.OK;
        OperationResult result;

        try
        {
            //get password from json body and update the user
            userService.updatePassword(id, getPasswordFromJson(body));
            //if update is successful, create operation result with success = true
            result = OperationResult.success();
        } catch (UserNotFoundException e)
        {
            //If user cannot be found, set response status to Not Found
            responseStatus = Response.Status.NOT_FOUND;
            logger.error("No user found for the given id", e);
            //Create operation result with Not Found error message
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating user password: {}", result);
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response findById(@PathParam("id") Long id)
    {
        logger.debug("Find user by id: {}", id);
        Response.ResponseBuilder responseBuilder;

        try
        {
            //if finding user is successful, set operation result to success = true and include user data
            User user = userService.findById(id);
            OperationResult result = OperationResult.success(userJsonConverter.convertToJsonElement(user));
            //Build response with status = OK and operation result
            responseBuilder = Response
                    .status(Response.Status.OK)
                    .entity(OperationResultJsonWriter.toJson(result));
            logger.debug("User found by id: {}", user);
        } catch (UserNotFoundException e)
        {
            logger.error("No user found for id", id);
            //if finding user fails, build response with status = not found
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
        }

        return responseBuilder.build();
    }

    @POST
    @Path("/authenticate")
    @PermitAll
    public Response findByEmailAndPassword(String body)
    {
        logger.debug("Find user by email and password");
        Response.ResponseBuilder responseBuilder;

        try
        {
            //Create a user object with the email and password from the json body
            User userWithEmailAndPassword = getUserWithEmailAndPasswordFromJson(body);
            //Find the user based on the email and password values
            User user = userService.findByEmailAndPassword(userWithEmailAndPassword.getEmail(),
                    userWithEmailAndPassword.getPassword());
            //set operation result to success = true and add user data
            OperationResult result = OperationResult.success(userJsonConverter.convertToJsonElement(user));
            //Build response with status = OK and operation result
            responseBuilder = Response
                    .status(Response.Status.OK)
                    .entity(OperationResultJsonWriter.toJson(result));
            logger.debug("User found by email/password: {}", user);
        } catch (UserNotFoundException e)
        {
            //if the user cannot be found, set the response status to Not Found
            logger.error("No user found for email/password");
            responseBuilder = Response.status(Response.Status.NOT_FOUND);
        }

        return responseBuilder.build();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    public Response findByFilter()
    {
        //Create a filter based on the uri parameters
        UserFilter userFilter = new UserFilterExtractorFromURL(uriInfo).getFilter();
        logger.debug("Finding users using filter: {}", userFilter);

        //Create a paginated user list filtered using the uri parameters
        PaginatedData<User> users = userService.findByFilter(userFilter);

        logger.debug("Found {} users", users.getNumberOfRows());

        //Convert paginated user list to json
        JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(users,
                userJsonConverter);

        return Response
                .status(Response.Status.OK)
                .entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") Long id)
    {
        logger.debug("Deleting course with id", id);

        Response.Status responseStatus = Response.Status.OK;
        OperationResult result;

        try
        {
            //if deletion is successful, create operation result with success = true
            userService.delete(id);
            result = OperationResult.success();
        } catch (UserNotFoundException e)
        {
            //If user cannot be found, set response status to Not Found
            logger.error("No user found for the given id", e);
            responseStatus = Response.Status.NOT_FOUND;
            //Create operation result with Not Found error message
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after deleting user: {}");
        return Response.status(responseStatus)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    //Compare parameter with id from logged user
    private boolean isLoggedUser(Long id)
    {
        try
        {
            //the name of the UserPrincipal is the username, here we use the email as a username
            User loggedUser = userService.findByEmail(securityContext.getUserPrincipal().getName());
            if (loggedUser.getId().equals(id))
            {
                return true;
            }
        } catch (UserNotFoundException e)
        {
            //if logged user wasn't found we can just return false
        }
        return false;
    }

    //Create a user object with the email and password from json
    private User getUserWithEmailAndPasswordFromJson(String body)
    {
        User user = new User();

        JsonObject jsonObject = JsonReader.readAsJsonObject(body);
        user.setEmail(JsonReader.getStringOrNull(jsonObject, "email"));
        user.setPassword(JsonReader.getStringOrNull(jsonObject, "password"));

        return user;
    }

    //Get the user's password from json
    private String getPasswordFromJson(String body)
    {
        JsonObject jsonObject = JsonReader.readAsJsonObject(body);
        return JsonReader.getStringOrNull(jsonObject, "password");
    }
}
