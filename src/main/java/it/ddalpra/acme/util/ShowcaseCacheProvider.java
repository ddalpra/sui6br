package it.ddalpra.acme.util;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import org.omnifaces.cdi.Startup;

import org.primefaces.cache.CacheProvider;


@Startup
@Named
public class ShowcaseCacheProvider {
    private CacheProvider cacheProvider;

    @PostConstruct
    public void init() {
        cacheProvider = new CaffeineCacheProvider();
        System.out.println("Initialized ShowcaseCacheProvider with Caffeine");
    }

    public CacheProvider getCacheProvider() {
        return cacheProvider;
    }
}