package fr.madu59.obe.client.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.SectionPos;

public class ChunkTaskHolder {
    private static Map<SectionPos, List<Runnable>> tasks = new ConcurrentHashMap<>();

    public static void addTask(SectionPos pos, Runnable task) {
        tasks.computeIfAbsent(pos, k -> new ArrayList<>()).add(task);
    }

    public static void executeTasks(SectionPos pos) {
        List<Runnable> taskList = tasks.get(pos);
        if (taskList != null) {
            for (Runnable task : taskList) {
                task.run();
            }
            tasks.remove(pos);
        }
    }
}
