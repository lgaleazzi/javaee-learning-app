package com.learning.app.common.repository;

import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.model.filter.PaginationData;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/*
 * Parent class of repository classes
 */

public abstract class GenericRepository<T>
{

    protected abstract Class<T> getPersistentClass();

    protected abstract EntityManager getEntityManager();

    public T add(T entity)
    {
        getEntityManager().persist(entity);
        return entity;
    }

    public T findById(Long id)
    {
        if (id == null)
        {
            return null;
        }
        return getEntityManager().find(getPersistentClass(), id);
    }

    public void update(T entity)
    {
        getEntityManager().merge(entity);
    }

    public void delete(Long id)
    {
        getEntityManager().remove(
                getEntityManager().find(getPersistentClass(), id)
        );
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll()
    {
        return getEntityManager().createQuery(
                "Select e From " + getPersistentClass().getSimpleName() + " e Order by e.id")
                .getResultList();
    }

    public boolean idExists(Long id)
    {
        return getEntityManager()
                .createQuery("Select 1 From " + getPersistentClass().getSimpleName() + " e where e.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList().size() > 0;
    }

    //query data set filtered by parameters and return paginated data
    @SuppressWarnings("unchecked")
    protected PaginatedData<T> findPaginatedDataByParameters(String clause, PaginationData paginationData,
                                                             Map<String, Object> queryParameters, String defaultSortFieldWithDirection)
    {
        //Build query
        String clauseSort = "Order by e." + getSortField(paginationData, defaultSortFieldWithDirection);
        Query queryEntities = getEntityManager().createQuery(
                "Select e From " + getPersistentClass().getSimpleName()
                        + " e " + clause + " " + clauseSort
        );

        //set all parameters
        applyQueryParametersOnQuery(queryParameters, queryEntities);
        //set first result and max results according to pagination data
        applyPaginationOnQuery(paginationData, queryEntities);

        List<T> entities = queryEntities.getResultList();

        //create paginated data with number of entities and list of entities
        PaginatedData<T> paginatedData = new PaginatedData<T>(countWithFilter(clause, queryParameters), entities);

        return paginatedData;
    }

    private int countWithFilter(String clause, Map<String, Object> queryParameters)
    {
        Query queryCount = getEntityManager().createQuery(
                "Select count(e) From " + getPersistentClass().getSimpleName()
                        + " e " + clause
        );

        applyQueryParametersOnQuery(queryParameters, queryCount);

        int count = ((Long)queryCount.getSingleResult()).intValue();

        return count;
    }

    private void applyPaginationOnQuery(PaginationData paginationData, Query query)
    {
        if (paginationData != null)
        {
            query.setFirstResult(paginationData.getFirstResult());
            query.setMaxResults(paginationData.getMaxResults());
        }
    }

    private String getSortField(PaginationData paginationData, String defaultSortField)
    {
        if (paginationData == null || paginationData.getOrderField() == null)
        {
            return defaultSortField;
        }
        return paginationData.getOrderField() + " " + getSortDirection(paginationData);
    }

    private String getSortDirection(PaginationData paginationData)
    {
        return paginationData.isAscending() ? "ASC" : "DESC";
    }

    protected void applyQueryParametersOnQuery(Map<String, Object> queryParameters, Query query)
    {
        for (Map.Entry<String, Object> entryMap : queryParameters.entrySet())
        {
            query.setParameter(entryMap.getKey(), entryMap.getValue());
        }
    }

}
