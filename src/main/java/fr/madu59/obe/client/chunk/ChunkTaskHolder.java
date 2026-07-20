package fr.madu59.obe.client.chunk;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.core.SectionPos;

public class ChunkTaskHolder {
    private static final Map<SectionPos, Queue<Runnable>> tasks = new ConcurrentHashMap<>();

    public static void addTask(SectionPos pos, Runnable task) {
        tasks.computeIfAbsent(pos, k -> new ConcurrentLinkedQueue<>()).add(task);
    }

    public static void executeTasks(SectionPos pos) {
        Queue<Runnable> taskQueue = tasks.remove(pos);
        
        if (taskQueue != null) {
            Runnable task;
            while ((task = taskQueue.poll()) != null) {
                task.run();
            }
        }
    }
}
