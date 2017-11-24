package com.learning.app.user.repository;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.filter.PaginationData;
import com.learning.app.user.model.User;
import com.learning.app.commontests.repository.TestBaseRepository;
import com.learning.app.user.model.filter.UserFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.learning.app.commontests.data.UserData.jan;
import static com.learning.app.commontests.data.UserData.userList;
import static com.learning.app.commontests.data.UserData.userWithoutId;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class UserRepositoryTest extends TestBaseRepository
{
    private UserRepository userRepository;

    @Before
    public void setUp()
    {
        initializeTestDB();

        userRepository = new UserRepository();
        userRepository.em = em;
    }

    @After
    public void tearDown()
    {
        closeEntityManager();
    }

    @Test
    public void add_ShouldPersistUser() throws Exception
    {
        User userAdded = transactionExecutor.executeCommandWithResult(
                () -> userRepository.add(userWithoutId("Jan"))
        );

        assertThat(userAdded.getId(), is(notNullValue()));
    }

    @Test
    public void update_ShouldPersistUser() throws Exception
    {
        User userAdded = transactionExecutor.executeCommandWithResult(
                () -> userRepository.add(userWithoutId("Jan"))
        );
        userAdded.setName("Johann");

        transactionExecutor.executeCommandWithNoResult(
                () -> userRepository.update(userAdded)
        );

        User userUpdated = userRepository.findById(userAdded.getId());
        assertThat(userUpdated.getName(), is(equalTo("Johann")));
    }

    @Test
    public void findAll_ShouldReturn3() throws Exception
    {
        transactionExecutor.executeCommandWithNoResult(
                () -> userList().forEach(userRepository::add)
        );

        assertEquals(userRepository.findAll().size(), 3);
    }

    @Test
    public void findByFilter_ShouldReturnFilteredAndPaginatedData()
    {

        transactionExecutor.executeCommandWithNoResult(
                () -> userList().forEach(userRepository::add)
        );

        UserFilter userFilter = new UserFilter();
        userFilter.setName("an");
        userFilter.setPaginationData(new PaginationData(0, 2, "name", PaginationData.OrderMode.ASCENDING));

        PaginatedData<User> result = userRepository.findByFilter(userFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(2)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getName(), is(equalTo("Daniel")));
        assertThat(result.getRow(1).getName(), is(equalTo("Jan")));
    }

    @Test
    public void findById_ShouldReturnNullIfIdNotExist() throws Exception
    {
        User user = userRepository.findById(999L);

        assertThat(user, is(nullValue()));
    }


    @Test
    public void findById_ShouldReturnUserIfExist() throws Exception
    {
        User userAdded = transactionExecutor.executeCommandWithResult(
                () -> userRepository.add(jan())
        );

        User userFoundById = userRepository.findById(userAdded.getId());

        assertThat(userFoundById, is(equalTo(userAdded)));
    }

    @Test
    public void findByEmail_ShouldReturnNullIfIdNotExist() throws Exception
    {
        User user = userRepository.findByEmail("...");

        assertThat(user, is(nullValue()));
    }

    @Test public void findByEmail_ShouldReturnUserIfExist() throws Exception
    {
        User userAdded = transactionExecutor.executeCommandWithResult(
                () -> userRepository.add(jan())
        );

        User userFoundByEmail = userRepository.findByEmail("Jan@test.com");

        assertThat(userFoundByEmail, is(equalTo(userAdded)));
    }

    @Test
    public void delete_ShouldRemoveUser() throws Exception
    {
        User userAdded = transactionExecutor.executeCommandWithResult(
                () -> userRepository.add(jan())
        );

        userRepository.delete(userAdded.getId());

        assertThat(userRepository.findById(userAdded.getId()), is(nullValue()));
    }
}