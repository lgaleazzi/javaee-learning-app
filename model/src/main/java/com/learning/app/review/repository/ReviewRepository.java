package com.learning.app.review.repository;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.repository.GenericRepository;
import com.learning.app.review.model.Review;
import com.learning.app.review.model.filter.ReviewFilter;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ReviewRepository extends GenericRepository<Review>
{
    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<Review> getPersistentClass()
    {
        return Review.class;
    }

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public PaginatedData<Review> findByFilter(ReviewFilter reviewFilter)
    {
        StringBuilder clause = new StringBuilder("WHERE e.id is not null");
        Map<String, Object> queryParameters = new HashMap<>();

        //Get Filter parameters
        if (reviewFilter.getUserId() != null)
        {
            clause.append(" And e.user.id = :user_id");
            queryParameters.put("user_id", +reviewFilter.getUserId());
        }
        if (reviewFilter.getCourseId() != null)
        {
            clause.append(" And e.course.id = :course_id");
            queryParameters.put("course_id", +reviewFilter.getCourseId());
        }

        return findPaginatedDataByParameters(clause.toString(), reviewFilter.getPaginationData(), queryParameters, "rating DESC");
    }

    @SuppressWarnings("unchecked")
    public void deleteByCourseId(Long courseId)
    {
        //get all reviews with this course id
        List<Review> reviews = em
                .createQuery("Select e From Review e Where e.course.id = :course_id")
                .setParameter("course_id", courseId)
                .getResultList();

        //delete all reviews with this course id
        for (Review review : reviews)
        {
            em.remove(review);
        }
    }
}
