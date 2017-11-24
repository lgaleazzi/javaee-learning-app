package com.learning.app.user.repository;


import com.learning.app.common.model.PaginatedData;
import com.learning.app.common.repository.GenericRepository;
import com.learning.app.user.model.User;
import com.learning.app.user.model.filter.UserFilter;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class UserRepository extends GenericRepository<User>
{
    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<User> getPersistentClass()
    {
        return User.class;
    }

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public User findByEmail(String email)
    {
        try
        {
            return (User)em.createQuery("Select e From User e where e.email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e)
        {
            return null;
        }
    }

    public PaginatedData<User> findByFilter(UserFilter userFilter)
    {
        StringBuilder clause = new StringBuilder("WHERE e.id is not null");
        Map<String, Object> queryParameters = new HashMap<>();
        if (userFilter.getName() != null)
        {
            clause.append(" And Upper(e.name) Like Upper(:name)");
            queryParameters.put("name", "%" + userFilter.getName() + "%");
        }
        return findPaginatedDataByParameters(clause.toString(), userFilter.getPaginationData(), queryParameters, "name ASC");
    }

}
