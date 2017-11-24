package com.learning.app.commontests.data;

import com.learning.app.category.model.Category;
import com.learning.app.course.model.Course;
import org.junit.Ignore;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static com.learning.app.commontests.data.CategoryData.categoryList;
import static com.learning.app.commontests.repository.TestRepositoryUtils.findByPropertyNameAndValue;

@Ignore
public class CourseData
{
    public static List<Course> courseList()
    {
        return Arrays.asList(
                learnSwift(),
                learnJava(),
                learnPython(),
                spanishForBeginners()
        );
    }

    public static List<Course> courseListWithId()
    {
        return Arrays.asList(
                courseWithId(learnSwift(), 1L),
                courseWithId(learnJava(), 2L),
                courseWithId(learnPython(), 3L),
                courseWithId(spanishForBeginners(), 4L)
        );
    }

    public static Course learnSwift()
    {
        return courseWithoutId("Learn Swift", categoryList().get(0));
    }

    public static Course learnJava()
    {
        return courseWithoutId("Learn Java", categoryList().get(0));
    }

    public static Course learnPython()
    {
        return courseWithoutId("Learn Python", categoryList().get(0));
    }

    public static Course spanishForBeginners()
    {
        return courseWithoutId("Spanish for Beginners", categoryList().get
                (1));
    }



    public static Course courseWithId(Course course, Long id)
    {
        course.setId(id);
        return course;
    }

    public static Course courseWithoutId(String name, Category category)
    {
        Course course = new Course(name, "http://test.com");
        course.setCategory(category);
        course.setDescription("This is a course for complete beginners");
        return course;
    }

    public static Course getCourseWithDependencies(Course course, EntityManager em)
    {
        Category managedCategory = findByPropertyNameAndValue(em, Category.class, "name", course.getCategory()
                .getName());
        course.setCategory(managedCategory);

        return course;
    }
}
