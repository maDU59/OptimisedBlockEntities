package fr.madu59.obe.config.configscreen;

import fr.madu59.obe.OBE;
import fr.madu59.obe.config.Option;
import fr.madu59.obe.config.SettingsManager;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.minecraft.network.chat.Component;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.minecraft.resources.Identifier;

@ConfigEntryPointForge(OBE.MOD_ID)
public class SodiumIntegration implements ConfigEntryPoint {

    private final StorageEventHandler handler =  new StorageEventHandler() {
        public void afterSave(){
            SettingsManager.saveSettings();
        }
    };
    
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
        .setNonTintedIcon(Identifier.parse("obe:icon.png"))
        .addPage(builder.createOptionPage()
            .setName(Component.literal("Settings"))
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.general"))
                .addOption(createBooleanOption(builder, SettingsManager.MOD_TOGGLE))
                .addOption(createBooleanOption(builder, SettingsManager.EMF_COMPAT))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.chests"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_CHESTS))
                .addOption(createBooleanOption(builder, SettingsManager.CHEST_AMBIENT_OCCLUSION))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.banners"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_BANNERS))
                .addOption(createBooleanOption(builder, SettingsManager.BANNER_AMBIENT_OCCLUSION))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.signs"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_SIGNS))
                .addOption(createBooleanOption(builder, SettingsManager.SIGN_AMBIENT_OCCLUSION))
                .addOption(createBooleanOption(builder, SettingsManager.SIGN_TEXT_CULLING))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.shulker_boxes"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_SHULKER_BOXES))
                .addOption(createBooleanOption(builder, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.skulls"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_SKULLS))
                .addOption(createBooleanOption(builder, SettingsManager.SKULL_AMBIENT_OCCLUSION))
            )

            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.beds"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_BEDS))
                .addOption(createBooleanOption(builder, SettingsManager.BED_AMBIENT_OCCLUSION))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.bells"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_BELLS))
                .addOption(createBooleanOption(builder, SettingsManager.BELL_AMBIENT_OCCLUSION))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.decorated_pots"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_DECORATED_POTS))
                .addOption(createBooleanOption(builder, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.copper_golems"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_COPPER_GOLEMS))
                .addOption(createBooleanOption(builder, SettingsManager.COPPER_GOLEM_AMBIENT_OCCLUSION))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.shelves"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_SHELVES))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.lecterns"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_LECTERNS))
            )
            
            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.campfires"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_CAMPFIRES))
            )

            .addOptionGroup(builder.createOptionGroup()
                .setName(Component.translatable("obe.config.category.beacons"))
                .addOption(createBooleanOption(builder, SettingsManager.OPTIMISED_BEACONS))
            )
        );
    }

    public OptionBuilder createBooleanOption(ConfigBuilder builder, Option<Boolean> option){
        return builder.createBooleanOption(Identifier.parse("obe:"+option.getId()))
        .setName(Component.translatable(option.getName()))
        .setTooltip(Component.translatable(option.getDescription()))
        .setStorageHandler(this.handler)
        .setBinding(option::setValue, option::getValue)
        .setDefaultValue(option.getDefaultValue())
        .setApplyHook(configState -> option.getRunnable());
    }
}