package com.example.m.gameengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M
 * Simple class for pooling (recycling short lived objects to avoid constructing and destroying)
 */
public abstract class Pool<T>
{
    private List<T> items = new ArrayList<T>();

    protected abstract T newItem();

    public T obtain()
    {
        int last = items.size() - 1;
        if(last == -1) return newItem();
        return items.remove(last);
    }

    public void free(T item)
    {
        items.add(item);
    }
}
