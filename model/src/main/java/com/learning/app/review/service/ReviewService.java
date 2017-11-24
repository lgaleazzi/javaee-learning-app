package com.learning.app.review.service;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.review.model.Review;
import com.learning.app.review.model.filter.ReviewFilter;

import javax.ejb.Local;

@Local
public interface ReviewService
{
    Review add(Review review);

    void update(Review review);

    Review findById(Long id);

    PaginatedData<Review> findByFilter(ReviewFilter reviewFilter);

    void deleteByCourseId(Long id);

    void deleteById(Long id);
}
