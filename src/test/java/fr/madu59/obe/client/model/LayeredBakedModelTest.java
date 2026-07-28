package fr.madu59.obe.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

class LayeredBakedModelTest {
    @Test
    void preservesLayerAndQuadOrderAndOnlyReturnsUnculledFaces() {
        TextureAtlasSprite particle = mock(TextureAtlasSprite.class);
        BakedQuad first = mock(BakedQuad.class);
        BakedQuad second = mock(BakedQuad.class);
        LayeredBakedModel model = new LayeredBakedModel(
                List.of(
                        new LayeredBakedModel.Layer(ResourceLocation.parse("test:first"), List.of(first)),
                        new LayeredBakedModel.Layer(ResourceLocation.parse("test:second"), List.of(second))
                ),
                particle,
                false
        );

        var quads = model.getQuads(null, null, null);
        assertEquals(List.of(first, second), quads);
        assertTrue(model.getQuads(null, net.minecraft.core.Direction.NORTH, null).isEmpty());
        assertEquals(List.of("test:first", "test:second"),
                model.layers().stream().map(layer -> layer.material().toString()).toList());
        assertSame(particle, model.getParticleIcon());
    }
}
