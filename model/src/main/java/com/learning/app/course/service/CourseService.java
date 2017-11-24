package com.learning.app.course.service;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.course.model.Course;
import com.learning.app.course.model.filter.CourseFilter;

import javax.ejb.Local;
import java.util.List;

@Local
public interface CourseService
{
    Course add(Course course);

    void update(Course course);

    Course findById(Long id);

    List<Course> findAll();

    PaginatedData<Course> findByFilter(CourseFilter courseFilter);

    void deleteById(Long id);
}
