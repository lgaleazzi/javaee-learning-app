package com.learning.app.category.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "CATEGORY")
public class Category implements Serializable
{
    private static final long serialVersionUID = 4065323491380087970L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 30)
    @Column(unique = true)
    private String name;

    public Category()
    {
    }

    public Category(String name)
    {
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Category category = (Category)o;

        if (id != null ? !id.equals(category.id) : category.id != null)
            return false;
        return name != null ? name.equals(category.name) : category.name == null;

    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}