package com.learning.app.course.model.filter;

import com.learning.app.common.model.filter.GenericFilter;

public class CourseFilter extends GenericFilter
{
    private String name;
    private Long categoryId;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    @Override
    public String toString()
    {
        return "CourseFilter{" +
                "name='" + name + '\'' +
                ", categoryId=" + categoryId +
                "toString()=" + super.toString() +
                '}';
    }
}