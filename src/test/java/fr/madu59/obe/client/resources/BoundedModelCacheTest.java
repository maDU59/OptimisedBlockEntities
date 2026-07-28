package fr.madu59.obe.client.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class BoundedModelCacheTest {
    @Test
    void evictsTheLeastRecentlyUsedEntry() throws Exception {
        BoundedModelCache<String, String> cache = new BoundedModelCache<>(2);

        assertEquals("a", cache.getOrCompute("a", () -> "a"));
        assertEquals("b", cache.getOrCompute("b", () -> "b"));
        assertEquals("a", cache.getOrCompute("a", () -> "wrong"));
        assertEquals("c", cache.getOrCompute("c", () -> "c"));

        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
        assertTrue(cache.containsKey("c"));
        assertEquals(1, cache.stats().evictions());
        assertEquals(1, cache.stats().hits());
        assertEquals(3, cache.stats().misses());
        assertEquals(3, cache.stats().builds());
    }

    @Test
    void computesOneValueForConcurrentRequestsOfTheSameKey() throws Exception {
        BoundedModelCache<String, String> cache = new BoundedModelCache<>(8);
        AtomicInteger builds = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(8);
        List<java.util.concurrent.Future<String>> futures = new ArrayList<>();

        for (int index = 0; index < 8; index++) {
            futures.add(executor.submit(() -> {
                start.await();
                return cache.getOrCompute("same", () -> {
                    builds.incrementAndGet();
                    return "value";
                });
            }));
        }

        start.countDown();
        for (var future : futures) {
            assertEquals("value", future.get(5, TimeUnit.SECONDS));
        }
        executor.shutdownNow();

        assertEquals(1, builds.get());
        assertEquals(1, cache.stats().builds());
        assertEquals(7, cache.stats().hits());
    }

    @Test
    void failedBuildsAreNotCachedAndClearDropsAllEntries() throws Exception {
        BoundedModelCache<String, String> cache = new BoundedModelCache<>(2);

        assertThrows(IllegalStateException.class,
                () -> cache.getOrCompute("bad", () -> { throw new IllegalStateException("boom"); }));
        assertFalse(cache.containsKey("bad"));

        cache.getOrCompute("good", () -> "value");
        cache.clear();
        assertEquals(0, cache.size());
    }
}
