package fr.madu59.obe.client.compat.blacklist;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.madu59.obe.client.platform.PlatformHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class Blacklist {
    private static final Set<Identifier> BLOCKS_BLACKLIST = new HashSet<>();
    private static final Map<String, Set<String>> MODS_BLACKLIST = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = PlatformHelper.getConfigDir().resolve("obe-blacklist.json");

    public static void load() {
        BLOCKS_BLACKLIST.clear();
        MODS_BLACKLIST.clear();
        loadFromResource();
        loadFromConfig();
    }

    private static void loadFromResource() {
        Identifier resourcePath = Identifier.fromNamespaceAndPath("obe", "blacklist.json");
        Minecraft.getInstance().getResourceManager().getResource(resourcePath).ifPresent(resource -> {
            try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                BlacklistData data = GSON.fromJson(reader, BlacklistData.class);
                applyData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void loadFromConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String content = Files.readString(CONFIG_PATH);
                BlacklistData data = GSON.fromJson(content, BlacklistData.class);
                applyData(data);
            }
        } catch (Exception e) {
            System.err.println("[OBE] Failed to load blacklist: " + e.getMessage());
        }
    }

    private static void applyData(BlacklistData data){
        for(Map.Entry<String, List<String>> mod : data.mods.entrySet()){
            MODS_BLACKLIST.put(mod.getKey(), new HashSet<>(mod.getValue()));
        }
        for(String block : data.blocks){
            Identifier id = Identifier.tryParse(block);
            BLOCKS_BLACKLIST.add(id);
        }
    }

    public static boolean isBlacklisted(BlockState state, @Nullable String group) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String mod = id.getNamespace();
        return BLOCKS_BLACKLIST.contains(id) || (group != null && MODS_BLACKLIST.getOrDefault(group, Collections.emptySet()).contains(mod));
    }
}
