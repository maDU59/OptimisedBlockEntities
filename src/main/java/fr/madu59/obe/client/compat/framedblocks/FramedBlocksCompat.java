package fr.madu59.obe.client.compat.framedblocks;

import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.ChestState;

public class FramedBlocksCompat {
    public static void init() {
        RegistryApi.registerBlockEntityType("chest", FBContent.blockEntityTypeFramedChest.get());
        RegistryApi.registerModelBuilder(FBContent.blockEntityTypeFramedChest.get(), (state, originalModel) -> originalModel);
    }

    public static void update(BlockEntity be){
        if(!(be instanceof FramedChestBlockEntity)) return;
        BlockState state = be.getBlockState();
        if(state == null) return;
        if(state.getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSED){
            RenderModeManager.setRenderModeDelayed(be, RenderModeManager.RenderMode.TERRAIN, be.getBlockPos());
        }
        else{
            RenderModeManager.setRenderModeDelayed(be, RenderModeManager.RenderMode.ENTITY, be.getBlockPos());
        }
    }
}