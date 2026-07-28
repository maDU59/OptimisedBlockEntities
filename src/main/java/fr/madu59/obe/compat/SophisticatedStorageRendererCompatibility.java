package fr.madu59.obe.compat;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Loading-time renderer ABI capabilities for the optional Sophisticated
 * Storage integration. This class intentionally has no Sophisticated or
 * Minecraft references so the guarded bootstrap can query it safely.
 */
public final class SophisticatedStorageRendererCompatibility {
    static final String CHEST_RENDER_SIX =
            "(Lnet/p3pp3rf1y/sophisticatedstorage/block/ChestBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;II)V";
    static final String CHEST_BOTTOM_AND_LID =
            "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FII"
                    + "Lnet/p3pp3rf1y/sophisticatedstorage/client/StorageTextureManager$ChestMaterial;)V";
    static final String CHEST_BOTTOM_AND_LID_TINTED =
            "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FIII"
                    + "Lnet/p3pp3rf1y/sophisticatedstorage/client/StorageTextureManager$ChestMaterial;)V";
    static final String CHEST_LOCK_OR_TIER =
            "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FII)V";
    static final String SHULKER_RENDER_SIX =
            "(Lnet/p3pp3rf1y/sophisticatedstorage/block/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;II)V";
    static final String SHULKER_RENDER_SEVEN =
            "(Lnet/p3pp3rf1y/sophisticatedstorage/block/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V";
    static final String SHULKER_TINTED_MODEL =
            "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;III"
                    + "Lnet/minecraft/client/resources/model/Material;)V";
    static final String SHULKER_MODEL_RENDER =
            "(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V";

    private static final String SHULKER_MODEL = "net/minecraft/client/model/ShulkerModel";
    private static volatile Capabilities current = Capabilities.unsupported();

    private SophisticatedStorageRendererCompatibility() {}

    public enum ShulkerLayout {
        LEGACY_SIX_ARGUMENT_BODY,
        EXTENDED_SEVEN_ARGUMENT_BODY,
        UNSUPPORTED
    }

    public record Capabilities(boolean chestSupported, ShulkerLayout shulkerLayout) {
        public Capabilities {
            if (shulkerLayout == null) {
                shulkerLayout = ShulkerLayout.UNSUPPORTED;
            }
        }

        public static Capabilities unsupported() {
            return new Capabilities(false, ShulkerLayout.UNSUPPORTED);
        }

        public boolean shulkerSupported() {
            return shulkerLayout != ShulkerLayout.UNSUPPORTED;
        }

        public boolean anyStorageSupported() {
            return chestSupported || shulkerSupported();
        }
    }

    public static Capabilities current() {
        return current;
    }

    public static boolean chestSupported() {
        return current.chestSupported();
    }

    public static boolean shulkerSupported() {
        return current.shulkerSupported();
    }

    static void publish(Capabilities capabilities) {
        current = capabilities == null ? Capabilities.unsupported() : capabilities;
    }

    static boolean supportsChest(ClassNode renderer, ClassNode subRenderer) {
        return hasMethod(renderer, "render", CHEST_RENDER_SIX)
                && hasMethod(subRenderer, "renderBottomAndLid", CHEST_BOTTOM_AND_LID)
                && hasMethod(subRenderer, "renderBottomAndLidWithTint", CHEST_BOTTOM_AND_LID_TINTED)
                && hasMethod(subRenderer, "renderChestLock", CHEST_LOCK_OR_TIER)
                && hasMethod(subRenderer, "renderTier", CHEST_LOCK_OR_TIER);
    }

    static ShulkerLayout classifyShulker(ClassNode renderer) {
        if (!hasMethod(renderer, "render", SHULKER_RENDER_SIX)
                || !hasMethod(renderer, "renderTintedModel", SHULKER_TINTED_MODEL)) {
            return ShulkerLayout.UNSUPPORTED;
        }

        boolean legacyBody = shellCallCount(renderer, SHULKER_RENDER_SIX) == 2;
        boolean extendedBody = shellCallCount(renderer, SHULKER_RENDER_SEVEN) == 2;
        if (legacyBody == extendedBody) {
            return ShulkerLayout.UNSUPPORTED;
        }
        return legacyBody
                ? ShulkerLayout.LEGACY_SIX_ARGUMENT_BODY
                : ShulkerLayout.EXTENDED_SEVEN_ARGUMENT_BODY;
    }

    private static boolean hasMethod(ClassNode node, String name, String descriptor) {
        return node != null && node.methods.stream()
                .anyMatch(method -> method.name.equals(name) && method.desc.equals(descriptor));
    }

    private static int shellCallCount(ClassNode node, String renderDescriptor) {
        if (node == null) {
            return 0;
        }
        return node.methods.stream()
                .filter(method -> method.name.equals("render") && method.desc.equals(renderDescriptor))
                .mapToInt(SophisticatedStorageRendererCompatibility::shellCallCount)
                .sum();
    }

    private static int shellCallCount(MethodNode method) {
        int calls = 0;
        for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof MethodInsnNode invocation
                    && invocation.owner.equals(SHULKER_MODEL)
                    && invocation.name.equals("renderToBuffer")
                    && invocation.desc.equals(SHULKER_MODEL_RENDER)) {
                calls++;
            }
        }
        return calls;
    }
}
