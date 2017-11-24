package com.learning.app.common.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class PasswordEncryption
{
    private PasswordEncryption()
    {
    }

    public static String encryptPassword(String password)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e)
        {
            throw new IllegalArgumentException(e);
        }
        md.update(password.getBytes());
        return Base64.getMimeEncoder().encodeToString(md.digest());
    }
}
