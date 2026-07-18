package fr.madu59.obe.client.mixin.renderer.compat.indigo;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Pseudo;
// import org.spongepowered.asm.mixin.Unique;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.ModifyVariable;

// import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
// import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
// import net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl;
// import net.minecraft.client.renderer.block.BlockAndTintGetter;
// import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
// import net.minecraft.core.BlockPos;
// import net.minecraft.world.level.block.state.BlockState;

// @Pseudo
// @Mixin(value = AltModelBlockRendererImpl.class, remap = false)
// public class AltModelBlockRendererImplMixin {
//     @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

//     @ModifyVariable(method = "tesselateBlock", at = @At("HEAD"), argsOnly = true)
//     private BlockStateModel obe$modifyModel(BlockStateModel model, QuadEmitter output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel originalModel, long seed) {
//         model = blockEntityModelsManager.getModel(blockState, pos, seed, model, level);
//         return model == null? originalModel : model;
//     }
// }
