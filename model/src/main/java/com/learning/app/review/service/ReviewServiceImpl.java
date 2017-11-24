package com.learning.app.review.service;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.utils.DataValidation;
import com.learning.app.course.model.Course;
import com.learning.app.course.service.CourseService;
import com.learning.app.review.exception.ReviewNotFoundException;
import com.learning.app.review.model.Review;
import com.learning.app.review.model.filter.ReviewFilter;
import com.learning.app.review.repository.ReviewRepository;
import com.learning.app.user.model.User;
import com.learning.app.user.service.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

@Stateless
public class ReviewServiceImpl implements ReviewService
{
    @Inject
    ReviewRepository reviewRepository;

    @Inject
    UserService userService;

    @Inject
    CourseService courseService;

    @Inject
    Validator validator;


    @Override
    public Review add(Review review)
    {
        setReviewUser(review);
        setReviewCourse(review);
        DataValidation.validateEntityFields(validator, review);

        return reviewRepository.add(review);
    }

    @Override
    public void update(Review review)
    {
        DataValidation.validateEntityFields(validator, review);

        if (!reviewRepository.idExists(review.getId()))
        {
            throw new ReviewNotFoundException();
        }

        setReviewUser(review);
        setReviewCourse(review);

        reviewRepository.update(review);
    }

    @Override
    public Review findById(Long id)
    {
        Review review = reviewRepository.findById(id);
        if (review == null)
        {
            throw new ReviewNotFoundException();
        }

        return review;
    }

    @Override
    public PaginatedData<Review> findByFilter(ReviewFilter reviewFilter)
    {
        return reviewRepository.findByFilter(reviewFilter);
    }

    @Override
    public void deleteByCourseId(Long courseId)
    {
        reviewRepository.deleteByCourseId(courseId);
    }

    @Override
    public void deleteById(Long id)
    {
        if (!reviewRepository.idExists(id))
        {
            throw new ReviewNotFoundException();
        }

        reviewRepository.delete(id);
    }

    //The review object converted from json only contains the user id
    //This method retrieves the full user object and links it to the review object
    private void setReviewUser(Review review)
    {
        User user = userService.findById(review.getUser().getId());
        review.setUser(user);
    }

    //The review object converted from json only contains the course id
    //This method retrieves the full course object and links it to the review object
    private void setReviewCourse(Review review)
    {
        Course course = courseService.findById(review.getCourse().getId());
        review.setCourse(course);
    }
}
