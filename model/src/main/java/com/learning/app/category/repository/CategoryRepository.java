package com.learning.app.category.repository;

import com.learning.app.category.model.Category;
import com.learning.app.common.repository.GenericRepository;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CategoryRepository extends GenericRepository<Category>
{
    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<Category> getPersistentClass()
    {
        return Category.class;
    }

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }
}
