package fr.madu59.obe.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;

class ModelPartQuadBakerTest {
    @Test
    void capturesTransformedPositionsUvsColorNormalsAndWinding() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild(
                "cube",
                CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 16, 16, 16),
                PartPose.ZERO
        );
        var part = LayerDefinition.create(mesh, 64, 64).bakeRoot().getChild("cube");
        TextureAtlasSprite sprite = mock(TextureAtlasSprite.class);
        when(sprite.getU(anyFloat())).thenAnswer(invocation -> 100.0F + invocation.<Float>getArgument(0) * 10.0F);
        when(sprite.getV(anyFloat())).thenAnswer(invocation -> 200.0F + invocation.<Float>getArgument(0) * 20.0F);
        when(sprite.wrap(any(VertexConsumer.class))).thenAnswer(
                invocation -> new SpriteCoordinateExpander(invocation.getArgument(0), sprite)
        );
        PoseStack poseStack = new PoseStack();
        poseStack.translate(2.0F, 3.0F, 4.0F);

        ArrayList<BakedQuad> quads = new ArrayList<>();
        ModelPartQuadBaker.append(quads, part, poseStack, sprite, 0x7F123456, false, false);

        assertEquals(6, quads.size());
        BakedQuad down = quads.getFirst();
        assertEquals(Direction.DOWN, down.getDirection());
        assertEquals(-1, down.getTintIndex());
        assertFalse(down.isShade());
        assertFalse(down.hasAmbientOcclusion());

        int[] vertices = down.getVertices();
        assertVertex(vertices, 0, 3.0F, 3.0F, 5.0F, 0x7F563412, 105.0F, 200.0F, 0x00008100);
        assertVertex(vertices, 1, 2.0F, 3.0F, 5.0F, 0x7F563412, 102.5F, 200.0F, 0x00008100);
        assertVertex(vertices, 2, 2.0F, 3.0F, 4.0F, 0x7F563412, 102.5F, 205.0F, 0x00008100);
        assertVertex(vertices, 3, 3.0F, 3.0F, 4.0F, 0x7F563412, 105.0F, 205.0F, 0x00008100);
    }

    private static void assertVertex(
            int[] vertices,
            int index,
            float x,
            float y,
            float z,
            int color,
            float u,
            float v,
            int normal
    ) {
        int offset = index * 8;
        assertEquals(x, Float.intBitsToFloat(vertices[offset]));
        assertEquals(y, Float.intBitsToFloat(vertices[offset + 1]));
        assertEquals(z, Float.intBitsToFloat(vertices[offset + 2]));
        assertEquals(color, vertices[offset + 3]);
        assertEquals(u, Float.intBitsToFloat(vertices[offset + 4]));
        assertEquals(v, Float.intBitsToFloat(vertices[offset + 5]));
        assertEquals(0, vertices[offset + 6]);
        assertEquals(normal, vertices[offset + 7]);
    }
}
