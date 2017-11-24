package com.learning.app.course.resource;


import com.learning.app.common.resource.AbstractFilterExtractorFromURL;
import com.learning.app.course.model.filter.CourseFilter;

import javax.ws.rs.core.UriInfo;

public class CourseFilterExtractorFromURL extends AbstractFilterExtractorFromURL
{
    private UriInfo uriInfo;

    public CourseFilterExtractorFromURL(UriInfo uriInfo)
    {
        super(uriInfo);
    }

    @Override
    protected String getDefaultSortField()
    {
        return "name";
    }

    public CourseFilter getFilter()
    {
        //Create CourseFilter object
        CourseFilter courseFilter = new CourseFilter();
        //Set pagination data
        courseFilter.setPaginationData(extractPaginationData());

        //Set name filter from url parameter
        courseFilter.setName(getUriInfo().getQueryParameters().getFirst("name"));
        //Set category id filter from url parameter
        String categoryIdString = getUriInfo().getQueryParameters().getFirst("categoryId");
        if (categoryIdString != null)
        {
            courseFilter.setCategoryId(Long.valueOf(categoryIdString));
        }

        return courseFilter;
    }
}
