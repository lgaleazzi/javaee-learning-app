package com.learning.app.course.repository;


import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.repository.GenericRepository;
import com.learning.app.course.model.Course;
import com.learning.app.course.model.filter.CourseFilter;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class CourseRepository extends GenericRepository<Course>
{
    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<Course> getPersistentClass()
    {
        return Course.class;
    }

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    @SuppressWarnings("unchecked")
    public PaginatedData<Course> findByFilter(CourseFilter courseFilter)
    {
        StringBuilder clause = new StringBuilder("WHERE e.id is not null");
        Map<String, Object> queryParameters = new HashMap<>();

        //Get Filter parameters
        if (courseFilter.getName() != null)
        {
            clause.append(" And UPPER(e.name) Like UPPER(:name)");
            queryParameters.put("name", "%" + courseFilter.getName() + "%");
        }
        if (courseFilter.getCategoryId() != null)
        {
            clause.append(" AND e.category.id = :category_id");
            queryParameters.put("category_id", courseFilter.getCategoryId());
        }

        return findPaginatedDataByParameters(clause.toString(), courseFilter.getPaginationData(), queryParameters, "name " +
                "ASC");
    }
}
