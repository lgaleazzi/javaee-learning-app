package com.learning.app.commontests.repository;

import com.learning.app.commontests.db.DBTransactionExecutor;
import org.junit.Ignore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Ignore
public class TestBaseRepository
{
    private EntityManagerFactory emf;
    protected EntityManager em;
    protected DBTransactionExecutor transactionExecutor;

    public void initializeTestDB() {
        emf = Persistence.createEntityManagerFactory("testPersistenceUnit");
        em = emf.createEntityManager();

        transactionExecutor = new DBTransactionExecutor(em);
    }

    protected void closeEntityManager() {
        em.close();
        emf.close();
    }
}
