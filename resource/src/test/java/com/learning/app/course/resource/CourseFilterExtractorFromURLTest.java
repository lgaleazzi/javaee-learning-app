package com.learning.app.course.resource;

import com.learning.app.common.model.filter.PaginationData;
import com.learning.app.course.model.filter.CourseFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static com.learning.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.learning.app.commontests.utils.FilterExtractorTestUtils.setUpUriInfoWithMap;


public class CourseFilterExtractorFromURLTest
{
    private UriInfo uriInfo;

    @Before
    public void setUp()
    {
        uriInfo = mock(UriInfo.class);
    }

    @Test
    public void nullParameters_ShouldReturnFilterWithDefaultValues() {
        setUpUriInfo(null, null, null, null);

        CourseFilterExtractorFromURL extractor = new CourseFilterExtractorFromURL(uriInfo);
        CourseFilter courseFilter = extractor.getFilter();

        //Assert pagination data is set up with default values: firstResult 0, maxResultsPerPage 10, orderField id,
        // orderMode ascending
        assertActualPaginationDataWithExpected(courseFilter.getPaginationData(), new PaginationData(0, 10, "name",
                PaginationData.OrderMode.ASCENDING));
        //Assert name filter parameter is null
        assertThat(courseFilter.getName(), is(nullValue()));
    }

    @Test
    public void parametersPaginationAndName_ShouldReturnCorrectFilterAndSortAscending() {
        //uri parameters: page 2, 5 results per page, name Java, sort by id ascending
        setUpUriInfo("2", "5", "Java", "id");

        CourseFilterExtractorFromURL extractor = new CourseFilterExtractorFromURL(uriInfo);
        CourseFilter courseFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Ascending
        assertActualPaginationDataWithExpected(courseFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert name filter parameter is set
        assertThat(courseFilter.getName(), is(equalTo("Java")));
    }

    @Test
    public void parametersPaginationAndNameWithPrefix__ShouldReturnCorrectFilterAndSortAscending() {
        //uri parameters: page 2, 5 results per page, name Java, sort by id ascending
        setUpUriInfo("2", "5", "Java", "+id");

        CourseFilterExtractorFromURL extractor = new CourseFilterExtractorFromURL(uriInfo);
        CourseFilter courseFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Ascending
        assertActualPaginationDataWithExpected(courseFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert name filter parameter is set
        assertThat(courseFilter.getName(), is(equalTo("Java")));
    }

    @Test
    public void parametersPaginationAndNameAndSortDescending__ShouldReturnCorrectFilterAndSortDescending() {
        //uri parameters: page 2, 5 results per page, name Java, sort by id descending
        setUpUriInfo("2", "5", "Java", "-id");

        CourseFilterExtractorFromURL extractor = new CourseFilterExtractorFromURL(uriInfo);
        CourseFilter courseFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Descending
        assertActualPaginationDataWithExpected(courseFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.DESCENDING));
        //Assert name filter parameter is set
        assertThat(courseFilter.getName(), is(equalTo("Java")));
    }

    //Set up uriInfo with all relevant parameters
    @SuppressWarnings("unchecked")
    private void setUpUriInfo(String page, String perPage, String name, String sort) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("name", name);
        parameters.put("sort", sort);

        setUpUriInfoWithMap(uriInfo, parameters);
    }
}