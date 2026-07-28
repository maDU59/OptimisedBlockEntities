package fr.madu59.obe.client.resources;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small synchronized LRU cache intended for models built during section
 * compilation. Synchronizing construction makes one build atomic per key and
 * avoids duplicate worker-thread builds.
 */
public final class BoundedModelCache<K, V> {
    private final int maximumSize;
    private final LinkedHashMap<K, V> entries;
    private long hits;
    private long misses;
    private long builds;
    private long evictions;

    public BoundedModelCache(int maximumSize) {
        if (maximumSize < 1) {
            throw new IllegalArgumentException("maximumSize must be positive");
        }
        this.maximumSize = maximumSize;
        entries = new LinkedHashMap<>(16, 0.75F, true);
    }

    public synchronized V getOrCompute(K key, Factory<V> factory) throws Exception {
        V existing = entries.get(key);
        if (existing != null) {
            hits++;
            return existing;
        }

        misses++;
        V value = factory.create();
        if (value == null) {
            throw new IllegalStateException("Model cache factories may not return null");
        }
        builds++;
        entries.put(key, value);
        evictIfNeeded();
        return value;
    }

    public synchronized V getIfPresent(K key) {
        V value = entries.get(key);
        if (value == null) {
            misses++;
        } else {
            hits++;
        }
        return value;
    }

    public synchronized boolean containsKey(K key) {
        return entries.containsKey(key);
    }

    public synchronized int size() {
        return entries.size();
    }

    public int maximumSize() {
        return maximumSize;
    }

    public synchronized void clear() {
        entries.clear();
    }

    public synchronized Stats stats() {
        return new Stats(hits, misses, builds, evictions, entries.size(), maximumSize);
    }

    private void evictIfNeeded() {
        while (entries.size() > maximumSize) {
            Iterator<Map.Entry<K, V>> iterator = entries.entrySet().iterator();
            iterator.next();
            iterator.remove();
            evictions++;
        }
    }

    @FunctionalInterface
    public interface Factory<V> {
        V create() throws Exception;
    }

    public record Stats(long hits, long misses, long builds, long evictions, int size, int maximumSize) {}
}
