package com.learning.app.common.model;

import java.util.List;

/*
 * Generic class to encapsulate data with pagination information
 */

public class PaginatedData<T>
{
    private int numberOfRows;
    private List<T> rows;

    public PaginatedData(int numberOfRows, List<T> rows)
    {
        this.numberOfRows = numberOfRows;
        this.rows = rows;
    }

    public int getNumberOfRows()
    {
        return numberOfRows;
    }

    public List<T> getRows()
    {
        return rows;
    }

    public T getRow(final int index)
    {
        if (index >= rows.size())
        {
            return null;
        }
        return rows.get(index);
    }

    @Override
    public String toString()
    {
        return "PaginatedData [numberOfRows=" + numberOfRows + ", rows=" + rows + "]";
    }
}
