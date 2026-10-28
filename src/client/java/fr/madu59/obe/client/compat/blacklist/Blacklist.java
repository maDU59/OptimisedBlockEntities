package fr.madu59.obe.client.compat.blacklist;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.platform.PlatformHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.state.BlockState;

public class Blacklist {
    private static final Set<ResourceLocation> BLOCKS_BLACKLIST = new HashSet<>();
    private static final Map<String, Set<String>> MODS_BLACKLIST = new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = PlatformHelper.getConfigDir().resolve("obe-blacklist.json");

    private static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath("obe", "blacklist.json");

    public static void load() {
        BLOCKS_BLACKLIST.clear();
        MODS_BLACKLIST.clear();
        loadFromResource();
        loadFromConfig();
    }

    private static void loadFromResource() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        for(Resource resource : resourceManager.getResourceStack(RESOURCE_LOCATION)) {
            try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                BlacklistData data = GSON.fromJson(reader, BlacklistData.class);
                applyData(data);
            } catch (Exception e) {
                OBE.LOGGER.error("Failed to load blacklist from resource: {}", e);
            }
        }
    }

    private static void loadFromConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String content = Files.readString(CONFIG_PATH);
                BlacklistData data = GSON.fromJson(content, BlacklistData.class);
                applyData(data);
            }
        } catch (Exception e) {
            OBE.LOGGER.error("Failed to load blacklist from config: {}", e);
        }
    }

    private static void applyData(BlacklistData data){
        if(data == null) return;

        for(Map.Entry<String, List<String>> mod : data.mods.entrySet()){
            for(String group : mod.getValue()){
                MODS_BLACKLIST.computeIfAbsent(group, k -> new HashSet<>()).add(mod.getKey());
            }
        }

        for(String block : data.blocks){
            if (block.endsWith(":*")) {
                MODS_BLACKLIST.computeIfAbsent("*", k -> new HashSet<>()).add(block.substring(0, block.length() - 2));
            }
            else{
                ResourceLocation id = ResourceLocation.tryParse(block);
                if(id != null) BLOCKS_BLACKLIST.add(id);
                else OBE.LOGGER.warn("Invalid block id in blacklist: {}", block);
            }
        }
    }

    public static boolean isBlacklisted(BlockState state, @Nullable String group) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String mod = id.getNamespace();

        Set<String> mods = MODS_BLACKLIST.get("*");
        Set<String> modsForGroup = null;
        if(group != null){
            modsForGroup = MODS_BLACKLIST.get(group);
        }

        return BLOCKS_BLACKLIST.contains(id) || (modsForGroup != null && modsForGroup.contains(mod)) || (mods != null && mods.contains(mod));
    }
}
