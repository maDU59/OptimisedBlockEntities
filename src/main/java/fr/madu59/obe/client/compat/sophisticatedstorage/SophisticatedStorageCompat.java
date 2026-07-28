package fr.madu59.obe.client.compat.sophisticatedstorage;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.compat.sophisticatedstorage.chest.SophisticatedChestAppearanceResolver;
import fr.madu59.obe.client.compat.sophisticatedstorage.chest.SophisticatedChestModelProvider;
import fr.madu59.obe.client.compat.sophisticatedstorage.chest.SophisticatedChestDynamicRenderPredicate;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.compat.sophisticatedstorage.shulker.SophisticatedShulkerModelProvider;
import fr.madu59.obe.client.compat.sophisticatedstorage.shulker.SophisticatedShulkerDynamicRenderPredicate;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import fr.madu59.obe.compat.SophisticatedStorageRendererCompatibility;

/** Typed client bootstrap, guarded by renderer capabilities detected at mixin load. */
public final class SophisticatedStorageCompat {
    public static final String CHEST_GROUP = "sophisticated_storage_chest";
    public static final String SHULKER_GROUP = "sophisticated_storage_shulker_box";

    private SophisticatedStorageCompat() {}

    public static void init() {
        if (SophisticatedStorageRendererCompatibility.chestSupported()) {
            registerChest();
        } else {
            OBE.LOGGER.warn("Sophisticated Storage chest renderer ABI is unknown; using its untouched BER");
        }
        if (SophisticatedStorageRendererCompatibility.shulkerSupported()) {
            registerShulker();
        } else {
            OBE.LOGGER.warn("Sophisticated Storage shulker renderer ABI is unknown; using its untouched BER");
        }

        SophisticatedStorageLiveValidator.initIfRequested();
        OBE.LOGGER.info("Loaded Sophisticated Storage compatibility: chest={}, shulker={}",
                SophisticatedStorageRendererCompatibility.chestSupported(),
                SophisticatedStorageRendererCompatibility.current().shulkerLayout());
    }

    private static void registerChest() {
        RegistryApi.registerGroup(CHEST_GROUP);
        RegistryApi.registerBlockEntityType(ModBlocks.CHEST_BLOCK_ENTITY_TYPE.get(), CHEST_GROUP);
        RegistryApi.registerSpecialBakedModelProvider(
                ModBlocks.CHEST_BLOCK_ENTITY_TYPE.get(),
                new SophisticatedChestModelProvider(new SophisticatedChestAppearanceResolver(
                        () -> SettingsManager.SOPHISTICATED_CHEST_AMBIENT_OCCLUSION.getValue()))
        );
        RegistryApi.registerDynamicRenderPredicate(
                ModBlocks.CHEST_BLOCK_ENTITY_TYPE.get(), SophisticatedChestDynamicRenderPredicate.INSTANCE);
    }

    private static void registerShulker() {
        RegistryApi.registerGroup(SHULKER_GROUP);
        RegistryApi.registerBlockEntityType(ModBlocks.SHULKER_BOX_BLOCK_ENTITY_TYPE.get(), SHULKER_GROUP);
        RegistryApi.registerSpecialBakedModelProvider(
                ModBlocks.SHULKER_BOX_BLOCK_ENTITY_TYPE.get(),
                new SophisticatedShulkerModelProvider(
                        () -> SettingsManager.SOPHISTICATED_SHULKER_AMBIENT_OCCLUSION.getValue())
        );
        RegistryApi.registerDynamicRenderPredicate(
                ModBlocks.SHULKER_BOX_BLOCK_ENTITY_TYPE.get(), SophisticatedShulkerDynamicRenderPredicate.INSTANCE);
    }
}
