package com.learning.app.course.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.learning.app.category.model.Category;
import com.learning.app.common.json.EntityJsonConverter;
import com.learning.app.common.json.JsonReader;
import com.learning.app.course.model.Course;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CourseJsonConverter implements EntityJsonConverter<Course>
{
    @Override
    public Course convertFrom(String json)
    {
        //Convert json String to JsonObject
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        //Create course object and set fields
        Course course = new Course();
        course.setName(JsonReader.getStringOrNull(jsonObject, "name"));
        course.setDescription(JsonReader.getStringOrNull(jsonObject, "description"));
        course.setUrl(JsonReader.getStringOrNull(jsonObject, "url"));

        //Create category object and add to course
        Category category = new Category();
        category.setId(JsonReader.getLongOrNull(jsonObject, "categoryId"));
        course.setCategory(category);

        return course;
    }

    @Override
    public JsonElement convertToJsonElement(Course course)
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", course.getId());
        jsonObject.addProperty("name", course.getName());
        jsonObject.addProperty("url", course.getUrl());
        jsonObject.addProperty("description", course.getDescription());
        jsonObject.add("category", getCategoryAsJsonElement(course.getCategory()));

        return jsonObject;
    }

    public JsonElement getCategoryAsJsonElement(Category category)
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", category.getId());
        jsonObject.addProperty("name", category.getName());

        return jsonObject;
    }
}
