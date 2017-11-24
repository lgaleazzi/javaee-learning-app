package com.learning.app.commontests.repository;

import com.learning.app.category.model.Category;
import com.learning.app.course.model.Course;
import org.junit.Ignore;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

@Ignore
@Stateless
public class TestRepositoryEJB
{
    @PersistenceContext
    private EntityManager em;

    private static final List<Class<?>> ENTITIES_TO_REMOVE = Arrays.asList(Category.class, Course.class);

    public void deleteAll() {
        for (Class<?> entityClass : ENTITIES_TO_REMOVE) {
            deleteAllForEntity(entityClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteAllForEntity(Class<?> entityClass) {
        List<Object> rows = em.createQuery("Select e From " + entityClass.getSimpleName() + " e").getResultList();
        for (Object row : rows) {
            em.remove(row);
        }
    }
}
