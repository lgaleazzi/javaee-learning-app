package com.learning.app.review.resource;

import com.learning.app.common.resource.AbstractFilterExtractorFromURL;
import com.learning.app.review.model.filter.ReviewFilter;

import javax.ws.rs.core.UriInfo;

public class ReviewFilterExtractorFromURL extends AbstractFilterExtractorFromURL
{
    public ReviewFilterExtractorFromURL(UriInfo uriInfo)
    {
        super(uriInfo);
    }

    @Override
    protected String getDefaultSortField()
    {
        return "id";
    }


    public ReviewFilter getFilter()
    {
        //create review filter object
        ReviewFilter reviewFilter = new ReviewFilter();
        //add pagination data
        reviewFilter.setPaginationData(extractPaginationData());

        //set user id filter from url parameter
        String userIdString = getUriInfo().getQueryParameters().getFirst("user_id");
        if (userIdString != null)
        {
            reviewFilter.setUserId(Long.valueOf(userIdString));
        }
        //set course id filter from url parameter
        String courseIdString = getUriInfo().getQueryParameters().getFirst("course_id");
        if (courseIdString != null)
        {
            reviewFilter.setCourseId(Long.valueOf(courseIdString));
        }

        return reviewFilter;
    }
}
