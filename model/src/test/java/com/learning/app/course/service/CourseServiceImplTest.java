package com.learning.app.course.service;

import com.learning.app.category.exception.CategoryNotFoundException;
import com.learning.app.category.service.CategoryService;
import com.learning.app.common.exception.FieldInvalidException;
import com.learning.app.common.model.PaginatedData;
import com.learning.app.course.exception.CourseNotFoundException;
import com.learning.app.course.model.Course;
import com.learning.app.course.model.filter.CourseFilter;
import com.learning.app.course.repository.CourseRepository;
import com.learning.app.review.service.ReviewService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static com.learning.app.commontests.data.CourseData.*;

public class CourseServiceImplTest
{
    private static Validator validator;
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ReviewService reviewService;

    @BeforeClass
    public static void setUpTestClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        courseService = new CourseServiceImpl();

        ((CourseServiceImpl) courseService).courseRepository = courseRepository;
        ((CourseServiceImpl) courseService).categoryService = categoryService;
        ((CourseServiceImpl) courseService).reviewService = reviewService;
        ((CourseServiceImpl) courseService).validator = validator;
    }

    @Test(expected = FieldInvalidException.class)
    public void addCourseWithNullName_ShouldThrowException() throws Exception
    {
        Course course = learnSwift();
        course.setName(null);
        courseService.add(course);
    }


    @Test(expected = FieldInvalidException.class)
    public void addCourseWithLongName_ShouldThrowException() throws Exception
    {
        Course course = learnSwift();
        course.setName("This is a long name that will cause an exception to be thrown");
        courseService.add(course);
    }

    @Test(expected = FieldInvalidException.class)
    public void addCourseWithShortName_ShouldThrowException() throws Exception
    {
        Course course = learnSwift();
        course.setName("A");
        courseService.add(course);
    }

    @Test(expected = CategoryNotFoundException.class)
    public void addCourseWithNonExistingCategory_ShouldThrowException() {
        when(categoryService.findById(1L)).thenThrow(new CategoryNotFoundException());

        Course course = learnJava();
        course.getCategory().setId(1L);

        courseService.add(course);
    }

    @Test(expected = FieldInvalidException.class)
    public void addCourseWithNullURL_ShouldThrowException() throws Exception
    {
        Course course = learnSwift();
        course.setUrl(null);
        courseService.add(course);
    }

    @Test(expected = FieldInvalidException.class)
    public void addCourseWithInvalidURL_ShouldThrowException() throws Exception
    {
        Course course = learnSwift();
        course.setUrl("...");
        courseService.add(course);
    }

    @Test
    public void addValidCourse_ShouldReturnPersistedCourse() throws Exception
    {
        Course course = learnJava();
        when(courseRepository.add(course)).thenReturn(courseWithId(learnJava(), 1L));
        when(categoryService.findById(anyLong())).thenReturn(learnJava().getCategory());

        Course courseAdded = courseService.add(course);

        assertThat(courseAdded.getId(), is(equalTo(1L)));
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCourseWithNullName_ShouldThrowException() throws Exception
    {
        Course course = learnJava();
        course.setName(null);
        courseService.update(courseWithId(course, 1L));
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCourseWithShortName_ShouldThrowException() throws Exception
    {
        Course course = learnJava();
        course.setName("A");
        courseService.update(course);
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCourseWithLongName_ShouldThrowException() throws Exception
    {
        Course course = learnJava();
        course.setName("This is a long name that will cause an exception to be thrown");
        courseService.update(course);
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCourseWithNullUrl_ShouldThrowException() throws Exception
    {
        Course course = learnJava();
        course.setUrl(null);
        courseService.update(course);
    }

    @Test(expected = FieldInvalidException.class)
    public void updateCourseWithInvalidUrl_ShouldThrowException() throws Exception
    {
        Course course = learnJava();
        course.setUrl("...");
        courseService.update(course);
    }

    @Test(expected = CategoryNotFoundException.class)
    public void updateCourseWithNonExistingCategory_ShouldThrowException() throws Exception
    {
        when(courseRepository.idExists(1L)).thenReturn(true);
        when(categoryService.findById(1L)).thenThrow(new CategoryNotFoundException());

        Course course = courseWithId(learnJava(), 1L);
        course.getCategory().setId(1L);
        courseService.update(course);
    }

    @Test(expected = CourseNotFoundException.class)
    public void updateNonExistingCourse_ShouldThrowException() throws Exception
    {
        when(courseRepository.findById(1L)).thenReturn(null);

        courseService.update(courseWithId(learnJava(), 1L));
    }

    @Test
    public void updateValidCourse_ShouldCallUpdateInRepository() throws Exception
    {
        when(courseRepository.idExists(1L)).thenReturn(true);
        when(categoryService.findById(anyLong())).thenReturn(learnJava().getCategory());

        courseService.update(courseWithId(learnJava(), 1L));

        verify(courseRepository).update(courseWithId(learnJava(), 1L));
    }

    @Test
    public void findCourseById_ShouldReturnCorrectCourse() throws Exception
    {
        Course course = courseWithId(learnJava(), 1L);
        when(courseRepository.findById(1L)).thenReturn(course);

        Course courseFound = courseService.findById(1L);
        assertThat(courseFound, is(notNullValue()));
        assertThat(courseFound.getId(), is(equalTo(1L)));
        assertThat(courseFound.getName(), is(equalTo(course.getName())));
    }

    @Test(expected = CourseNotFoundException.class)
    public void findCourseById_ShouldThrowExceptionIfNotFound() throws Exception
    {
        when(courseRepository.findById(999L)).thenReturn(null);

        Course courseFound = courseService.findById(999L);

        assertThat(courseFound, is(nullValue()));
    }

    @Test
    public void findAll_ShouldReturn4() throws Exception
    {
        when(courseRepository.findAll()).thenReturn(courseListWithId());

        List<Course> coursesFound = courseService.findAll();

        assertEquals(coursesFound.size(), 4);
    }

    @Test
    public void findCourseByFilter_ReturnsFilteredPaginatedData() throws Exception
    {
        PaginatedData<Course> courses = new PaginatedData<>(1, Arrays.asList(
                courseWithId(learnJava(), 1L),
                courseWithId(learnPython(), 2L))
        );
        CourseFilter courseFilter = new CourseFilter();
        when(courseRepository.findByFilter(courseFilter))
                .thenReturn(courses);

        PaginatedData<Course> coursesReturned = courseService.findByFilter(courseFilter);

        assertThat(coursesReturned.getNumberOfRows(), is(equalTo(1)));
        assertThat(coursesReturned.getRow(0).getName(), is(equalTo("Learn Java")));
    }

    @Test
    public void deleteExistingCourse_ShouldCallDeleteInRepository() throws Exception
    {
        when(courseRepository.idExists(1L)).thenReturn(true);

        courseService.deleteById(1L);

        verify(courseRepository).delete(1L);
    }

    @Test(expected = CourseNotFoundException.class)
    public void deleteNonExistingCourse_ShouldThrowException() throws Exception
    {
        courseService.deleteById(999L);
    }
}