package com.learning.app.user.resource;

import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.commontests.utils.ResourceDefinitions;
import com.learning.app.user.exception.UserNotFoundException;
import com.learning.app.user.model.User;
import com.learning.app.user.model.filter.UserFilter;
import com.learning.app.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.learning.app.commontests.data.UserData.*;
import static com.learning.app.commontests.user.UserTestUtils.*;
import static com.learning.app.commontests.utils.FileTestNameUtils.*;
import static com.learning.app.commontests.utils.JsonTestUtils.*;
import static com.learning.app.user.model.UserArgumentMatcher.userEquivalent;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserResourceTest
{
    private UserResource userResource;

    private static final String PATH_RESOURCE = ResourceDefinitions.USER.getResourceName();

    @Mock
    private UserService userService;

    @Mock
    private UriInfo uriInfo;

    @Mock
    private SecurityContext securityContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        userResource = new UserResource();

        userResource.userJsonConverter = new UserJsonConverter();
        userResource.userService = userService;
        userResource.uriInfo = uriInfo;
        userResource.securityContext = securityContext;
    }

    @Test
    public void addValidUser_ShouldReturnUserId() {
        //Set up service to return user
        when(userService.add(userEquivalent(jan()))).thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        Response response = userResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "userJan.json")));

        //Assert response status is success and response entity contains id of created user
        assertThat(response.getStatus(), is(equalTo(Response.Status.CREATED.getStatusCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addUserWithNullName_ShouldReturnError() {
        when(userService.add((User) anyObject())).thenThrow(new FieldInvalidException("name", "may not be null"));

        Response response = userResource
                .add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "userNullName.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "userErrorNullName.json"));
    }

    @Test
    public void updateValidUserAsAdmin_ShouldReturnSuccess() {
        //Security principal has admin permissions
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(true);

        Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateUserJan.json")));

        //Assert response status is Success and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        //Verify a user with correct name and id but no password is passed to UserService
        User expectedUser = userWithIdAndCreatedAt(jan(), 1L);
        expectedUser.setPassword(null);
        verify(userService).update(userEquivalent(expectedUser));
    }

    @Test
    public void updateValidUserLoggedAsUserToBeUpdated_ShouldReturnSuccess() {
        //Set up mock principal user without admin permissions
        setUpPrincipalUser(userWithIdAndCreatedAt(jan(), 1L));
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(false);

        //update logged in user
        Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateUserJan.json")));

        //Assert response status is Success and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        //Verify a user with correct name and id but no password is passed to UserService
        User expectedUser = userWithIdAndCreatedAt(jan(), 1L);
        expectedUser.setPassword(null);
        verify(userService).update(userEquivalent(expectedUser));
    }

    @Test
    public void updateValidUserLoggedAsOtherUser_ShouldReturnError() {
        //Set up mock principal user without admin permissions
        setUpPrincipalUser(userWithIdAndCreatedAt(daniel(), 2L));
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(false);

        Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateUserJan.json")));

        //Assert response status is Forbidden
        assertThat(response.getStatus(), is(equalTo(Response.Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void updateUserWithNullName_ShouldReturnError() {
        //Security principal has admin permissions
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(true);
        //set up user with null name
        User user = userWithIdAndCreatedAt(jan(), 2L);
        user.setName(null);
        //Service should throw exception when called
        doThrow(new FieldInvalidException("name", "may not be null"))
                .when(userService)
                .update(user);

        Response response = userResource.update(2L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "userNullName.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "userErrorNullName.json"));
    }

    @Test
    public void updateUserNotFound_ShouldReturnError() {
        //Security principal has admin permissions
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(true);
        //Service should throw exception when called
        doThrow(new UserNotFoundException())
                .when(userService)
                .update(userWithIdAndCreatedAt(jan(), 2L));

        Response response = userResource.update(2L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateUserJan.json")));

        //Assert response status is Not Found
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void updateUserPasswordAsAdmin_ShouldReturnSuccess() {
        //Security principal has admin permissions
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(true);

        Response response = userResource.updatePassword(1L, getJsonWithPassword("123456"));

        //Assert response status is OK and json entity is empty string
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct password was passed to UserService
        verify(userService).updatePassword(1L, "123456");
    }

    @Test
    public void updateUserPasswordLoggedAsUserToBeUpdated_ShouldReturnSuccess() {
        //Set up principal user without admin permissions
        setUpPrincipalUser(userWithIdAndCreatedAt(jan(), 1L));
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(false);

        Response response = userResource.updatePassword(1L, getJsonWithPassword("123456"));

        //Assert response status is OK and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct password was passed to UserService
        verify(userService).updatePassword(1L, "123456");
    }

    @Test
    public void updateUserPasswordLoggedAsOtherUser_ShouldReturnError() {
        //Set up principal user without admin permissions
        setUpPrincipalUser(userWithIdAndCreatedAt(daniel(), 2L));
        when(securityContext.isUserInRole(User.Role.ADMIN.name())).thenReturn(false);

        Response response = userResource.updatePassword(1L, getJsonWithPassword("123456"));

        //Assert response status is Forbidden
        assertThat(response.getStatus(), is(equalTo(Response.Status.FORBIDDEN.getStatusCode())));
    }

    @Test
    public void findUserById_ShouldReturnCorrectUser() {
        //Set up service to return correct user
        when(userService.findById(1L)).thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        Response response = userResource.findById(1L);

        //Assert response status is OK and response entity contains correct user data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "userJanFound.json"));
    }

    @Test
    public void findUserByIdNotFound_ShouldReturnError() {
        //Set up service to throw exception
        when(userService.findById(1L)).thenThrow(new UserNotFoundException());

        Response response = userResource.findById(1L);

        //Assert response status is Not Found
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void findUserByEmailAndPassword_ShouldReturnCorrectUser() {
        //Set up service to return user
        when(userService.findByEmailAndPassword(userAdmin().getEmail(), userAdmin().getPassword())).thenReturn(
                userWithIdAndCreatedAt(userAdmin(), 1L));

        Response response = userResource.findByEmailAndPassword(getJsonWithEmailAndPassword(userAdmin().getEmail(),
                userAdmin().getPassword()));

        //Assert response status is OK and response entity contains correct user data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "userAdminFound.json"));
    }

    @Test
    public void findUserByEmailAndPasswordNotFound_ShouldReturnError() {
        //Set up service to throw exception
        when(userService.findByEmailAndPassword(userAdmin().getEmail(), userAdmin().getPassword())).thenThrow(
                new UserNotFoundException());

        Response response = userResource.findByEmailAndPassword(getJsonWithEmailAndPassword(userAdmin().getEmail(),
                userAdmin().getPassword()));

        //Assert response status is Not Found
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByFilterNoFilter_ShouldReturn3Users() {
        //Create list of users with id
        List<User> users = new ArrayList<>();
        List<User> allUsers = userList();
        for (int i = 0; i < allUsers.size(); i++) {
            users.add(userWithIdAndCreatedAt(allUsers.get(i), new Long(i + 1)));
        }

        //Return empty Map as uri query parameters
        MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);

        //Return paginated list of users when service is called
        when(userService.findByFilter((UserFilter) anyObject())).thenReturn(
                new PaginatedData<User>(users.size(), users));

        Response response = userResource.findByFilter();

        //Assert response status is OK and response entity contains correct user data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, "usersAllInOnePage.json"));
    }

    //Set up a mock principal user to use in tests
    private void setUpPrincipalUser(User user) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
    }
}