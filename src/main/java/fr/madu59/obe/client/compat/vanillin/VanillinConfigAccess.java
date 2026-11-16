package fr.madu59.obe.client.compat.vanillin;

import dev.engine_room.vanillin.ForgeVanillinConfig;
import dev.engine_room.vanillin.config.VisualConfigValue;
import dev.engine_room.vanillin.config.VisualOverride;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

final class VanillinConfigAccess {
    private static final Object BLOCK_ENTITY_SECTION;
    private static final Method CONFIG;
    private static final Method OVERRIDES;

    static {
        try {
            Field field = ForgeVanillinConfig.class.getDeclaredField("blockEntities");
            field.setAccessible(true);
            BLOCK_ENTITY_SECTION = field.get(ForgeVanillinConfig.INSTANCE);

            Class<?> section = Class.forName("dev.engine_room.vanillin.ForgeVanillinConfig$ConfigSection");
            CONFIG = section.getDeclaredMethod("config");
            CONFIG.setAccessible(true);
            OVERRIDES = section.getDeclaredMethod("overrides");
            OVERRIDES.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, ForgeConfigSpec.EnumValue<VisualConfigValue>> blockEntityConfig() {
        try { return (Map<String, ForgeConfigSpec.EnumValue<VisualConfigValue>>) CONFIG.invoke(BLOCK_ENTITY_SECTION); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }

    @SuppressWarnings("unchecked")
    static Map<String, List<VisualOverride>> blockEntityOverrides() {
        try { return (Map<String, List<VisualOverride>>) OVERRIDES.invoke(BLOCK_ENTITY_SECTION); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }
}