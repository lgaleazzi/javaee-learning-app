package com.learning.app.common.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

/*
 * Converts java.time.LocalDate to and from java.sql.Date when writing queries to the database
 */

@SuppressWarnings("UnusedDeclaration")
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date>
{

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate)
    {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date)
    {
        return date == null ? null : date.toLocalDate();
    }
}