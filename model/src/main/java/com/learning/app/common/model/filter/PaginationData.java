package com.learning.app.common.model.filter;

/*
* Class to set data related to pagination and ordering
*/

public class PaginationData
{
    private int firstResult;
    private int maxResults;
    private String orderField;
    private OrderMode orderMode;

    public enum OrderMode
    {
        ASCENDING, DESCENDING
    }

    public PaginationData(int firstResult, int maxResults, String orderField,
                          OrderMode orderMode)
    {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        this.orderField = orderField;
        this.orderMode = orderMode;
    }

    public int getFirstResult()
    {
        return firstResult;
    }

    public int getMaxResults()
    {
        return maxResults;
    }

    public String getOrderField()
    {
        return orderField;
    }

    public OrderMode getOrderMode()
    {
        return orderMode;
    }

    public boolean isAscending()
    {
        return OrderMode.ASCENDING.equals(orderMode);
    }

    @Override
    public String toString()
    {
        return "PaginationData [firstResult=" + firstResult + ", maxResults=" + maxResults + ", orderField="
                + orderField + ", orderMode=" + orderMode + "]";
    }
}
