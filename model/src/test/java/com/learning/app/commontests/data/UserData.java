package com.learning.app.commontests.data;

import com.learning.app.common.utils.PasswordEncryption;
import com.learning.app.user.model.User;
import org.junit.Ignore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Ignore
public class UserData
{
    public static List<User> userList()
    {
        return Arrays.asList(
                jan(),
                lena(),
                daniel()
        );
    }

    public static List<User> userListWithId()
    {
        return Arrays.asList(
                userWithIdAndCreatedAt(jan(), 1L),
                userWithIdAndCreatedAt(lena(), 2L),
                userWithIdAndCreatedAt(daniel(), 3L)
        );
    }

    public static User jan()
    {
        return userWithoutId("Jan");
    }

    public static User lena()
    {
        return userWithoutId("Lena");
    }

    public static User daniel()
    {
        return userWithoutId("Daniel");
    }

    public static User userAdmin()
    {
        User user = userWithoutId("Admin");
        user.setRoles(Arrays.asList(User.Role.STANDARD, User.Role.ADMIN));
        return user;
    }

    public static User userWithoutId(String name)
    {
        User user = new User();
        user.setName(name);
        user.setEmail(name + "@test.com");
        user.setPassword("123456");

        return user;
    }

    public static User userWithIdAndCreatedAt(User user, Long id)
    {
        user.setId(id);
        user.setCreatedAt(LocalDate.parse("2017-01-03", DateTimeFormatter.ISO_LOCAL_DATE));
        return user;
    }

    public static User userWithEncryptedPassword(User user)
    {
        user.setPassword(PasswordEncryption.encryptPassword(user.getPassword()));
        return user;
    }


}
