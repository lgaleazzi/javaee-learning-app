package com.learning.app.review.resource;

import com.learning.app.common.model.filter.PaginationData;
import com.learning.app.review.model.filter.ReviewFilter;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.UriInfo;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.learning.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.learning.app.commontests.utils.FilterExtractorTestUtils.setUpUriInfoWithMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ReviewFilterExtractorFromURLTest
{
    private UriInfo uriInfo;

    @Before
    public void setUp()
    {
        uriInfo = mock(UriInfo.class);
    }

    @Test
    public void nullParameters_ShouldReturnFilterWithDefaultValues() {
        setUpUriInfo(null, null, null, null, null);

        ReviewFilterExtractorFromURL extractor = new ReviewFilterExtractorFromURL(uriInfo);
        ReviewFilter reviewFilter = extractor.getFilter();

        //Assert pagination data is set up with default values: firstResult 0, maxResultsPerPage 10, orderField id,
        // orderMode ascending
        assertActualPaginationDataWithExpected(reviewFilter.getPaginationData(), new PaginationData(0, 10, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert courseId and userId filter parameters are null
        assertThat(reviewFilter.getCourseId(), is(nullValue()));
        assertThat(reviewFilter.getUserId(), is(nullValue()));
    }

    @Test
    public void withPaginationAndCourseIdAndSortAscending_ShouldReturnCorrectFilterAndSortAscending() {
        //uri parameters: page 2, 5 results per page, courseId 1, userId null, sort by id
        setUpUriInfo("2", "5", "1", null,"id");

        ReviewFilterExtractorFromURL extractor = new ReviewFilterExtractorFromURL(uriInfo);
        ReviewFilter reviewFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Ascending
        //TODO: check the numbering of pages in pagination data and filter parameters. Should it be first result 6?
        assertActualPaginationDataWithExpected(reviewFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert courseId filter parameter is set
        assertThat(reviewFilter.getCourseId(), is(equalTo(1L)));
    }

    @Test
    public void withPaginationAndUserIdAndSortAscending_ShouldReturnCorrectFilterAndSortAscending() {
        //uri parameters: page 2, 5 results per page, courseId null, userId 1, sort by id
        setUpUriInfo("2", "5", null, "1","id");

        ReviewFilterExtractorFromURL extractor = new ReviewFilterExtractorFromURL(uriInfo);
        ReviewFilter reviewFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Ascending
        assertActualPaginationDataWithExpected(reviewFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert userId filter parameter is set
        assertThat(reviewFilter.getUserId(), is(equalTo(1L)));
    }

    @Test
    public void withPaginationCourseIdUserIdAndSortAscendingWithPrefix_ShouldReturnCorrectFilterAndSortAscending() {
        //uri parameters: page 2, 5 results per page, courseId 1, userId 1, sort by id ascending
        setUpUriInfo("2", "5", "1", "1", "+id");

        ReviewFilterExtractorFromURL extractor = new ReviewFilterExtractorFromURL(uriInfo);
        ReviewFilter reviewFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5,
        // orderField id, orderMode Ascending
        assertActualPaginationDataWithExpected(reviewFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.ASCENDING));

        //Assert courseId filter parameter is set
        assertThat(reviewFilter.getCourseId(), is(equalTo(1L)));
        //Assert userId filter parameter is set
        assertThat(reviewFilter.getUserId(), is(equalTo(1L)));
    }

    @Test
    public void withPaginationAndNameAndSortDescending_ShouldReturnCorrectFilterAndSortDescending() {
        //uri parameters: page 2, 5 results per page, courseId 1, userId 1, sort by id descending
        setUpUriInfo("2", "5", "1", "1", "-id");

        ReviewFilterExtractorFromURL extractor = new ReviewFilterExtractorFromURL(uriInfo);
        ReviewFilter reviewFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5,
        // orderField id, orderMode Descending
        assertActualPaginationDataWithExpected(reviewFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.DESCENDING));
        //Assert courseId filter parameter is set
        assertThat(reviewFilter.getCourseId(), is(equalTo(1L)));
        //Assert userId filter parameter is set
        assertThat(reviewFilter.getUserId(), is(equalTo(1L)));
    }

    //Set up uriInfo with all relevant parameters
    @SuppressWarnings("unchecked")
    private void setUpUriInfo(String page, String perPage, String courseId, String userId, String sort) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("course_id", courseId);
        parameters.put("user_id", userId);
        parameters.put("sort", sort);

        setUpUriInfoWithMap(uriInfo, parameters);
    }
}