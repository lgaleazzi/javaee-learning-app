package com.learning.app.commontests.data;

import com.learning.app.course.model.Course;
import com.learning.app.review.model.Review;
import com.learning.app.user.model.User;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.learning.app.commontests.data.CourseData.*;
import static com.learning.app.commontests.data.UserData.*;
import static com.learning.app.commontests.repository.TestRepositoryUtils.findByPropertyNameAndValue;

public class ReviewData
{

    public static List<Review> reviewList()
    {
        return Arrays.asList(
                reviewJanSwift(),
                reviewJanPython(),
                reviewDanielSwift(),
                reviewLenaSwift(),
                reviewDanielSpanish()
        );
    }

    public static List<Review> reviewListWithId()
    {
        return Arrays.asList(
                reviewWithId(reviewJanSwift(), 1L),
                reviewWithId(reviewJanPython(), 2L),
                reviewWithId(reviewDanielSwift(), 3L),
                reviewWithId(reviewLenaSwift(), 4L),
                reviewWithId(reviewDanielSpanish(), 5L)
        );
    }


    public static Review reviewJanSwift()
    {
        return reviewWithoutId(1, jan(), learnSwift());
    }

    public static Review reviewLenaSwift()
    {
        return reviewWithoutId(3, lena(), learnSwift());
    }

    public static Review reviewDanielSwift()
    {
        return reviewWithoutId(4, daniel(), learnSwift());
    }

    public static Review reviewJanPython()
    {
        return reviewWithoutId(4, jan(), learnPython());
    }

    public static Review reviewDanielSpanish()
    {
        return reviewWithoutId(3, daniel(), spanishForBeginners());
    }

    public static Review reviewWithId(Review review, Long id)
    {
        Review reviewWithId = reviewWithoutId(review.getRating(), review.getUser(), review.getCourse());
        reviewWithId.setId(id);
        return reviewWithId;
    }

    public static Review reviewWithoutId(int rating, User user, Course course)
    {
        Review review = new Review();
        review.setRating(rating);
        review.setUser(user);
        review.setCourse(course);
        review.setComment("A review comment");
        review.setCreatedAt(LocalDate.parse("2017-01-03", DateTimeFormatter.ISO_LOCAL_DATE));

        return review;
    }

    public static Review getReviewWithDependencies(Review review, EntityManager em)
    {
        User managedUser = findByPropertyNameAndValue(em, User.class, "name", review.getUser().getName());
        review.setUser(managedUser);

        Course managedCourse = findByPropertyNameAndValue(em, Course.class, "name", review.getCourse().getName());
        review.setCourse(managedCourse);

        return review;
    }
}
