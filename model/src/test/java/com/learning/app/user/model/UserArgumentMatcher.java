package com.learning.app.user.model;

import org.junit.Ignore;
import org.mockito.ArgumentMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;

@Ignore
public class UserArgumentMatcher extends ArgumentMatcher<User>
{
    private User expectedUser;

    public UserArgumentMatcher(User expectedUser) {
        this.expectedUser = expectedUser;
    }

    public static User userEquivalent(User expectedUser) {
        return argThat(new UserArgumentMatcher(expectedUser));
    }

    @Override
    public boolean matches(Object object) {
        if(!(object instanceof User))
        {
            return false;
        }
        User actualUser = (User) object;

        assertThat(actualUser.getId(), is(equalTo(expectedUser.getId())));
        assertThat(actualUser.getName(), is(equalTo(expectedUser.getName())));
        assertThat(actualUser.getEmail(), is(equalTo(expectedUser.getEmail())));
        assertThat(actualUser.getPassword(), is(equalTo(expectedUser.getPassword())));

        return true;
    }
}
