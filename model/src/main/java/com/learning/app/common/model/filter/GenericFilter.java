package com.learning.app.common.model.filter;

/*
 * Parent class of filter classes
 * Enable filtering paginated data with pre-defined parameters
 */

public class GenericFilter
{
    private PaginationData paginationData;

    public GenericFilter()
    {
    }

    public GenericFilter(PaginationData paginationData)
    {
        this.paginationData = paginationData;
    }

    public void setPaginationData(PaginationData paginationData)
    {
        this.paginationData = paginationData;
    }

    public PaginationData getPaginationData()
    {
        return paginationData;
    }

    public boolean hasPaginationData()
    {
        return getPaginationData() != null;
    }

    public boolean hasOrderField()
    {
        return hasPaginationData() && getPaginationData().getOrderField() != null;
    }

    @Override
    public String toString()
    {
        return "GenericFilter [paginationData=" + paginationData + "]";
    }
}
