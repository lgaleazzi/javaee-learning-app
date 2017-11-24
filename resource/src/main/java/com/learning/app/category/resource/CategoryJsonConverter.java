package com.learning.app.category.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.learning.app.category.model.Category;
import com.learning.app.common.json.EntityJsonConverter;
import com.learning.app.common.json.JsonReader;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryJsonConverter implements EntityJsonConverter<Category>
{
    @Override
    public Category convertFrom(String json)
    {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        Category category = new Category();
        category.setName(JsonReader.getStringOrNull(jsonObject, "name"));

        return category;
    }

    @Override
    public JsonElement convertToJsonElement(Category category)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", category.getId());
        jsonObject.addProperty("name", category.getName());

        return jsonObject;
    }
}
