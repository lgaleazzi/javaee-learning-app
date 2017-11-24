package com.learning.app.common.utils;

import com.learning.app.common.exception.FieldInvalidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

public class DataValidation
{
    public static <T> void validateEntityFields(Validator validator, T entity)
    {
        Set<ConstraintViolation<T>> errors = validator.validate(entity);
        Iterator<ConstraintViolation<T>> itErrors = errors.iterator();

        if (itErrors.hasNext())
        {
            ConstraintViolation<T> violation = itErrors.next();
            throw new FieldInvalidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }
}
