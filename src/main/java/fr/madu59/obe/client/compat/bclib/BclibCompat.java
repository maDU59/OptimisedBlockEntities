package fr.madu59.obe.client.compat.bclib;

// import org.betterx.bclib.registry.BaseBlockEntities;

// import fr.madu59.obe.client.registry.MaterialGetter;
// import fr.madu59.obe.client.registry.Registry;
// import fr.madu59.obe.client.util.BackportUtil;
// import net.minecraft.core.registries.BuiltInRegistries;
// import net.minecraft.resources.ResourceLocation;
// import net.minecraft.world.level.block.ChestBlock;
// import net.minecraft.world.level.block.state.properties.ChestType;

public class BclibCompat {
    
    public static void init() {

        // Registry.addBlockEntityTypeInGroup("chest", BaseBlockEntities.CHEST);
        
        // MaterialGetter.register(BaseBlockEntities.CHEST, (state) -> {
        //     ChestType type = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);

        //     ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());

        //     String texturePath = blockId.getPath();

        //     if(type == ChestType.LEFT) texturePath += "_left";
        //     else if(type == ChestType.RIGHT) texturePath += "_right";
        //     return ResourceLocation.tryBuild(blockId.getNamespace(), "entity/chest/" + texturePath);
        // });
    }
}
