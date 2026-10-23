package fr.madu59.obe.client.mixin.resources;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;

@Mixin(AtlasManager.class)
public abstract class AtlasmanagerMixin {

    @Unique 
    private static final Identifier blockAtlas = Identifier.tryParse("minecraft:textures/atlas/blocks.png");

    @WrapWithCondition(method = "lambda$updateSpriteMaps$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private static boolean obe$muteDuplicates(Logger logger, String message, Object[] args) {
        Identifier texture = (Identifier) args[0];
        Identifier newAtlas = (Identifier) args[1];
        Identifier oldAtlas = (Identifier) args[2];
        return !(texture.getPath().startsWith("entity/") && (newAtlas.equals(blockAtlas) || oldAtlas.equals(blockAtlas)));
    }
}
