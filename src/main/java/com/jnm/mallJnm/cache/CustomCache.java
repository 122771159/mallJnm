package com.jnm.mallJnm.cache;

import jakarta.validation.constraints.NotNull;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class CustomCache extends AbstractValueAdaptingCache {
    private final String name;
    private final ConcurrentMap<Object, Object> store;
    @Nullable
    private final SerializationDelegate serialization;
    private long defaultTtl = 3600000; // 默认1小时

    public CustomCache(String name) {
        this(name, new ConcurrentHashMap<>(256), true);
    }

    public CustomCache(String name, boolean allowNullValues) {
        this(name, new ConcurrentHashMap<>(256), allowNullValues);
    }

    public CustomCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues) {
        this(name, store, allowNullValues, null);
    }

    protected CustomCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues, SerializationDelegate serialization) {
        super(allowNullValues);
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(store, "Store must not be null");
        this.name = name;
        this.store = store;
        this.serialization = serialization;
    }

    @Override
    protected Object lookup(@NotNull Object key) {
        Object obj = this.store.get(key);
        if (obj instanceof CacheObject<?> co) {
            if (co.isExpired()) {
                this.evict(key);
                return null;
            } else {
                return co.get(true);
            }
        } else {
            return obj;
        }
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @NotNull
    @Override
    public Object getNativeCache() {
        return this.store;
    }

    /**
     * 从缓存中获取 key 对应的值，如果缓存没有命中，则添加缓存，
     * 此时可异步地从 callable 中获取对应的值（4.3版本新增）
     * 与缓存标签中的sync属性有关
     */
    @Override
    public <T> T get(Object key, Callable<T> callable) {
        Object obj = this.lookup(key);
        if (obj == null) {
            try {
                T t = callable.call();
                this.put(key, t);
                return t;
            } catch (Exception e) {
                throw new ValueRetrievalException(key, callable, e);
            }
        } else {
            return (T) obj;
        }
    }

    @Override
    public void put(@NotNull Object key, Object value) {
        this.store.put(key, this.toStoreValue(value));
    }

    @Override
    public void evict(@NotNull Object key) {
        this.store.remove(key);
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Object existing = this.store.putIfAbsent(key, this.toStoreValue(value));
        return this.toValueWrapper(existing);
    }

    @NotNull
    protected Object toStoreValue(@Nullable Object userValue, long ttl) {
        Object storeValue = super.toStoreValue(userValue);
        if (this.serialization != null) {
            try {
                return this.serialization.serializeToByteArray(storeValue);
            } catch (Throwable var4) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + userValue + "'. Does it implement Serializable?", var4);
            }
        } else {
            return new CacheObject<>(storeValue, ttl);
        }
    }

    @NotNull
    @Override
    protected Object toStoreValue(@Nullable Object userValue) {
        return toStoreValue(userValue, defaultTtl);
    }

    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (storeValue != null && this.serialization != null) {
            try {
                return super.fromStoreValue(this.serialization.deserializeFromByteArray((byte[])storeValue));
            } catch (Throwable var3) {
                throw new IllegalArgumentException("Failed to deserialize cache value '" + storeValue + "'", var3);
            }
        } else {
            return super.fromStoreValue(storeValue);
        }
    }

    public void setDefaultTtl(long defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    public void put(@NotNull Object key, Object value, long ttl) {
        this.store.put(key, toStoreValue(value, ttl));
    }
}
