package fr.madu59.obe.client.mixin.resources;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.ResourceLocation;

@Mixin(AtlasManager.class)
public abstract class AtlasManagerMixin {

    @Unique 
    private static final ResourceLocation blockAtlas = ResourceLocation.tryParse("minecraft:textures/atlas/blocks.png");

    @WrapWithCondition(method = { "lambda$updateSpriteMaps$0", "method_76672" }, at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private static boolean obe$muteDuplicates(Logger logger, String message, Object[] args) {
        ResourceLocation texture = (ResourceLocation) args[0];
        ResourceLocation newAtlas = (ResourceLocation) args[1];
        ResourceLocation oldAtlas = (ResourceLocation) args[2];
        return !(texture.getPath().startsWith("entity/") && (newAtlas.equals(blockAtlas) || oldAtlas.equals(blockAtlas)));
    }
}
