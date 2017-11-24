package com.learning.app.user.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.learning.app.common.json.EntityJsonConverter;
import com.learning.app.common.json.JsonReader;
import com.learning.app.user.model.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserJsonConverter implements EntityJsonConverter<User>
{
    @Override
    public User convertFrom(String json)
    {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        User user = new User();
        user.setName(JsonReader.getStringOrNull(jsonObject, "name"));
        user.setEmail(JsonReader.getStringOrNull(jsonObject, "email"));
        user.setPassword(JsonReader.getStringOrNull(jsonObject, "password"));

        return user;
    }

    @Override
    public JsonElement convertToJsonElement(User user)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getId());
        jsonObject.addProperty("name", user.getName());
        jsonObject.addProperty("email", user.getEmail());
        jsonObject.addProperty("createdAt", user.getCreatedAt().format(DATE_FORMATTER));

        JsonArray roles = new JsonArray();
        for (User.Role role : user.getRoles())
        {
            roles.add(new JsonPrimitive(role.toString()));
        }
        jsonObject.add("roles", roles);

        return jsonObject;
    }
}
