package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;

public class MaterialResolver {
    public static Identifier getSkullBlockMaterial(SkullBlock.Type type){
        Identifier id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
        return entityTextureFormatter(id);
    }

    public static Identifier entityTextureFormatter(Identifier identifier){
        return Identifier.tryBuild(identifier.getNamespace(), identifier.getPath().replace(".png", "").replace("textures/", ""));
    }
}
