package com.jnm.mallJnm.cache;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class CustomCacheManager implements CacheManager, BeanClassLoaderAware {
    private final ConcurrentMap<String, Cache> cacheMap;
    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private boolean storeByValue = false;
    @Nullable
    private SerializationDelegate serialization;

    public CustomCacheManager(){
        cacheMap = new ConcurrentHashMap<>(16);
    }

    public CustomCacheManager(String... cacheNames) {
        this();
        this.setCacheNames(Arrays.asList(cacheNames));
    }

    public void setCacheNames(@Nullable Collection<String> cacheNames) {
        if (cacheNames != null) {
            for (String name : cacheNames) {
                this.cacheMap.put(name, this.createCustomCache(name));
            }
            this.dynamic = false;
        } else {
            this.dynamic = true;
        }
    }

    public void setAllowNullValues(boolean allowNullValues) {
        if (allowNullValues != this.allowNullValues) {
            this.allowNullValues = allowNullValues;
            this.recreateCaches();
        }
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    public void setStoreByValue(boolean storeByValue) {
        if (storeByValue != this.storeByValue) {
            this.storeByValue = storeByValue;
            this.recreateCaches();
        }
    }

    public boolean isStoreByValue() {
        return this.storeByValue;
    }

    @Override
    public void setBeanClassLoader(@NotNull ClassLoader classLoader) {
        this.serialization = new SerializationDelegate(classLoader);
        if (this.isStoreByValue()) {
            this.recreateCaches();
        }
    }

    @NotNull
    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    @Override
    public Cache getCache(@NotNull String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            synchronized(this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = this.createCustomCache(name);
                    this.cacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }

    private void recreateCaches() {
        for (Map.Entry<String, Cache> stringCacheEntry : this.cacheMap.entrySet()) {
            stringCacheEntry.setValue(this.createCustomCache(stringCacheEntry.getKey()));
        }
    }

    private Cache createCustomCache(String name) {
        SerializationDelegate actualSerialization = this.isStoreByValue() ? this.serialization : null;
        return new CustomCache(name, new ConcurrentHashMap<>(256), this.isAllowNullValues(), actualSerialization);
    }

    public void setDefaultTtl(String cacheName, long ttl) {
        Cache cache = this.getCache(cacheName);
        if (cache instanceof CustomCache) {
            ((CustomCache) cache).setDefaultTtl(ttl);
        }
    }

    public Cache getCache(String name, long ttl) {
        Cache cache = this.getCache(name);
        if (cache instanceof CustomCache) {
            ((CustomCache) cache).setDefaultTtl(ttl);
        }
        return cache;
    }
}
