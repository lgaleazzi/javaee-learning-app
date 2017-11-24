package com.learning.app.review.service;

import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.course.exception.CourseNotFoundException;
import com.learning.app.course.service.CourseService;
import com.learning.app.review.exception.ReviewNotFoundException;
import com.learning.app.review.model.Review;
import com.learning.app.review.model.filter.ReviewFilter;
import com.learning.app.review.repository.ReviewRepository;
import com.learning.app.user.exception.UserNotFoundException;
import com.learning.app.user.service.UserService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static com.learning.app.commontests.data.ReviewData.*;

public class ReviewServiceImplTest
{
    private ReviewService reviewService;
    private static Validator validator;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @BeforeClass
    public static void setUpTestClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        reviewService = new ReviewServiceImpl();
        ((ReviewServiceImpl)reviewService).validator = validator;
        ((ReviewServiceImpl)reviewService).reviewRepository = reviewRepository;
        ((ReviewServiceImpl)reviewService).courseService = courseService;
        ((ReviewServiceImpl)reviewService).userService = userService;
    }


    @Test(expected = CourseNotFoundException.class)
    public void addReviewWithNonExistingCourse_ShouldThrowException() {
        when(courseService.findById(1L)).thenThrow(new CourseNotFoundException());

        Review review = reviewJanPython();
        review.getCourse().setId(1L);

        reviewService.add(review);
    }

    @Test(expected = UserNotFoundException.class)
    public void addReviewWithNonExistingUser_ShouldThrowException() {
        when(userService.findById(1L)).thenThrow(new UserNotFoundException());

        Review review = reviewJanPython();
        review.getUser().setId(1L);

        reviewService.add(review);
    }


    @Test(expected = FieldInvalidException.class)
    public void addReviewWithInvalidRating_ShouldThrowException() throws Exception
    {
        Review review = reviewJanPython();
        review.setRating(10);

        reviewService.add(review);
    }

    @Test
    public void addValidReview_ShouldReturnPersistedReview() throws Exception
    {
        Review review = reviewJanPython();
        when(reviewRepository.add(review)).thenReturn(reviewWithId(reviewJanPython(), 1L));
        when(userService.findById(anyLong())).thenReturn(review.getUser());
        when(courseService.findById(anyLong())).thenReturn(review.getCourse());

        Review reviewAdded = reviewService.add(review);

        assertThat(reviewAdded.getId(), is(equalTo(1L)));
    }

    @Test(expected = CourseNotFoundException.class)
    public void updateReviewWithNonExistingCourse_ShouldThrowException() {
        when(courseService.findById(1L)).thenThrow(new CourseNotFoundException());
        when(reviewRepository.idExists(1L)).thenReturn(true);

        Review review = reviewWithId(reviewJanPython(), 1L);
        review.getCourse().setId(1L);

        reviewService.update(review);
    }

    @Test(expected = UserNotFoundException.class)
    public void updateReviewWithNonExistingUser_ShouldThrowException() {
        when(userService.findById(1L)).thenThrow(new UserNotFoundException());
        when(reviewRepository.idExists(1L)).thenReturn(true);

        Review review = reviewWithId(reviewJanPython(), 1L);
        review.getUser().setId(1L);

        reviewService.update(review);
    }

    @Test(expected = FieldInvalidException.class)
    public void updateReviewWithInvalidRating_ShouldThrowException() throws Exception
    {
        when(reviewRepository.idExists(1L)).thenReturn(true);
        Review review = reviewWithId(reviewJanPython(), 1L);
        review.setRating(10);

        reviewService.update(review);
    }

    @Test
    public void updateValidReview_ShouldCallUpdateInRepository() throws Exception
    {
        when(reviewRepository.idExists(1L)).thenReturn(true);
        when(userService.findById(anyLong())).thenReturn(reviewJanPython().getUser());
        when(courseService.findById(anyLong())).thenReturn(reviewJanPython().getCourse());

        reviewService.update(reviewWithId(reviewJanPython(), 1L));

        verify(reviewRepository).update(reviewWithId(reviewJanPython(), 1L));
    }

    @Test(expected = ReviewNotFoundException.class)
    public void findReviewById_ShouldThrowExceptionIfNotFound() throws Exception
    {
        when(reviewRepository.findById(999L)).thenReturn(null);

        Review reviewFound = reviewService.findById(999L);

        assertThat(reviewFound, is(nullValue()));
    }

    @Test
    public void findReviewById_ShouldReturnCorrectReview() throws Exception
    {
        Review review = reviewWithId(reviewJanPython(), 1L);
        when(reviewRepository.findById(1L)).thenReturn(review);

        Review reviewFound = reviewService.findById(1L);
        assertThat(reviewFound, is(notNullValue()));
        assertThat(reviewFound.getId(), is(equalTo(1L)));
        assertThat(reviewFound.getRating(), is(equalTo(review.getRating())));
    }

    @Test
    public void findReviewByFilter_ReturnsFilteredPaginatedData() throws Exception
    {
        PaginatedData<Review> reviews = new PaginatedData<>(1, Arrays.asList(
                reviewWithId(reviewJanPython(), 1L),
                reviewWithId(reviewJanSwift(), 2L))
        );
        ReviewFilter reviewFilter = new ReviewFilter();
        when(reviewRepository.findByFilter(reviewFilter))
                .thenReturn(reviews);

        PaginatedData<Review> reviewsReturned = reviewService.findByFilter(reviewFilter);

        assertThat(reviewsReturned.getNumberOfRows(), is(equalTo(1)));
        assertThat(reviewsReturned.getRow(0).getCourse(), is(equalTo(reviewJanPython().getCourse())));
        assertThat(reviewsReturned.getRow(0).getUser(), is(equalTo(reviewJanPython().getUser())));
    }

    @Test
    public void deleteExistingCourse_ShouldCallDeleteInRepository() throws Exception
    {
        when(reviewRepository.idExists(1L)).thenReturn(true);

        reviewService.deleteById(1L);

        verify(reviewRepository).delete(1L);
    }

    @Test(expected = ReviewNotFoundException.class)
    public void deleteNonExistingReview_ShouldThrowException() throws Exception
    {
        reviewService.deleteById(999L);
    }
}