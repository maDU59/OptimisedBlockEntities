package fr.madu59.obe.compat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

class SophisticatedStorageMixinPluginTest {
    private static final String SIX_ARGUMENT =
            "(Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lnet/minecraft/nbt/CompoundTag;"
                    + "Ljava/util/function/Consumer;ILjava/util/function/Supplier;)V";
    private static final String SEVEN_ARGUMENT =
            "(Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lnet/minecraft/nbt/CompoundTag;"
                    + "Ljava/util/function/Consumer;IZLjava/util/function/Supplier;)V";

    @AfterEach
    void resetRendererCompatibility() {
        SophisticatedStorageRendererCompatibility.publish(
                SophisticatedStorageRendererCompatibility.Capabilities.unsupported());
    }

    @Test
    void bridgesOnlyWhenCoreRemovedTheStorageTargetConstructor() {
        assertFalse(SophisticatedStorageMixinPlugin.requiresConstructorBridge(List.of(SIX_ARGUMENT)));
        assertTrue(SophisticatedStorageMixinPlugin.requiresConstructorBridge(List.of(SEVEN_ARGUMENT)));
        assertFalse(SophisticatedStorageMixinPlugin.requiresConstructorBridge(List.of(SIX_ARGUMENT, SEVEN_ARGUMENT)));
        assertFalse(SophisticatedStorageMixinPlugin.requiresConstructorBridge(List.of()));
    }

    @Test
    void bridgeAlsoRequiresTheExactLegacyStorageCallSite() {
        assertTrue(SophisticatedStorageMixinPlugin.requiresConstructorBridge(
                List.of(SEVEN_ARGUMENT), storageSettingsHandler(SIX_ARGUMENT)));
        assertFalse(SophisticatedStorageMixinPlugin.requiresConstructorBridge(
                List.of(SEVEN_ARGUMENT), storageSettingsHandler(SEVEN_ARGUMENT)));
        assertFalse(SophisticatedStorageMixinPlugin.requiresConstructorBridge(
                List.of(SEVEN_ARGUMENT), new ClassNode()));
        assertFalse(SophisticatedStorageMixinPlugin.requiresConstructorBridge(
                List.of(SIX_ARGUMENT), storageSettingsHandler(SIX_ARGUMENT)));
    }

    @Test
    void classifiesLegacyShulkerBody() {
        ClassNode renderer = shulkerRenderer(true, false, true);

        assertTrue(SophisticatedStorageRendererCompatibility.classifyShulker(renderer)
                == SophisticatedStorageRendererCompatibility.ShulkerLayout.LEGACY_SIX_ARGUMENT_BODY);
    }

    @Test
    void classifiesExtendedShulkerBody() {
        ClassNode renderer = shulkerRenderer(false, true, true);

        assertTrue(SophisticatedStorageRendererCompatibility.classifyShulker(renderer)
                == SophisticatedStorageRendererCompatibility.ShulkerLayout.EXTENDED_SEVEN_ARGUMENT_BODY);
    }

    @Test
    void rejectsMissingOrAmbiguousShulkerBodies() {
        assertTrue(SophisticatedStorageRendererCompatibility.classifyShulker(shulkerRenderer(false, false, true))
                == SophisticatedStorageRendererCompatibility.ShulkerLayout.UNSUPPORTED);
        assertTrue(SophisticatedStorageRendererCompatibility.classifyShulker(shulkerRenderer(true, true, true))
                == SophisticatedStorageRendererCompatibility.ShulkerLayout.UNSUPPORTED);
        assertTrue(SophisticatedStorageRendererCompatibility.classifyShulker(shulkerRenderer(true, false, false))
                == SophisticatedStorageRendererCompatibility.ShulkerLayout.UNSUPPORTED);
    }

    @Test
    void recognizesOnlyCompleteChestRendererContract() {
        assertTrue(SophisticatedStorageRendererCompatibility.supportsChest(chestRenderer(true), chestSubRenderer(true)));
        assertFalse(SophisticatedStorageRendererCompatibility.supportsChest(chestRenderer(false), chestSubRenderer(true)));
        assertFalse(SophisticatedStorageRendererCompatibility.supportsChest(chestRenderer(true), chestSubRenderer(false)));
    }

    @Test
    void selectsExactlyOneShulkerShellMixinAndFailsOpenPerType() {
        var legacy = new SophisticatedStorageRendererCompatibility.Capabilities(
                true, SophisticatedStorageRendererCompatibility.ShulkerLayout.LEGACY_SIX_ARGUMENT_BODY);
        assertTrue(SophisticatedStorageMixinPlugin.shouldApplyMixin(
                true, legacy, false, "x.ShulkerBoxRendererLegacyShellMixin"));
        assertFalse(SophisticatedStorageMixinPlugin.shouldApplyMixin(
                true, legacy, false, "x.ShulkerBoxRendererExtendedShellMixin"));

        var extended = new SophisticatedStorageRendererCompatibility.Capabilities(
                true, SophisticatedStorageRendererCompatibility.ShulkerLayout.EXTENDED_SEVEN_ARGUMENT_BODY);
        assertFalse(SophisticatedStorageMixinPlugin.shouldApplyMixin(
                true, extended, false, "x.ShulkerBoxRendererLegacyShellMixin"));
        assertTrue(SophisticatedStorageMixinPlugin.shouldApplyMixin(
                true, extended, false, "x.ShulkerBoxRendererExtendedShellMixin"));

        var unsupported = SophisticatedStorageRendererCompatibility.Capabilities.unsupported();
        assertFalse(SophisticatedStorageMixinPlugin.shouldApplyMixin(
                true, unsupported, false, "x.ChestRendererMixin"));
        assertFalse(SophisticatedStorageMixinPlugin.shouldApplyMixin(
                true, unsupported, false, "x.ShulkerBoxRendererMixin"));
        assertFalse(SophisticatedStorageMixinPlugin.shouldApplyMixin(
                true, unsupported, false, "x.StorageBlockEntityMixin"));
    }

    private static ClassNode shulkerRenderer(boolean shellInSix, boolean shellInSeven, boolean tintedHelper) {
        ClassNode node = new ClassNode();
        node.methods.add(method(SophisticatedStorageRendererCompatibility.SHULKER_RENDER_SIX,
                shellInSix ? 2 : 0));
        if (shellInSeven) {
            node.methods.add(method(SophisticatedStorageRendererCompatibility.SHULKER_RENDER_SEVEN, 2));
        }
        if (tintedHelper) {
            node.methods.add(new MethodNode(Opcodes.ACC_PRIVATE, "renderTintedModel",
                    SophisticatedStorageRendererCompatibility.SHULKER_TINTED_MODEL, null, null));
        }
        return node;
    }

    private static ClassNode chestRenderer(boolean complete) {
        ClassNode node = new ClassNode();
        if (complete) {
            node.methods.add(new MethodNode(Opcodes.ACC_PUBLIC, "render",
                    SophisticatedStorageRendererCompatibility.CHEST_RENDER_SIX, null, null));
        }
        return node;
    }

    private static ClassNode chestSubRenderer(boolean complete) {
        ClassNode node = new ClassNode();
        node.methods.add(new MethodNode(Opcodes.ACC_PRIVATE, "renderBottomAndLid",
                SophisticatedStorageRendererCompatibility.CHEST_BOTTOM_AND_LID, null, null));
        node.methods.add(new MethodNode(Opcodes.ACC_PRIVATE, "renderBottomAndLidWithTint",
                SophisticatedStorageRendererCompatibility.CHEST_BOTTOM_AND_LID_TINTED, null, null));
        node.methods.add(new MethodNode(Opcodes.ACC_PRIVATE, "renderChestLock",
                SophisticatedStorageRendererCompatibility.CHEST_LOCK_OR_TIER, null, null));
        if (complete) {
            node.methods.add(new MethodNode(Opcodes.ACC_PRIVATE, "renderTier",
                    SophisticatedStorageRendererCompatibility.CHEST_LOCK_OR_TIER, null, null));
        }
        return node;
    }

    private static MethodNode method(String descriptor, int shellCalls) {
        MethodNode method = new MethodNode(Opcodes.ACC_PUBLIC, "render", descriptor, null, null);
        for (int i = 0; i < shellCalls; i++) {
            method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/client/model/ShulkerModel", "renderToBuffer",
                    SophisticatedStorageRendererCompatibility.SHULKER_MODEL_RENDER, false));
        }
        return method;
    }

    private static ClassNode storageSettingsHandler(String constructorDescriptor) {
        ClassNode node = new ClassNode();
        MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC,
                "lambda$addItemDisplayCategory$1", "()Ljava/lang/Object;", null, null);
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                "net/p3pp3rf1y/sophisticatedcore/settings/itemdisplay/ItemDisplaySettingsCategory",
                "<init>", constructorDescriptor, false));
        node.methods.add(method);
        return node;
    }
}
