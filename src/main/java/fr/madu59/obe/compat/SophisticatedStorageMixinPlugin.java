package fr.madu59.obe.compat;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import net.neoforged.fml.loading.FMLLoader;

/**
 * Keeps every mixin with a direct Sophisticated class reference behind a
 * loading-time presence check. This class deliberately has no client or
 * Sophisticated imports, so loading the config is safe when the optional mods
 * are absent and on a dedicated server.
 */
public final class SophisticatedStorageMixinPlugin implements IMixinConfigPlugin {
    private static final String SETTINGS_CATEGORY =
            "net.p3pp3rf1y.sophisticatedcore.settings.itemdisplay.ItemDisplaySettingsCategory";
    private static final String SETTINGS_HANDLER =
            "net.p3pp3rf1y.sophisticatedstorage.settings.StorageSettingsHandler";
    private static final String SETTINGS_CATEGORY_INTERNAL = SETTINGS_CATEGORY.replace('.', '/');
    private static final String CHEST_RENDERER =
            "net.p3pp3rf1y.sophisticatedstorage.client.render.ChestRenderer";
    private static final String CHEST_SUB_RENDERER = CHEST_RENDERER + "$ChestSubRenderer";
    private static final String SHULKER_RENDERER =
            "net.p3pp3rf1y.sophisticatedstorage.client.render.ShulkerBoxRenderer";
    private static final String SIX_ARGUMENT_CONSTRUCTOR =
            "(Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lnet/minecraft/nbt/CompoundTag;"
                    + "Ljava/util/function/Consumer;ILjava/util/function/Supplier;)V";
    private static final String SEVEN_ARGUMENT_CONSTRUCTOR =
            "(Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lnet/minecraft/nbt/CompoundTag;"
                    + "Ljava/util/function/Consumer;IZLjava/util/function/Supplier;)V";

    private boolean enabled;
    private boolean constructorBridgeRequired;

    @Override
    public void onLoad(String mixinPackage) {
        var modList = FMLLoader.getLoadingModList();
        enabled = modList.getModFileById("sophisticatedstorage") != null
                && modList.getModFileById("sophisticatedcore") != null;
        if (enabled) {
            constructorBridgeRequired = inspectConstructorBridgeRequirement();
            SophisticatedStorageRendererCompatibility.publish(inspectRendererCompatibility());
        } else {
            SophisticatedStorageRendererCompatibility.publish(
                    SophisticatedStorageRendererCompatibility.Capabilities.unsupported());
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return shouldApplyMixin(enabled, SophisticatedStorageRendererCompatibility.current(),
                constructorBridgeRequired, mixinClassName);
    }

    static boolean shouldApplyMixin(boolean enabled,
            SophisticatedStorageRendererCompatibility.Capabilities capabilities,
            boolean constructorBridgeRequired, String mixinClassName) {
        if (!enabled) {
            return false;
        }
        String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        return switch (simpleName) {
            case "ChestBlockEntityMixin", "ChestRendererMixin", "ChestSubRendererMixin" ->
                    capabilities.chestSupported();
            case "ShulkerBoxBlockEntityMixin", "ShulkerBoxRendererMixin" ->
                    capabilities.shulkerSupported();
            case "ShulkerBoxRendererLegacyShellMixin" -> capabilities.shulkerLayout()
                    == SophisticatedStorageRendererCompatibility.ShulkerLayout.LEGACY_SIX_ARGUMENT_BODY;
            case "ShulkerBoxRendererExtendedShellMixin" -> capabilities.shulkerLayout()
                    == SophisticatedStorageRendererCompatibility.ShulkerLayout.EXTENDED_SEVEN_ARGUMENT_BODY;
            case "StorageBlockEntityMixin" -> capabilities.anyStorageSupported();
            case "StorageSettingsHandlerMixin" -> constructorBridgeRequired;
            default -> true;
        };
    }

    private static SophisticatedStorageRendererCompatibility.Capabilities inspectRendererCompatibility() {
        boolean chestSupported = false;
        var shulkerLayout = SophisticatedStorageRendererCompatibility.ShulkerLayout.UNSUPPORTED;
        try {
            ClassNode renderer = classNode(CHEST_RENDERER);
            ClassNode subRenderer = classNode(CHEST_SUB_RENDERER);
            chestSupported = SophisticatedStorageRendererCompatibility.supportsChest(renderer, subRenderer);
        } catch (Exception ignored) {
            // Unknown or unreadable chest ABI: keep its untouched BER.
        }
        try {
            shulkerLayout = SophisticatedStorageRendererCompatibility.classifyShulker(classNode(SHULKER_RENDERER));
        } catch (Exception ignored) {
            // Unknown or unreadable shulker ABI: keep its untouched BER.
        }
        return new SophisticatedStorageRendererCompatibility.Capabilities(chestSupported, shulkerLayout);
    }

    private static ClassNode classNode(String className) throws Exception {
        return MixinService.getService().getBytecodeProvider().getClassNode(className);
    }

    private static boolean inspectConstructorBridgeRequirement() {
        try {
            ClassNode node = classNode(SETTINGS_CATEGORY);
            Collection<String> constructors = node.methods.stream()
                    .filter(method -> method.name.equals("<init>"))
                    .map(method -> method.desc)
                    .toList();
            return requiresConstructorBridge(constructors, classNode(SETTINGS_HANDLER));
        } catch (Exception ignored) {
            // Preserve Storage's original call if Core's ABI cannot be inspected.
            return false;
        }
    }

    static boolean requiresConstructorBridge(Collection<String> constructorDescriptors) {
        return !constructorDescriptors.contains(SIX_ARGUMENT_CONSTRUCTOR)
                && constructorDescriptors.contains(SEVEN_ARGUMENT_CONSTRUCTOR);
    }

    static boolean requiresConstructorBridge(Collection<String> constructorDescriptors,
            ClassNode storageSettingsHandler) {
        return requiresConstructorBridge(constructorDescriptors)
                && legacyStorageConstructorCallCount(storageSettingsHandler) == 1;
    }

    private static int legacyStorageConstructorCallCount(ClassNode storageSettingsHandler) {
        if (storageSettingsHandler == null) {
            return 0;
        }
        int count = 0;
        for (var method : storageSettingsHandler.methods) {
            if (!method.name.equals("lambda$addItemDisplayCategory$1")) {
                continue;
            }
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode invocation
                        && invocation.owner.equals(SETTINGS_CATEGORY_INTERNAL)
                        && invocation.name.equals("<init>")
                        && invocation.desc.equals(SIX_ARGUMENT_CONSTRUCTOR)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
