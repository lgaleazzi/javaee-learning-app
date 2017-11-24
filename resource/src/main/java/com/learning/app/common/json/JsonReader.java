package com.learning.app.common.json;


import com.google.gson.*;
import com.learning.app.common.exception.InvalidJsonException;

public class JsonReader
{
    public static JsonObject readAsJsonObject(String json)
    {
        return readJsonAs(json, JsonObject.class);
    }

    public static JsonArray readAsJsonArray(String json)
    {
        return readJsonAs(json, JsonArray.class);
    }

    public static <T> T readJsonAs(String json, Class<T> jsonClass)
    {
        if (json == null || json.trim().isEmpty())
        {
            throw new InvalidJsonException("Json String can not be null");
        }
        try
        {
            return new Gson().fromJson(json, jsonClass);
        } catch (JsonSyntaxException e)
        {
            throw new InvalidJsonException(e);
        }
    }

    public static Long getLongOrNull(JsonObject jsonObject, String propertyName)
    {
        JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property))
        {
            return null;
        }
        return property.getAsLong();
    }

    public static Integer getIntegerOrNull(JsonObject jsonObject, String propertyName)
    {
        JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property))
        {
            return null;
        }
        return property.getAsInt();
    }

    public static String getStringOrNull(JsonObject jsonObject, String propertyName)
    {
        JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property))
        {
            return null;
        }
        return property.getAsString();
    }

    public static Double getDoubleOrNull(JsonObject jsonObject, String propertyName)
    {
        JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property))
        {
            return null;
        }
        return property.getAsDouble();
    }

    private static boolean isJsonElementNull(JsonElement element)
    {
        return element == null || element.isJsonNull();
    }

}
