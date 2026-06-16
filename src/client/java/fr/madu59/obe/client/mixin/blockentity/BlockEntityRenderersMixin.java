package fr.madu59.obe.client.mixin.blockentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import fr.madu59.obe.client.OBEClient;
import fr.madu59.obe.client.renderer.blockentity.banner.OBEBannerRenderer;
import fr.madu59.obe.client.renderer.blockentity.bed.OBEBedRenderer;
import fr.madu59.obe.client.renderer.blockentity.bell.OBEBellRenderer;
import fr.madu59.obe.client.renderer.blockentity.chest.OBEChestRenderer;
import fr.madu59.obe.client.renderer.blockentity.coppergolemstatues.OBECopperGolemStatueBlockRenderer;
import fr.madu59.obe.client.renderer.blockentity.decoratedpot.OBEDecoratedPotRenderer;
import fr.madu59.obe.client.renderer.blockentity.shulkerbox.OBEShulkerBoxRenderer;
import fr.madu59.obe.client.renderer.blockentity.sign.OBEHangingSignRenderer;
import fr.madu59.obe.client.renderer.blockentity.sign.OBEStandingSignRenderer;
import fr.madu59.obe.client.renderer.blockentity.skull.OBESkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Mixin(BlockEntityRenderers.class)
public abstract class BlockEntityRenderersMixin {
    
    @SuppressWarnings("rawtypes")
    @ModifyVariable(method = "register", at = @At("HEAD"), argsOnly = true)
    private static BlockEntityRendererProvider<?, ?> obe$overrideRegister(
        BlockEntityRendererProvider<?, ?> renderer, 
        BlockEntityType<?> type
    ) {
        if (type == BlockEntityType.SIGN) {
            OBEClient.debug("Replaced sign block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEStandingSignRenderer(ctx);
        }
        else if (type == BlockEntityType.HANGING_SIGN) {
            OBEClient.debug("Replaced hanging sign block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEHangingSignRenderer(ctx);
        }
        else if (type == BlockEntityType.SKULL) {
            OBEClient.debug("Replaced skull block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBESkullBlockRenderer(ctx);
        }
        else if (type == BlockEntityType.BED) {
            OBEClient.debug("Replaced bed block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEBedRenderer(ctx);
        }
        else if (type == BlockEntityType.CHEST || type == BlockEntityType.ENDER_CHEST || type == BlockEntityType.TRAPPED_CHEST) {
            OBEClient.debug("Replaced chest block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEChestRenderer(ctx);
        }
        else if (type == BlockEntityType.BELL) {
            OBEClient.debug("Replaced bell block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEBellRenderer(ctx);
        }
        else if (type == BlockEntityType.BANNER) {
            OBEClient.debug("Replaced banner block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEBannerRenderer(ctx);
        }
        else if (type == BlockEntityType.COPPER_GOLEM_STATUE) {
            OBEClient.debug("Replaced copper golem statue block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBECopperGolemStatueBlockRenderer(ctx);
        }
        else if (type == BlockEntityType.SHULKER_BOX) {
            OBEClient.debug("Replaced sulker boxes block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEShulkerBoxRenderer(ctx);
        }
        else if (type == BlockEntityType.DECORATED_POT) {
            OBEClient.debug("Replaced decorated pots block entity renderer");
            renderer = (BlockEntityRendererProvider) (ctx) -> new OBEDecoratedPotRenderer(ctx);
        }
        
        return renderer;
    }
}
