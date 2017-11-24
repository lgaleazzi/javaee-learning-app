package com.learning.app.common.exception;


public class FieldInvalidException extends RuntimeException
{
    private String fieldName;

    public FieldInvalidException(String fieldName, String message)
    {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    @Override
    public String toString()
    {
        return "FieldInvalidException [fieldName=" + fieldName + "]";
    }
}
