package com.learning.app.commontests.db;

import org.junit.Ignore;

import javax.persistence.EntityManager;

@Ignore
public class DBTransactionExecutor
{
    private EntityManager em;

    public DBTransactionExecutor(EntityManager em) {
        this.em = em;
    }

    public <T> T executeCommandWithResult(DBTransaction<T> transaction)
    {
        try {
            em.getTransaction().begin();
            T toReturn = transaction.execute();
            em.getTransaction().commit();
            em.clear();
            return toReturn;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            throw new IllegalStateException(e);
        }
    }

    public void executeCommandWithNoResult(DBTransactionVoid transaction)
    {
        try {
            em.getTransaction().begin();
            transaction.execute();
            em.getTransaction().commit();
            em.clear();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            throw new IllegalStateException(e);
        }
    }
}
