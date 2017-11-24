package com.learning.app.user.resource;

import com.learning.app.common.model.filter.PaginationData;
import com.learning.app.user.model.filter.UserFilter;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.UriInfo;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.learning.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.learning.app.commontests.utils.FilterExtractorTestUtils.setUpUriInfoWithMap;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserFilterExtractorFromURLTest
{
    private UriInfo uriInfo;

    @Before
    public void setUp() {
        uriInfo = mock(UriInfo.class);
    }

    @Test
    public void nullParameters_ShouldReturnFilterWithDefaultValues() {
        setUpUriInfo(null, null, null, null);

        UserFilterExtractorFromURL extractor = new UserFilterExtractorFromURL(uriInfo);
        UserFilter userFilter = extractor.getFilter();

        //Assert pagination data is set up with default values: firstResult 0, maxResultsPerPage 10, orderField id,
        // orderMode ascending
        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(0, 10, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert name filter parameter is null
        assertThat(userFilter.getName(), is(nullValue()));
    }

    @Test
    public void withPaginationAndNameAndSortAscending_ShouldReturnCorrectFilterAndSortAscending() {
        //uri parameters: page 2, 5 results per page, name John, sort by id
        setUpUriInfo("2", "5", "John", "id");

        UserFilterExtractorFromURL extractor = new UserFilterExtractorFromURL(uriInfo);
        UserFilter userFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Ascending
        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert name filter parameter is set
        assertThat(userFilter.getName(), is(equalTo("John")));
    }

    @Test
    public void withPaginationAndNameAndSortAscendingWithPrefix_ShouldReturnCorrectFilterAndSortAscending() {
        setUpUriInfo("2", "5", "John", "+id");

        UserFilterExtractorFromURL extractor = new UserFilterExtractorFromURL(uriInfo);
        UserFilter userFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Ascending
        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.ASCENDING));
        //Assert name filter parameter is set
        assertThat(userFilter.getName(), is(equalTo("John")));
    }

    @Test
    public void withPaginationAndNameAndUserTypeAndSortDescending_ShouldReturnCorrectFilterAndSortDescending() {
        setUpUriInfo("2", "5", "John", "-id");

        UserFilterExtractorFromURL extractor = new UserFilterExtractorFromURL(uriInfo);
        UserFilter userFilter = extractor.getFilter();

        //Assert pagination data is set up according to uriInfo parameters: firstResult 10, maxResultsPerPage 5
        // orderField id, orderMode Descending
        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(10, 5, "id",
                PaginationData.OrderMode.DESCENDING));
        //Assert name filter parameter is set
        assertThat(userFilter.getName(), is(equalTo("John")));
    }

    //Set up uriInfo with all relevant parameters
    private void setUpUriInfo(String page, String perPage, String name, String sort) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("name", name);
        parameters.put("sort", sort);

        setUpUriInfoWithMap(uriInfo, parameters);
    }
}