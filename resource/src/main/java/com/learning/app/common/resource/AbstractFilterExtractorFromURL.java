package com.learning.app.common.resource;

import com.learning.app.common.model.filter.PaginationData;

import javax.ws.rs.core.UriInfo;

public abstract class AbstractFilterExtractorFromURL
{
    private UriInfo uriInfo;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PER_PAGE = 10;

    public AbstractFilterExtractorFromURL(UriInfo uriInfo)
    {
        this.uriInfo = uriInfo;
    }


    protected abstract String getDefaultSortField();

    protected UriInfo getUriInfo()
    {
        return uriInfo;
    }

    //Create pagination data using uriInfo parameters
    protected PaginationData extractPaginationData()
    {
        int perPage = getPerPage();
        int firstResult = getPage() * perPage;

        String orderField;
        PaginationData.OrderMode orderMode;
        String sortField = getSortField();

        //set orderField and orderMode depending on sign + / - and name of sort field parameter
        if (sortField.startsWith("+"))
        {
            orderField = sortField.substring(1);
            orderMode = PaginationData.OrderMode.ASCENDING;
        } else if (sortField.startsWith("-"))
        {
            orderField = sortField.substring(1);
            orderMode = PaginationData.OrderMode.DESCENDING;
        } else
        {
            orderField = sortField;
            orderMode = PaginationData.OrderMode.ASCENDING;
        }

        return new PaginationData(firstResult, perPage, orderField, orderMode);
    }

    protected String getSortField()
    {
        String sortField = uriInfo.getQueryParameters().getFirst("sort");
        if (sortField == null)
        {
            return getDefaultSortField();
        }
        return sortField;
    }

    private Integer getPage()
    {
        String page = uriInfo.getQueryParameters().getFirst("page");
        if (page == null)
        {
            return DEFAULT_PAGE;
        }
        return Integer.parseInt(page);
    }

    private Integer getPerPage()
    {
        String perPage = uriInfo.getQueryParameters().getFirst("per_page");
        if (perPage == null)
        {
            return DEFAULT_PER_PAGE;
        }
        return Integer.parseInt(perPage);
    }

}
