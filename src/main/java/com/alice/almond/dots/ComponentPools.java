package com.alice.almond.dots;

import java.util.HashMap;

import com.alice.almond.utils.collections.Array;
import com.alice.almond.utils.collections.Pool;
import com.alice.almond.utils.collections.ReflectionPool;

public class ComponentPools {

    private HashMap<Class<?>, ReflectionPool> pools;
    private int initialSize;
    private int maxSize;

    public ComponentPools(int initialSize, int maxSize) {
        this.pools = new HashMap<Class<?>, ReflectionPool>();
        this.initialSize = initialSize;
        this.maxSize = maxSize;
    }

    public <T> T obtain(Class<T> type) {
        ReflectionPool pool = pools.get(type);

        if (pool == null) {
            pool = new ReflectionPool(type, initialSize, maxSize);
            pools.put(type, pool);
        }

        return (T) pool.obtain();
    }

    public void free(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }

        ReflectionPool pool = pools.get(object.getClass());

        if (pool == null) {
            return; // Ignore freeing an object that was never retained.
        }

        pool.free(object);
    }

    public void freeAll(Array objects) {
        if (objects == null)
            throw new IllegalArgumentException("objects cannot be null.");

        for (int i = 0, n = objects.size; i < n; i++) {
            Object object = objects.get(i);
            if (object == null)
                continue;
            free(object);
        }
    }

    public void clear() {
        for (Pool pool : pools.values()) {
            pool.clear();
        }
    }
}
