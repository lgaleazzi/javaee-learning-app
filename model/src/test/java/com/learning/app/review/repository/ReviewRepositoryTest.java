package com.learning.app.review.repository;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.filter.PaginationData;
import com.learning.app.commontests.repository.TestBaseRepository;
import com.learning.app.course.model.Course;
import com.learning.app.review.model.Review;
import com.learning.app.review.model.filter.ReviewFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.learning.app.commontests.data.CategoryData.categoryList;
import static com.learning.app.commontests.data.CourseData.courseList;
import static com.learning.app.commontests.data.CourseData.getCourseWithDependencies;
import static com.learning.app.commontests.data.UserData.userList;
import static com.learning.app.commontests.data.ReviewData.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class ReviewRepositoryTest extends TestBaseRepository
{
    private ReviewRepository reviewRepository;

    @Before
    public void setUp()
    {
        initializeTestDB();

        reviewRepository = new ReviewRepository();
        reviewRepository.em = em;

        loadUsersToDB();
        loadCoursesToDB();
    }

    @After
    public void tearDown()
    {
        closeEntityManager();
    }


    @Test
    public void add_ShouldPersistReview() throws Exception
    {
        Review reviewAdded = transactionExecutor.executeCommandWithResult(
                () -> reviewRepository.add(getReviewWithDependencies(reviewJanPython(), em))
        );

        assertThat(reviewAdded.getId(), is(notNullValue()));
    }

    @Test
    public void update_ShouldPersistReview() throws Exception
    {
        Review reviewAdded = transactionExecutor.executeCommandWithResult(
                () -> reviewRepository.add(getReviewWithDependencies(reviewJanPython(), em))
        );

        reviewAdded.setRating(5);
        transactionExecutor.executeCommandWithNoResult(
                () -> reviewRepository.update(reviewAdded)
        );

        Review reviewUpdated = em.find(Review.class, reviewAdded.getId());
        assertThat(reviewUpdated.getRating(), is(equalTo(5)));
    }

    @Test
    public void findByFilter_ShouldReturnFilteredAndPaginatedData()
    {
        List<Review> reviewListWithDependencies = new ArrayList<>();
        reviewList()
                .forEach(review ->
                        reviewListWithDependencies.add(getReviewWithDependencies(review, em))
                );

        loadReviewsToDB();

        ReviewFilter reviewFilter = new ReviewFilter();
        //get Id of the course of the first review in the list (swift course)
        Long courseId = reviewListWithDependencies.get(0).getCourse().getId();
        reviewFilter.setCourseId(courseId);
        //set pagination data to 2 rows per page, starting at row 0, order by rating
        reviewFilter.setPaginationData(new PaginationData(0, 2, "rating", PaginationData.OrderMode.ASCENDING));

        PaginatedData<Review> result = reviewRepository.findByFilter(reviewFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getCourse().getId(), is(equalTo(courseId)));
        assertThat(result.getRow(1).getCourse().getId(), is(equalTo(courseId)));
    }

    @Test
    public void findById_ShouldReturnNullIfIdNotExist() throws Exception
    {
        Review review = reviewRepository.findById(999L);

        assertThat(review, is(nullValue()));
    }


    @Test
    public void findById_ShouldReturnReviewIfExist() throws Exception
    {
        Review reviewAdded = transactionExecutor.executeCommandWithResult(
                () -> reviewRepository.add(getReviewWithDependencies(reviewJanPython(), em))
        );


        Review reviewFoundById = reviewRepository.findById(reviewAdded.getId());

        assertThat(reviewFoundById, is(equalTo(reviewAdded)));
    }

    @Test
    public void delete_ShouldRemoveReview() throws Exception
    {
        Review reviewAdded = transactionExecutor.executeCommandWithResult(
                () -> reviewRepository.add(getReviewWithDependencies(reviewJanPython(), em))
        );

        reviewRepository.delete(reviewAdded.getId());

        assertThat(em.find(Review.class, reviewAdded.getId()), is(nullValue()));
    }

    @Test
    public void deleteByCourseId_ShouldRemoveReview() throws Exception
    {
        loadReviewsToDB();
        Review review = em.find(Review.class, 1L);
        Long courseId = review.getCourse().getId();

        reviewRepository.deleteByCourseId(courseId);

        assertThat(em.find(Review.class, 1L), is(nullValue()));
    }


    private void loadCategoriesToDB()
    {
        transactionExecutor.executeCommandWithNoResult(
                () -> categoryList().forEach(em::persist)
        );
    }

    private void loadUsersToDB()
    {
        transactionExecutor.executeCommandWithNoResult(
                () -> userList().forEach(em::persist)
        );
    }

    private void loadCoursesToDB()
    {
        loadCategoriesToDB();
        transactionExecutor.executeCommandWithNoResult(
                () -> courseList().forEach(
                course -> em.persist(getCourseWithDependencies(course, em))
                )
        );
    }

    private void loadReviewsToDB()
    {
        transactionExecutor.executeCommandWithNoResult(
                () -> reviewList().forEach(
                review -> em.persist(getReviewWithDependencies(review, em))
                )
        );
    }



}