package com.learning.app.review.resource;

import com.learning.app.category.resource.CategoryJsonConverter;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.commontests.utils.ResourceDefinitions;
import com.learning.app.course.exception.CourseNotFoundException;
import com.learning.app.course.model.Course;
import com.learning.app.course.resource.CourseJsonConverter;
import com.learning.app.review.exception.ReviewNotFoundException;
import com.learning.app.review.model.Review;
import com.learning.app.review.model.filter.ReviewFilter;
import com.learning.app.review.service.ReviewService;
import com.learning.app.user.exception.UserNotFoundException;
import com.learning.app.user.model.User;
import com.learning.app.user.resource.UserJsonConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.learning.app.commontests.data.CourseData.courseWithId;
import static com.learning.app.commontests.data.CourseData.learnSwift;
import static com.learning.app.commontests.data.ReviewData.*;
import static com.learning.app.commontests.data.UserData.jan;
import static com.learning.app.commontests.data.UserData.userWithIdAndCreatedAt;
import static com.learning.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.learning.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.learning.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.learning.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.learning.app.commontests.utils.JsonTestUtils.readJsonFile;
import static com.learning.app.review.model.ReviewArgumentMatcher.reviewEquivalent;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Matchers.*;


public class ReviewResourceTest
{
    private ReviewResource reviewResource;

    private static final String PATH_RESOURCE = ResourceDefinitions.REVIEW.getResourceName();

    @Mock
    private ReviewService reviewService;

    @Mock
    private UriInfo uriInfo;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        reviewResource = new ReviewResource();
        reviewResource.reviewService = reviewService;
        reviewResource.uriInfo = uriInfo;
        reviewResource.reviewJsonConverter = new ReviewJsonConverter();
    }

    @Test
    public void addValidReview_ShouldReturnReviewId() throws Exception
    {
        //Create review object
        User user = new User();
        user.setId(1L);
        Course course = new Course();
        course.setId(1L);
        Review review = reviewWithoutId(3, user, course);
        //Set up service to return review
        when(reviewService.add(reviewEquivalent(review))).thenReturn(reviewWithId(review, 1L));

        Response response = reviewResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "review.json")));

        //Assert response status is Created and response entity contains id of created review
        assertThat(response.getStatus(), is(equalTo(Response.Status.CREATED.getStatusCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addReviewCourseNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        when(reviewService.add((Review) anyObject())).thenThrow(CourseNotFoundException.class);

        Response response = reviewResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "review.json")));

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "reviewErrorInexistentCourse.json"));
    }

    @Test
    public void addReviewUserNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        when(reviewService.add((Review) anyObject())).thenThrow(UserNotFoundException.class);

        Response response = reviewResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "review.json")));

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "reviewErrorInexistentUser.json"));
    }

    @Test
    public void updateValidReview_ShouldReturnSuccess() throws Exception
    {
        //Create expected Review object
        Review expectedReview = reviewWithId(reviewLenaSwift(), 1L);
        Course course = new Course();
        course.setId(1L);
        User user = new User();
        user.setId(1L);
        expectedReview.setCourse(course);
        expectedReview.setUser(user);

        Response response = reviewResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE, "review.json")));

        //Assert response status is OK and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct review is passed to CourseService
        verify(reviewService).update(reviewEquivalent(expectedReview));
    }

    @Test
    public void updateReviewNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(ReviewNotFoundException.class).when(reviewService).update((Review) anyObject());

        Response response = reviewResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE, "review.json")));

        //Assert response status is Not Found
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void updateReviewCourseNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(CourseNotFoundException.class).when(reviewService).update((Review) anyObject());

        Response response = reviewResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE, "review.json")));

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "reviewErrorInexistentCourse.json"));
    }

    @Test
    public void updateReviewUserNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(UserNotFoundException.class).when(reviewService).update((Review) anyObject());

        Response response = reviewResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE, "review.json")));

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "reviewErrorInexistentUser.json"));
    }

    @Test
    public void findByFilter_ShouldReturn3Reviews() throws Exception
    {
        //Create review list with ids
        List<Review> reviews = new ArrayList<>(Arrays.asList(reviewLenaSwift(), reviewJanPython(), reviewDanielSpanish()));
        Long i = 1L;
        for (Review review : reviews)
        {
            review.setId(i);
            review.getUser().setId(i);
            review.getCourse().setId(i);
            i++;
        }
        //Return paginated list of reviews when service is called
        when(reviewService.findByFilter((ReviewFilter) anyObject()))
                .thenReturn(new PaginatedData<Review>(reviews.size(), reviews));
        //return multimap when asked for query parameters from uri
        MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);

        Response response = reviewResource.findByFilter();

        //Assert response status is OK and response entity contains correct review data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "reviewsAllInOnePage.json"));
    }

    @Test
    public void findById_ShouldReturnReview() throws Exception
    {
        //Create review object
        Review review = reviewWithId(reviewLenaSwift(), 1L);
        review.getUser().setId(1L);
        review.getCourse().setId(1L);
        //Set up service to return review
        when(reviewService.findById(1L)).thenReturn(review);

        Response response = reviewResource.findById(1L);

        //Assert response status is OK and response entity returns correct review data
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
               "singleReview.json"));
    }

    @Test
    public void findByIdNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        when(reviewService.findById(1L)).thenThrow(ReviewNotFoundException.class);

        Response response = reviewResource.findById(1L);

        //Assert response status is Not Found
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void deleteValidReview_ShouldReturnSuccess() throws Exception
    {
        Response response = reviewResource.delete(1L);

        //Assert response status is OK and response entity is empty
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        //Verify correct id was passed to ReviewService
        verify(reviewService).deleteById(1L);
    }

    @Test
    public void deleteReviewNotFound_ShouldReturnError() throws Exception
    {
        //Set up service to throw exception
        doThrow(ReviewNotFoundException.class).when(reviewService).deleteById(1L);

        Response response = reviewResource.delete(1L);

        //Assert response status is Not Found and error message is returned
        assertThat(response.getStatus(), is(equalTo(Response.Status.NOT_FOUND.getStatusCode())));
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE,
                "reviewErrorNotFound.json"));
    }
}