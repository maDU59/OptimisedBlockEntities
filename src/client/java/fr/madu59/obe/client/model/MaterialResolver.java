package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;

public class MaterialResolver {
    public static ResourceLocation getSkullBlockMaterial(SkullBlock.Type type){
        ResourceLocation id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
        return entityTextureFormatter(id);
    }

    public static ResourceLocation entityTextureFormatter(ResourceLocation identifier){
        return ResourceLocation.tryBuild(identifier.getNamespace(), identifier.getPath().replace(".png", "").replace("textures/", ""));
    }
}
