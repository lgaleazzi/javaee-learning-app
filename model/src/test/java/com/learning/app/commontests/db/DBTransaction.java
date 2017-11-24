package com.learning.app.commontests.db;

import org.junit.Ignore;

@Ignore
public interface DBTransaction<T>
{
    T execute();
}
