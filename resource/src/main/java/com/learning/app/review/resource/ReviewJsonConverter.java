package com.learning.app.review.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.learning.app.common.json.EntityJsonConverter;
import com.learning.app.common.json.JsonReader;
import com.learning.app.course.model.Course;
import com.learning.app.review.model.Review;
import com.learning.app.user.model.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReviewJsonConverter implements EntityJsonConverter<Review>
{
    @Override
    public Review convertFrom(String json)
    {
        //Convert json String in JsonObject
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        //Create review object with correct fields
        Review review = new Review();
        review.setComment(JsonReader.getStringOrNull(jsonObject, "comment"));
        review.setRating(JsonReader.getIntegerOrNull(jsonObject, "rating"));

        //Create user object with correct id and set user
        User user = new User();
        user.setId(JsonReader.getLongOrNull(jsonObject, "userId"));
        review.setUser(user);

        //Create course object with correct id and set course
        Course course = new Course();
        course.setId(JsonReader.getLongOrNull(jsonObject, "courseId"));
        review.setCourse(course);

        return review;
    }

    @Override
    public JsonElement convertToJsonElement(Review review)
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", review.getId());
        jsonObject.addProperty("rating", review.getRating());
        jsonObject.addProperty("comment", review.getComment());
        jsonObject.addProperty("createdAt", review.getCreatedAt().format(DATE_FORMATTER));
        jsonObject.add("user", getJsonElementFromUser(review.getUser()));
        jsonObject.add("course", getJsonElementFromCourse(review.getCourse()));

        return jsonObject;
    }

    public JsonElement getJsonElementFromUser(User user)
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", user.getId());
        jsonObject.addProperty("name", user.getName());
        jsonObject.addProperty("email", user.getEmail());

        return jsonObject;
    }

    public JsonElement getJsonElementFromCourse(Course course)
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", course.getId());
        jsonObject.addProperty("name", course.getName());

        return jsonObject;
    }
}
