package com.learning.app.user.model.filter;

import com.learning.app.common.model.filter.GenericFilter;

public class UserFilter extends GenericFilter
{
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "UserFilter [name=" + name + ", toString()=" + super.toString() + "]";
    }
}
