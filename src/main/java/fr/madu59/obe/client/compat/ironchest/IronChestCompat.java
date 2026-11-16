package fr.madu59.obe.client.compat.ironchest;

import com.progwml6.ironchest.IronChestsClientEvents;
import com.progwml6.ironchest.client.model.IronChestsModels;
import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.entity.IronChestsBlockEntityTypes;
import com.progwml6.ironchest.common.block.regular.AbstractIronChestBlock;
import com.progwml6.ironchest.common.block.trapped.AbstractTrappedIronChestBlock;
import fr.madu59.obe.client.api.registry.RegistryApi;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class IronChestCompat {
    public static void init() {
        for(RegistryObject<BlockEntityType<?>> entry : IronChestsBlockEntityTypes.BLOCK_ENTITIES.getEntries()){
            if(entry.get() == IronChestsBlockEntityTypes.CRYSTAL_CHEST.get() || entry.get() == IronChestsBlockEntityTypes.TRAPPED_CRYSTAL_CHEST.get()){
                continue;
            }
            RegistryApi.registerBlockEntityType("chest", entry.get());
            RegistryApi.registerModelLayerLocationProvider(entry.get(), IronChestCompat::getChestModelLayerLocation);
            RegistryApi.registerMaterialProvider(entry.get(), IronChestCompat::getChestMaterial);
        }
    }

    public static ModelLayerLocation getChestModelLayerLocation(BlockState state){
        return IronChestsClientEvents.IRON_CHEST;
    }

    public static ResourceLocation getChestMaterial(BlockState state){
        IronChestsTypes chestType = IronChestsTypes.IRON;
        IronChestsTypes actualType = AbstractIronChestBlock.getTypeFromBlock(state.getBlock());

        if (actualType != null) {
            chestType = actualType;
        }
        return IronChestsModels.chooseChestTexture(chestType, state.getBlock() instanceof AbstractTrappedIronChestBlock);
    }
}