package it.ddalpra.acme.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.quarkus.runtime.annotations.RegisterForReflection;

import org.primefaces.cache.CacheProvider;

/**
 * Caffeine Cache Provider
 */
@RegisterForReflection
public class CaffeineCacheProvider implements CacheProvider {

    private Cache<String, Object> cache;

    public CaffeineCacheProvider() {
        this.cache = Caffeine.newBuilder()
                    .maximumSize(200)
                    .build();
    }

    @Override
    public Object get(String region, String key) {
        return cache.getIfPresent(region + key);
    }

    @Override
    public void put(String region, String key, Object object) {
        cache.put(region + key, object);
    }

    @Override
    public void remove(String region, String key) {
        cache.invalidate(region + key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    public Cache<String, Object> getCache() {
        return cache;
    }

    public void setCache(Cache<String, Object> cache) {
        this.cache = cache;
    }
}   