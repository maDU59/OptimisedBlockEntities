package fr.madu59.obe.client.compat.vanillin;

import dev.engine_room.vanillin.VanillaVisuals;
import dev.engine_room.vanillin.config.Configurator;
import dev.engine_room.vanillin.config.VisualConfigValue;
import fr.madu59.obe.client.config.Option;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.registry.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Map;

public class VanillinCompat {
    public static void preventConflicts() {
        Map<BlockEntityType<?>, Configurator.ConfiguredBlockEntity<?>> configurator = VanillaVisuals.CONFIGURATOR.blockEntities;
        //boolean supportChest = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.CHEST).getClass() == ModelPart.class;
        boolean supportChest = true;

        var config = VanillinConfigAccess.blockEntityConfig();
        var overrides = VanillinConfigAccess.blockEntityOverrides();

        for(Map.Entry<BlockEntityType<?>, Configurator.ConfiguredBlockEntity<?>> entry : configurator.entrySet()){
            BlockEntityType<?> type = entry.getKey();

            String group = Registry.getGroup(type);
            if(group == null) continue;
            Option<Boolean> option = SettingsManager.GROUP_TOGGLE_SETTINGS.get(group);
            if(option == null) continue;

            Configurator.ConfiguredBlockEntity<?> configured = configurator.get(type);
            String key = configured.configKey();
            VisualConfigValue value = config.get(key).get();
            if (value == VisualConfigValue.DEFAULT) {
                configured.set(
                        !option.getValue() && (supportChest || !group.equals("chest")) ? VisualConfigValue.DEFAULT : VisualConfigValue.DISABLE,
                        overrides.get(key)
                );
            }
        }
    }
}
