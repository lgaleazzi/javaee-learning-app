package com.learning.app.review.model.filter;

import com.learning.app.common.model.filter.GenericFilter;

public class ReviewFilter extends GenericFilter
{
    private Long userId;
    private Long courseId;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    @Override
    public String toString()
    {
        return "ReviewFilter{" +
                "userId=" + userId +
                ", courseId=" + courseId +
                '}';
    }
}
