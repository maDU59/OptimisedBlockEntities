package fr.madu59.obe.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.platform.PlatformHelper;
import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class SettingsManager {

    public static Map<String, Option<?>> ALL_OPTIONS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = PlatformHelper.getConfigDir().resolve("fwa.json");
    private static Map<String, String> loadedSettings = loadSettings();

    private static Runnable emptyAction = () -> {};
    private static Runnable reloadResourcesAction = () -> {
        ResourceUtil.clearCache();
        Minecraft.getInstance().levelRenderer.allChanged();
    };
    private static Runnable reloadChunksAction = () -> Minecraft.getInstance().levelRenderer.allChanged();

    public static Option<Boolean> EMF_COMPAT = loadOptionWithDefaults("emf_compat",
        "obe.config.option.emf_compat",
        true,
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_CHESTS = loadOptionWithDefaults("optimized_chest",
        "obe.config.option.optimised_chests",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> CHEST_AMBIENT_OCCLUSION = loadOptionWithDefaults("chest_ambient_occlusion",
        "obe.config.option.chest_ao",
        false,
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_BANNERS = loadOptionWithDefaults("optimized_banner",
        "obe.config.option.optimised_banners",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> BANNER_AMBIENT_OCCLUSION = loadOptionWithDefaults("banner_ambient_occlusion",
        "obe.config.option.banner_ao",
        ModCompat.isSodiumLoaded(),
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_SIGNS = loadOptionWithDefaults("optimized_sign",
        "obe.config.option.optimised_signs",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> SIGN_AMBIENT_OCCLUSION = loadOptionWithDefaults("sign_ambient_occlusion",
        "obe.config.option.sign_ao",
        ModCompat.isSodiumLoaded(),
        reloadResourcesAction
    );

    public static Option<Boolean> SIGN_TEXT_CULLING = loadOptionWithDefaults("sign_text_culling",
        "obe.config.option.sign_text_culling",
        true,
        emptyAction
    );

    public static Option<Boolean> OPTIMISED_SHULKER_BOXES = loadOptionWithDefaults("optimized_shulker_box",
        "obe.config.option.optimised_shulker_boxes",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> SHULKER_BOX_AMBIENT_OCCLUSION = loadOptionWithDefaults("shulker_box_ambient_occlusion",
        "obe.config.option.shulker_box_ao",
        false,
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_SKULLS = loadOptionWithDefaults("optimized_skull",
        "obe.config.option.optimised_skulls",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> SKULL_AMBIENT_OCCLUSION = loadOptionWithDefaults("skull_ambient_occlusion",
        "obe.config.option.skull_ao",
        false,
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_BEDS = loadOptionWithDefaults("optimized_bed",
        "obe.config.option.optimised_beds",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> BED_AMBIENT_OCCLUSION = loadOptionWithDefaults("bed_ambient_occlusion",
        "obe.config.option.bed_ao",
        ModCompat.isSodiumLoaded(),
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_BELLS = loadOptionWithDefaults("optimized_bell",
        "obe.config.option.optimised_bells",
        true,
       reloadChunksAction
    );

    public static Option<Boolean> BELL_AMBIENT_OCCLUSION = loadOptionWithDefaults("bell_ambient_occlusion",
        "obe.config.option.bell_ao",
        true,
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_DECORATED_POTS = loadOptionWithDefaults("optimized_decorated_pot",
        "obe.config.option.optimised_decorated_pots",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> DECORATED_POT_AMBIENT_OCCLUSION = loadOptionWithDefaults("decorated_pot_ambient_occlusion",
        "obe.config.option.decorated_pot_ao",
        false,
        reloadResourcesAction
    );

    public static Option<Boolean> OPTIMISED_COPPER_GOLEMS = loadOptionWithDefaults("optimized_copper_golem",
        "obe.config.option.optimised_copper_golems",
        true,
        reloadChunksAction
    );

    public static Option<Boolean> COPPER_GOLEM_AMBIENT_OCCLUSION = loadOptionWithDefaults("copper_golem_ambient_occlusion",
        "obe.config.option.copper_golem_ao",
        ModCompat.isSodiumLoaded(),
        reloadResourcesAction
    );

    public static void saveSettings() {
        Map<String, String> map = toMap(ALL_OPTIONS.values());
        Set<Runnable> actions = new HashSet<Runnable>();

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(map, writer);
            }
            Set<String> allEntries = new HashSet<>();

            allEntries.addAll(map.keySet());
            allEntries.addAll(loadedSettings.keySet());
            for(String key : allEntries){
                if(!Objects.equals(map.get(key), loadedSettings.get(key))){
                    if(SettingsManager.ALL_OPTIONS.containsKey(key)){
                        Runnable action = SettingsManager.ALL_OPTIONS.get(key).getRunnable();
                        if(actions.add(action)) action.run();
                    }
                }
            }
            loadedSettings = map;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> toMap(Collection<Option<?>> options) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Option<?> option : options) {
            if (option.getValue() != option.getDefaultValue()) map.put(option.getId(), option.getValue().toString());
        }
        return map;
    }

    private static Map<String, String> loadSettings() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = GSON.fromJson(reader, type);
            return map;
        } catch (Exception e) {
            OBE.LOGGER.info("[OBE] Config file not found or invalid, using default");
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOptionValue(String key, T defaultValue) {
        if (loadedSettings == null || !loadedSettings.containsKey(key)) return null;
        else if (defaultValue instanceof Enum<?> e){
            return (T) Enum.valueOf(e.getDeclaringClass(), loadedSettings.get(key));
        }
        else if (defaultValue instanceof Float){
            return (T) Float.valueOf(loadedSettings.get(key));
        }
        else if (defaultValue instanceof Double){
            return (T) Double.valueOf(loadedSettings.get(key));
        }
        else if (defaultValue instanceof Integer){
            return (T) Integer.valueOf(loadedSettings.get(key));
        }
        else if (defaultValue instanceof Boolean){
            return (T) (Boolean) Boolean.parseBoolean(loadedSettings.get(key));
        }
        else return null;
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, T defaultValue) {
        return loadOptionWithDefaults(id, name, name, defaultValue, emptyAction);
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, T defaultValue, Runnable action) {
        return loadOptionWithDefaults(id, name, name, defaultValue, action);
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, String description, T defaultValue) {
        return loadOptionWithDefaults(id, name, description, defaultValue, emptyAction);
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, String description, T defaultValue, Runnable action) {
        T optionValue= getOptionValue(id, defaultValue);
        if (optionValue == null) optionValue = defaultValue;
        Option<T> option = new Option<T>(
                id,
                name,
                description,
                optionValue,
                defaultValue,
                action
        );
        return option;
    }
}