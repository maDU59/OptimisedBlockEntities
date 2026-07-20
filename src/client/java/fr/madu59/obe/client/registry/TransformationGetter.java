package fr.madu59.obe.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.util.blockentity.BannerUtil;
import fr.madu59.obe.client.util.blockentity.BedUtil;
import fr.madu59.obe.client.util.blockentity.BellUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import fr.madu59.obe.client.util.blockentity.HangingSignUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import fr.madu59.obe.client.util.blockentity.SignUtil;
import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TransformationGetter {
    private static Map<BlockEntityType<?>, BiConsumer<BlockState, PoseStack>> transformationsGetterProvider = new ConcurrentHashMap<>();
    private static Map<String, BiConsumer<BlockState, PoseStack>> defaultTransformationsGetterProvider = new ConcurrentHashMap<>();

    public static void init(){
        registerDefault("chest", ChestUtil::transform);
        registerDefault("skull", SkullBlockUtil::transform);
        registerDefault("bell", BellUtil::transform);
        registerDefault("banner", BannerUtil::transform);
        registerDefault("shulker_box", ShulkerBoxUtil::transform);
        registerDefault("decorated_pot", DecoratedPotUtil::transform);
        registerDefault("sign", SignUtil::transform);
        registerDefault("hanging_sign", HangingSignUtil::transform);
        registerDefault("bed", BedUtil::transform);
    }

    public static void registerDefault(String group, BiConsumer<BlockState, PoseStack> getter){
        if(!Registry.hasGroup(group)){
            OBE.LOGGER.error("An external mod tried registering a default transformation getter in a non existing group: " + group);
        }
        else{
            defaultTransformationsGetterProvider.put(group, getter);
        }
    }

    public static void register(BlockEntityType<?> beType, BiConsumer<BlockState, PoseStack> getter){
        transformationsGetterProvider.put(beType, getter);
    }

    public static void applyTransformation(BlockState state, PoseStack poseStack){
        applyTransformation(state, poseStack, null);
    }

    public static void applyTransformation(BlockState state, PoseStack poseStack, String group){
        if(!state.hasBlockEntity()) return;
        BlockEntityType<?> beType = Registry.getBlockEntityType(state);
        if (beType == null) return;
        BiConsumer<BlockState, PoseStack> provider = transformationsGetterProvider.get(beType);
        if (provider != null) {
            provider.accept(state, poseStack);
            return;
        }
        if (group == null) group = Registry.getGroup(beType);
        if (group != null) provider = defaultTransformationsGetterProvider.get(group);
        if (provider != null) {
            provider.accept(state, poseStack);
            return;
        }
    }
}
