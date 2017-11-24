package com.learning.app.user.service;

import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.user.exception.UserNotFoundException;
import com.learning.app.user.model.User;
import com.learning.app.user.model.filter.UserFilter;
import com.learning.app.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Arrays;

import static com.learning.app.commontests.data.UserData.jan;
import static com.learning.app.commontests.data.UserData.userWithEncryptedPassword;
import static com.learning.app.commontests.data.UserData.userWithIdAndCreatedAt;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

public class UserServiceImplTest
{
    private UserService userService;
    private UserRepository userRepository;
    private Validator validator;

    @Before
    public void setUp()
    {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        userRepository = mock(UserRepository.class);

        MockitoAnnotations.initMocks(this);

        userService = new UserServiceImpl();
        ((UserServiceImpl)userService).validator = validator;
        ((UserServiceImpl)userService).userRepository = userRepository;
    }

    @Test
    public void addValidUser_ShouldReturnPersistedUser()
    {
        when(userRepository.add(jan()))
                .thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        User userAdded = userService.add(jan());

        assertThat(userAdded.getId(), is(equalTo(1L)));
    }

    @Test(expected = FieldInvalidException.class)
    public void addUserWithNullName_ShouldThrowException() {
        User user = jan();
        user.setName(null);
        userService.add(user);
    }

    @Test(expected = FieldInvalidException.class)
    public void addUserWithShortName_ShouldThrowException() {
        User user = jan();
        user.setName("A");
        userService.add(user);
    }

    @Test(expected = FieldInvalidException.class)
    public void addUserWithLongName_ShouldThrowException() {
        User user = jan();
        user.setName("This is a long name that will cause an exception to be thrown");
        userService.add(user);
    }

    @Test(expected = FieldInvalidException.class)
    public void updateUserWithNullName_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        User user = userWithIdAndCreatedAt(jan(), 1L);
        user.setName(null);
        userService.update(user);
    }

    @Test(expected = FieldInvalidException.class)
    public void updateUserWithShortName_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        User user = userWithIdAndCreatedAt(jan(), 1L);
        user.setName("A");
        userService.update(user);
    }

    @Test(expected = FieldInvalidException.class)
    public void updateUserWithLongName_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        User user = userWithIdAndCreatedAt(jan(), 1L);
        user.setName("This is a long name that will cause an exception to be thrown");
        userService.update(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void updateNonExistentUser_ShouldThrowException() throws Exception {
        User user = userWithIdAndCreatedAt(jan(), 1L);
        when(userRepository.findById(1L)).thenReturn(null);

        userService.update(user);
    }

    @Test
    public void findUserById_ShouldReturnCorrectUser() {
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        User userFound = userService.findById(1L);

        assertThat(userFound, is(notNullValue()));
        assertThat(userFound.getName(), is(equalTo(jan().getName())));
    }

    @Test(expected = UserNotFoundException.class)
    public void findUserById_ShouldThrowExceptionIfNotFound()
    {
        when(userRepository.findById(999L)).thenReturn(null);

        userService.findById(999L);
    }

    @Test
    public void findUserByEmail_ShouldReturnCorrectUser() {
        when(userRepository.findByEmail("Jan@test.com")).thenReturn(userWithIdAndCreatedAt(jan(), 1L));

        User userFound = userService.findByEmail("Jan@test.com");

        assertThat(userFound, is(notNullValue()));
        assertThat(userFound.getName(), is(equalTo(jan().getName())));
    }

    @Test(expected = UserNotFoundException.class)
    public void findUserByEmail_ShouldThrowExceptionIfNotFound()
    {
        when(userRepository.findByEmail("...")).thenReturn(null);

        userService.findByEmail("...");
    }

    @Test(expected = UserNotFoundException.class)
    public void findUserByAndPasswordEmailWithInvalidPassword() throws UserNotFoundException {
        User user = jan();
        user.setPassword("1111");

        User userReturned = userWithIdAndCreatedAt(jan(), 1L);
        userReturned = userWithEncryptedPassword(userReturned);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(userReturned);

        userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
    }

    @Test
    public void findUserByAndPasswordEmail_ShouldReturnCorrectUser() throws UserNotFoundException {
        User user = jan();

        User userReturned = userWithIdAndCreatedAt(jan(), 1L);
        userReturned = userWithEncryptedPassword(userReturned);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(userReturned);

        user = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(user, is(notNullValue()));
        assertThat(user.getName(), is(equalTo(jan().getName())));
    }

    @Test
    public void findUserByFilter_ShouldReturnPaginatedData() {
        PaginatedData<User> users = new PaginatedData<>(1,
                Arrays.asList(userWithIdAndCreatedAt(jan(), 1L)));
        when(userRepository.findByFilter((UserFilter) anyObject())).thenReturn(users);

        PaginatedData<User> usersReturned = userService.findByFilter(new UserFilter());
        assertThat(usersReturned.getNumberOfRows(), is(equalTo(1)));
        assertThat(usersReturned.getRow(0).getName(), is(equalTo(jan().getName())));
    }

    @Test
    public void deleteExistingUser_ShouldCallDeleteInRepository()
    {
        when(userRepository.idExists(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).delete(1L);
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteNonExistingCategory_ShouldThrowException()
    {
        userService.delete(999L);
    }
}