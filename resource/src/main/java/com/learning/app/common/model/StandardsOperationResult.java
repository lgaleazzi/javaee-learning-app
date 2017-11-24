package com.learning.app.common.model;

import com.learning.app.common.exception.FieldInvalidException;

/*
 * Helper class to build operation results for standard standard errors
 */

public class StandardsOperationResult
{
    private StandardsOperationResult()
    {
    }

    public static OperationResult getOperationResultInvalidField(ResourceMessage resourceMessage,
                                                                 FieldInvalidException ex)
    {
        return OperationResult.error(
                resourceMessage.getKeyOfInvalidField(ex.getFieldName()),
                ex.getMessage());
    }

    public static OperationResult getOperationResultNotFound(ResourceMessage resourceMessage)
    {
        return OperationResult.error(
                resourceMessage.getKeyOfResourceNotFound(),
                resourceMessage.getMessageOfResourceNotFound());
    }

    public static OperationResult getOperationResultDependencyNotFound(ResourceMessage resourceMessage, String dependency)
    {
        return OperationResult.error(
                resourceMessage.getKeyOfInvalidField(dependency),
                resourceMessage.getMessageNotFound());
    }
}
