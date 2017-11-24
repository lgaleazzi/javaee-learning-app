package com.learning.app.commontests.utils;


import com.learning.app.common.model.filter.PaginationData;
import org.junit.Ignore;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/*
 * Class providing helper methods to test filter extractors
 */

@Ignore
public class FilterExtractorTestUtils
{
    private FilterExtractorTestUtils()
    {
    }

    //Assert pagination data against expected pagination data
    public static void assertActualPaginationDataWithExpected(PaginationData actual,
                                                              PaginationData expected) {
        assertThat(actual.getFirstResult(), is(equalTo(expected.getFirstResult())));
        assertThat(actual.getMaxResults(), is(equalTo(expected.getMaxResults())));
        assertThat(actual.getOrderField(), is(equalTo(expected.getOrderField())));
        assertThat(actual.getOrderMode(), is(equalTo(expected.getOrderMode())));
    }

    //Create MultivaluedMap with parameters and return it when asking for uri parameters
    @SuppressWarnings("unchecked")
    public static void setUpUriInfoWithMap(UriInfo uriInfo, Map<String, String> parameters) {
        MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);

        for (Map.Entry<String, String> keyValue : parameters.entrySet()) {
            when(multiMap.getFirst(keyValue.getKey())).thenReturn(keyValue.getValue());
        }

        when(uriInfo.getQueryParameters()).thenReturn(multiMap);
    }
}
