package fr.madu59.obe.client.mixin.blockentity.sign;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignTextExt;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignText;

// SignText is immutable (edits return a new instance), so hasMessage can be computed once per instance
// instead of streaming and stringifying every line on every frame
@Mixin(SignText.class)
public abstract class SignTextMixin implements SignTextExt {
    @Unique private static final byte UNKNOWN = 0, EMPTY = 1, PRESENT = 2;
    @Unique private byte hasMessage = UNKNOWN;
    @Unique private byte hasFilteredMessage = UNKNOWN;

    @Override
    public boolean obe$hasMessage(LocalPlayer player) {
        boolean filtered = player.isTextFilteringEnabled();
        byte cached = filtered ? hasFilteredMessage : hasMessage;
        if (cached == UNKNOWN) {
            cached = ((SignText) (Object) this).hasMessage(player) ? PRESENT : EMPTY;
            if (filtered) hasFilteredMessage = cached;
            else hasMessage = cached;
        }
        return cached == PRESENT;
    }
}
