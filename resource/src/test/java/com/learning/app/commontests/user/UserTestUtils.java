package com.learning.app.commontests.user;

import org.junit.Ignore;

/*
 * Class to generate json for tests of UserResource
 */

@Ignore
public class UserTestUtils
{
    private UserTestUtils() {
    }

    public static String getJsonWithPassword(String password) {
        return String.format("{\"password\":\"%s\"}", password);
    }

    public static String getJsonWithEmailAndPassword(String email, String password) {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }
}
