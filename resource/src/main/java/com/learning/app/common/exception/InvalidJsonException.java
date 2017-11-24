package com.learning.app.common.exception;


public class InvalidJsonException extends RuntimeException
{
    public InvalidJsonException(String message)
    {
        super(message);
    }

    public InvalidJsonException(Throwable throwable)
    {
        super(throwable);
    }
}
