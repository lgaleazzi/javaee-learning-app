package com.learning.app.common.json;


import com.google.gson.JsonObject;
import com.learning.app.common.model.OperationResult;

public class OperationResultJsonWriter
{
    private OperationResultJsonWriter()
    {
    }

    public static String toJson(OperationResult operationResult)
    {
        return JsonWriter.writeToString(getJsonObject(operationResult));
    }

    private static Object getJsonObject(OperationResult operationResult)
    {
        if (operationResult.isSuccess())
        {
            return getJsonSucess(operationResult);
        }
        return getJsonError(operationResult);
    }

    private static Object getJsonSucess(OperationResult operationResult)
    {
        return operationResult.getEntity();
    }

    private static JsonObject getJsonError(OperationResult operationResult)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorIdentification", operationResult.getErrorIdentification());
        jsonObject.addProperty("errorDescription", operationResult.getErrorDescription());

        return jsonObject;
    }
}
