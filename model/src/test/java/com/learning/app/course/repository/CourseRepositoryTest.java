package com.learning.app.course.repository;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.filter.PaginationData;
import com.learning.app.course.model.Course;
import com.learning.app.course.model.filter.CourseFilter;
import com.learning.app.commontests.repository.TestBaseRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.learning.app.commontests.data.CategoryData.*;
import static com.learning.app.commontests.data.CategoryData.categoryList;
import static com.learning.app.commontests.data.CourseData.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CourseRepositoryTest extends TestBaseRepository
{
    private CourseRepository courseRepository;

    @Before
    public void setUp()
    {
        initializeTestDB();

        courseRepository = new CourseRepository();
        courseRepository.em = em;

        loadCategoriesToDB();
    }

    @After
    public void tearDown()
    {
        closeEntityManager();
    }

    @Test
    public void add_ShouldPersistCourse() throws Exception
    {
        Course courseAdded = transactionExecutor.executeCommandWithResult(
                () -> courseRepository.add(getCourseWithDependencies(courseWithoutId("Learn Java", categoryList()
                        .get(0)), em))
        );

        assertThat(courseAdded.getId(), is(notNullValue()));
    }

    @Test
    public void update_ShouldPersistCourse() throws Exception
    {
        Course courseAdded = transactionExecutor.executeCommandWithResult(
                () -> courseRepository.add(getCourseWithDependencies(courseWithoutId("Learn Java", categoryList()
                        .get(0)), em))
        );

        courseAdded.setName("Java for Beginners");
        transactionExecutor.executeCommandWithNoResult(
                () -> courseRepository.update(courseAdded)
        );

        Course courseUpdated = courseRepository.findById(courseAdded.getId());
        assertThat(courseUpdated.getName(), is(equalTo("Java for Beginners")));
    }

    @Test
    public void findAll_ShouldReturn4() throws Exception
    {
        List<Course> courseListWithDependencies = new ArrayList<>();
        courseList()
                .forEach(course ->
                        courseListWithDependencies.add(getCourseWithDependencies(course, em))
                );

        transactionExecutor.executeCommandWithNoResult(
                () -> courseListWithDependencies.forEach(courseRepository::add)
        );

        assertEquals(courseRepository.findAll().size(), 4);
    }

    @Test
    public void findByFilter_ShouldReturnFilteredAndPaginatedData()
    {
        List<Course> courseListWithDependencies = new ArrayList<>();
        courseList()
                .forEach(course ->
                    courseListWithDependencies.add(getCourseWithDependencies(course, em))
                );

       transactionExecutor.executeCommandWithNoResult(
                () -> courseListWithDependencies.forEach(courseRepository::add)
        );

        CourseFilter courseFilter = new CourseFilter();
        courseFilter.setName("Learn");
        courseFilter.setPaginationData(new PaginationData(0, 2, "name", PaginationData.OrderMode.ASCENDING));

        PaginatedData<Course> result = courseRepository.findByFilter(courseFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getName(), is(equalTo("Learn Java")));
        assertThat(result.getRow(1).getName(), is(equalTo("Learn Python")));
    }

    @Test
    public void findById_ShouldReturnNullIfIdNotExist() throws Exception
    {
        Course course = courseRepository.findById(999L);

        assertThat(course, is(nullValue()));
    }


    @Test
    public void findById_ShouldReturnCourseIfExist() throws Exception
    {
        Course courseAdded = transactionExecutor.executeCommandWithResult(
                () -> courseRepository.add(getCourseWithDependencies(courseWithoutId("Learn Java", categoryList()
                        .get(0)), em))
        );

        Course courseFoundById = courseRepository.findById(courseAdded.getId());

        assertThat(courseFoundById, is(equalTo(courseAdded)));
    }

    @Test
    public void delete_ShouldRemoveCourse() throws Exception
    {
        Course courseAdded = transactionExecutor.executeCommandWithResult(
                () -> courseRepository.add(getCourseWithDependencies(courseWithoutId("Learn Java", categoryList()
                        .get(0)), em))
        );

        courseRepository.delete(courseAdded.getId());

        assertThat(courseRepository.findById(courseAdded.getId()), is(nullValue()));
    }

    private void loadCategoriesToDB()
    {
        transactionExecutor.executeCommandWithNoResult(
                () -> categoryList().forEach(em::persist)
        );
    }

}