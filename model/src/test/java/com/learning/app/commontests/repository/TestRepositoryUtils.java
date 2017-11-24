package com.learning.app.commontests.repository;


import javax.persistence.EntityManager;

public class TestRepositoryUtils
{
    private TestRepositoryUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T findByPropertyNameAndValue(EntityManager em, Class<T> clazz,
                                                   String propertyName, String propertyValue) {
        return (T) em
                .createQuery("Select o From " + clazz.getSimpleName() +
                        " o Where o." + propertyName + " = :propertyValue")
                .setParameter("propertyValue", propertyValue)
                .getSingleResult();
    }

}
