package com.learning.app.course.service;


import com.learning.app.category.model.Category;
import com.learning.app.category.service.CategoryService;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.utils.DataValidation;
import com.learning.app.course.exception.CourseNotFoundException;
import com.learning.app.course.model.Course;
import com.learning.app.course.model.filter.CourseFilter;
import com.learning.app.course.repository.CourseRepository;
import com.learning.app.review.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.List;

@Stateless
public class CourseServiceImpl implements CourseService
{
    @Inject
    CourseRepository courseRepository;

    @Inject
    Validator validator;

    @Inject
    CategoryService categoryService;

    @Inject
    ReviewService reviewService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Course add(Course course)
    {
        setCourseCategory(course);
        DataValidation.validateEntityFields(validator, course);

        return courseRepository.add(course);
    }

    @Override
    public void update(Course course)
    {
        DataValidation.validateEntityFields(validator, course);

        if (!courseRepository.idExists(course.getId()))
        {
            throw new CourseNotFoundException();
        }

        setCourseCategory(course);

        courseRepository.update(course);
    }

    @Override
    public Course findById(Long id)
    {
        Course course = courseRepository.findById(id);
        if (course == null)
        {
            throw new CourseNotFoundException();
        }
        return course;
    }

    @Override
    public List<Course> findAll()
    {
        return courseRepository.findAll();
    }

    @Override
    public PaginatedData<Course> findByFilter(CourseFilter courseFilter)
    {
        return courseRepository.findByFilter(courseFilter);
    }

    @Override
    public void deleteById(Long id)
    {
        if (!courseRepository.idExists(id))
        {
            throw new CourseNotFoundException();
        }

        //delete reviews associated with this course
        reviewService.deleteByCourseId(id);
        //delete course
        courseRepository.delete(id);
    }

    //The course object converted from json only contains the category id
    //This method retrieves the full category object and links it to the course object
    private void setCourseCategory(Course course)
    {
        Category category = categoryService.findById(course.getCategory().getId());
        course.setCategory(category);
    }
}
