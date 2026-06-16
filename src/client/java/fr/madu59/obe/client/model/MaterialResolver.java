package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;

public class MaterialResolver {
    public static SpriteId getSkullBlockMaterial(SkullBlock.Type type){
        Identifier id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
        return entityTextureFormatter(id);
    }

    public static SpriteId entityTextureFormatter(Identifier identifier){
        return new SpriteId(null, Identifier.tryBuild(identifier.getNamespace(), identifier.getPath().replace(".png", "").replace("textures/", "")));
    }
}
