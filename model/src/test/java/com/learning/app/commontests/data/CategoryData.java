package com.learning.app.commontests.data;


import com.learning.app.category.model.Category;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.List;

@Ignore
public class CategoryData
{
    public static List<Category> categoryList()
    {
        return Arrays.asList(
                new Category("Coding"),
                new Category("Languages"),
                new Category("Project Management")
        );
    }

    public static List<Category> categoryListWithId()
    {
        return Arrays.asList(
                categoryWithId("Coding", 1L),
                categoryWithId("Languages", 2L),
                categoryWithId("Project Management", 3L)
        );
    }

    public static Category categoryWithId(String name, Long id) {
        Category newCategory = new Category(name);
        newCategory.setId(id);
        return newCategory;
    }
}
