package fr.madu59.obe.client.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/** Converts ModelPart CPU vertex submissions into unculled block-model quads. */
public final class ModelPartQuadBaker {
    private ModelPartQuadBaker() {}

    public static List<BakedQuad> bake(
            ModelPart part,
            PoseStack poseStack,
            TextureAtlasSprite sprite,
            int argb,
            boolean shade,
            boolean ambientOcclusion
    ) {
        List<BakedQuad> output = new ArrayList<>();
        append(output, part, poseStack, sprite, argb, shade, ambientOcclusion);
        return output;
    }

    public static void append(
            List<BakedQuad> output,
            ModelPart part,
            PoseStack poseStack,
            TextureAtlasSprite sprite,
            int argb,
            boolean shade,
            boolean ambientOcclusion
    ) {
        QuadCollector collector = new QuadCollector(output, sprite, shade, ambientOcclusion);
        part.render(poseStack, sprite.wrap(collector), 0, 0, argb);
        collector.verifyComplete();
    }

    private static final class QuadCollector implements VertexConsumer {
        private final List<BakedQuad> output;
        private final TextureAtlasSprite sprite;
        private final boolean shade;
        private final boolean ambientOcclusion;
        private final List<Vertex> vertices = new ArrayList<>(4);

        private float x;
        private float y;
        private float z;
        private int color = -1;
        private float u;
        private float v;
        private int light;

        private QuadCollector(
                List<BakedQuad> output,
                TextureAtlasSprite sprite,
                boolean shade,
                boolean ambientOcclusion
        ) {
            this.output = output;
            this.sprite = sprite;
            this.shade = shade;
            this.ambientOcclusion = ambientOcclusion;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            color = -1;
            u = 0;
            v = 0;
            light = 0;
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            color = (red & 0xFF)
                    | (green & 0xFF) << 8
                    | (blue & 0xFF) << 16
                    | (alpha & 0xFF) << 24;
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.u = u;
            this.v = v;
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            light = (u & 0xFFFF) | (v & 0xFFFF) << 16;
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            vertices.add(new Vertex(this.x, this.y, this.z, color, u, v, light, x, y, z));
            if (vertices.size() == 4) {
                emitQuad();
            }
            return this;
        }

        private void emitQuad() {
            int[] packed = new int[32];
            Vector3f averageNormal = new Vector3f();
            for (int index = 0; index < 4; index++) {
                Vertex vertex = vertices.get(index);
                int offset = index * 8;
                packed[offset] = Float.floatToRawIntBits(vertex.x);
                packed[offset + 1] = Float.floatToRawIntBits(vertex.y);
                packed[offset + 2] = Float.floatToRawIntBits(vertex.z);
                packed[offset + 3] = vertex.color;
                packed[offset + 4] = Float.floatToRawIntBits(vertex.u);
                packed[offset + 5] = Float.floatToRawIntBits(vertex.v);
                packed[offset + 6] = vertex.light;
                packed[offset + 7] = packNormal(vertex.normalX, vertex.normalY, vertex.normalZ);
                averageNormal.add(vertex.normalX, vertex.normalY, vertex.normalZ);
            }
            Direction direction = Direction.getNearest(averageNormal.x(), averageNormal.y(), averageNormal.z());
            output.add(new BakedQuad(packed, -1, direction, sprite, shade, ambientOcclusion));
            vertices.clear();
        }

        private void verifyComplete() {
            if (!vertices.isEmpty()) {
                throw new IllegalStateException("ModelPart emitted a non-quad vertex count: " + vertices.size());
            }
        }

        private static int packNormal(float x, float y, float z) {
            int packedX = Math.round(Mth.clamp(x, -1.0F, 1.0F) * 127.0F) & 0xFF;
            int packedY = Math.round(Mth.clamp(y, -1.0F, 1.0F) * 127.0F) & 0xFF;
            int packedZ = Math.round(Mth.clamp(z, -1.0F, 1.0F) * 127.0F) & 0xFF;
            return packedX | packedY << 8 | packedZ << 16;
        }
    }

    private record Vertex(
            float x,
            float y,
            float z,
            int color,
            float u,
            float v,
            int light,
            float normalX,
            float normalY,
            float normalZ
    ) {}
}
