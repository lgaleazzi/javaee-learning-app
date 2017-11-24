package com.learning.app.user.resource;


import com.learning.app.common.resource.AbstractFilterExtractorFromURL;
import com.learning.app.user.model.filter.UserFilter;

import javax.ws.rs.core.UriInfo;

public class UserFilterExtractorFromURL extends AbstractFilterExtractorFromURL
{
    public UserFilterExtractorFromURL(UriInfo uriInfo)
    {
        super(uriInfo);
    }

    @Override
    protected String getDefaultSortField()
    {
        return "id";
    }

    public UserFilter getFilter()
    {
        //Create UserFilter object
        UserFilter userFilter = new UserFilter();
        //Set pagination data
        userFilter.setPaginationData(extractPaginationData());

        //Set name filter from url paramater
        userFilter.setName(getUriInfo().getQueryParameters().getFirst("name"));

        return userFilter;
    }
}
