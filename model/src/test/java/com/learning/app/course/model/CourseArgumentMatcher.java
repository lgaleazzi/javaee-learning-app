package com.learning.app.course.model;

import org.mockito.ArgumentMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;

public class CourseArgumentMatcher extends ArgumentMatcher<Course>
{
    private Course expectedCourse;

    public CourseArgumentMatcher(Course expectedCourse)
    {
        this.expectedCourse = expectedCourse;
    }

    public static Course courseEquivalent(Course expectedCourse)
    {
        return argThat(new CourseArgumentMatcher(expectedCourse));
    }

    @Override
    public boolean matches(Object object)
    {
        if(!(object instanceof Course))
        {
            return false;
        }

        Course course = (Course) object;
        assertThat(course.getId(), is(equalTo(expectedCourse.getId())));
        assertThat(course.getName(), is(equalTo(expectedCourse.getName())));
        assertThat(course.getDescription(), is(equalTo(expectedCourse.getDescription())));
        assertThat(course.getUrl(), is(equalTo(expectedCourse.getUrl())));
        assertThat(course.getCategory(), is(equalTo(expectedCourse.getCategory())));

        return true;
    }
}
