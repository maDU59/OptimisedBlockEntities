package fr.madu59.obe.client.compat.vanillin;

import dev.engine_room.vanillin.FabricVanillinConfig;
import dev.engine_room.vanillin.config.ModOverrides;
import dev.engine_room.vanillin.config.VisualConfigValue;
import dev.engine_room.vanillin.config.VisualOverride;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

final class VanillinConfigAccess {
    private static final Field  CONFIG;
    private static final Field  OVERRIDES;

    static {
        try {
            CONFIG = FabricVanillinConfig.class.getDeclaredField("config");
            CONFIG.setAccessible(true);
            OVERRIDES = FabricVanillinConfig.class.getDeclaredField("overrides");
            OVERRIDES.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, VisualConfigValue> blockEntityConfig() {
        try { return ((FabricVanillinConfig.Config) CONFIG.get(FabricVanillinConfig.INSTANCE)).blockEntities; }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }

    @SuppressWarnings("unchecked")
    static Map<String, List<VisualOverride>> blockEntityOverrides() {
        try { return ((ModOverrides) OVERRIDES.get(FabricVanillinConfig.INSTANCE)).blockEntities(); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }
}