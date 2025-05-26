package com.jnm.mallJnm.cache;


public class CacheObject<V> {
    private V obj;
    private long lastAccess;
    private final long ttl;

    public CacheObject(V obj, long ttl) {
        this.obj = obj;
        this.ttl = ttl;
        this.lastAccess = System.currentTimeMillis();
    }

    public boolean isExpired() {
        if (this.ttl <= 0L) {
            return false;
        } else {
            long expiredTime = this.lastAccess + this.ttl;
            return expiredTime > 0L && expiredTime < System.currentTimeMillis();
        }
    }

    V get(boolean isUpdateLastAccess) {
        if (isUpdateLastAccess) {
            this.lastAccess = System.currentTimeMillis();
        }
        return this.obj;
    }

    public V getValue() {
        return this.obj;
    }
}
