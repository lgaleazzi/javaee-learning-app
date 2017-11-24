package com.learning.app.course.resource;

import com.learning.app.category.exception.CategoryNotFoundException;
import com.learning.app.category.model.Category;
import com.learning.app.category.resource.CategoryJsonConverter;
import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.course.exception.CourseNotFoundException;
import com.learning.app.course.model.Course;
import com.learning.app.course.model.filter.CourseFilter;
import com.learning.app.course.service.CourseService;
import com.learning.app.commontests.utils.ResourceDefinitions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;

import static com.learning.app.commontests.data.CategoryData.categoryWithId;
import static com.learning.app.commontests.data.CourseData.*;
import static com.learning.app.commontests.utils.FileTestNameUtils.*;
import static com.learning.app.commontests.utils.JsonTestUtils.*;
import static com.learning.app.course.model.CourseArgumentMatcher.courseEquivalent;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

public class CourseResourceTest
{
    private CourseResource courseResource;

    private static final String PATH_RESOURCE = ResourceDefinitions.COURSE.getResourceName();

    @Mock
    private CourseService courseService;

    @Mock
    private UriInfo uriInfo;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        courseResource = new CourseResource();
        courseResource.courseService = courseService;

        courseResource.courseJsonConverter = new CourseJsonConverter();

        courseResource.uriInfo = uriInfo;
    }

    @Test
    public void addValidCourse_ShouldReturnCourseId() throws Exception
    {
        //Set up service to return course
        Course course = learnJava();
        course.setCategory(categoryWithId("Coding", 1L));
        when(courseService.add(course)).thenReturn(courseWithId(learnJava(), 1L));

        Response response = courseResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "learnJavaCourse.json")));

        //Assert response status is Created and response entity contains id of created course
        assertThat(response.getStatus(), is(equalTo(Response.Status.CREATED.getStatusCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addCourseNullName_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        when(courseService.add((Course) anyObject())).thenThrow(new FieldInvalidException("name", "may not be null"));

        Response response = courseResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "learnJavaCourse.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorName.json"));
    }

    @Test
    public void addCourseNullURL_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        when(courseService.add((Course) anyObject())).thenThrow(new FieldInvalidException("url", "may not be null"));

        Response response = courseResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "learnJavaCourse.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorNullURL.json"));
    }

    @Test
    public void addCourseCategoryNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        when(courseService.add((Course) anyObject())).thenThrow(new CategoryNotFoundException());

        Response response = courseResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "learnJavaCourse.json")));

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorInexistentCategory.json"));
    }

    @Test
    public void updateValidCourse_ShouldReturnSuccess() throws Exception
    {
        //Create expected course object
        Course expectedCourse = courseWithId(learnJava(), 1L);
        Category category = new Category();
        category.setId(1L);
        expectedCourse.setCategory(category);

        Response response = courseResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "learnJavaCourse.json")));

        //Assert response status is OK and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct course is passed to CourseService
        verify(courseService).update(courseEquivalent(expectedCourse));
    }

    @Test
    public void updateCourseNullName_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(new FieldInvalidException("name", "may not be null")).when(courseService).update((Course) anyObject());

        Response response = courseResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "learnJavaCourse.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorName.json"));
    }

    @Test
    public void updateCourseNullURL_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(new FieldInvalidException("url", "may not be null")).when(courseService).update((Course) anyObject());

        Response response = courseResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "learnJavaCourse.json")));

        //Assert response status is Bad Request and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorNullURL.json"));
    }

    @Test
    public void updateCourseNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(CourseNotFoundException.class).when(courseService).update((Course) anyObject());

        Response response = courseResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "learnJavaCourse.json")));

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorNotFound.json"));
    }

    @Test
    public void updateCourseCategoryNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(CategoryNotFoundException.class).when(courseService).update((Course) anyObject());

        Response response = courseResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "learnJavaCourse.json")));

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorInexistentCategory.json"));
    }

    @Test
    public void findById_ShouldReturnCourse() throws Exception
    {
        //Set up service to return course
        Course course = courseWithId(learnJava(), 1L);
        course.getCategory().setId(1L);
        when(courseService.findById(1L)).thenReturn(course);

        Response response = courseResource.findById(1L);

        //Assert response status is OK and response entity contains correct course data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                        "singleCourse.json"));
    }

    @Test
    public void findByIdNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        when(courseService.findById(1L)).thenThrow(CourseNotFoundException.class);

        Response response = courseResource.findById(1L);

        //Assert response status is Not Found
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void findByFilterNoFilter_ShouldReturn4Courses() throws Exception
    {
        //Create list of courses with id
        List<Course> courses = courseListWithId();
        Long categoryId = 1L;
        for (Course course : courses)
        {
            course.getCategory().setId(categoryId++);
        }
        //Return paginated list of courses when service is called
        when(courseService.findByFilter((CourseFilter) anyObject()))
                .thenReturn(new PaginatedData<Course>(courses.size(), courses));

        //return multimap when asked for query parameters from uri
        MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);

        Response response = courseResource.findByFilter();

        //Assert response status is OK and response entity contains correct course data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "coursesAllInOnePage.json"));
    }

    @Test
    public void deleteValidCourse_ShouldReturnSuccess() throws Exception
    {
        Response response = courseResource.delete(1L);

        //Assert response status is OK and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct id was passed to CourseService
        verify(courseService).deleteById(1L);
    }

    @Test
    public void deleteCourseNotFound_ShouldReturnError() throws Exception
    {
        //Throw exception when service is called
        doThrow(CourseNotFoundException.class).when(courseService).deleteById(1L);

        Response response = courseResource.delete(1L);

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "courseErrorNotFound.json"));
    }
}