package fr.madu59.obe.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityStateModel implements BlockStateModel{
    private final List<SingleVariant> models = new ArrayList<>();
    private final Map<String, BlockStateModel> partsMap = new HashMap<>();
    private final TextureAtlasSprite particleMaterial;

    public BlockEntityStateModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, boolean useAo, BlockState state, TextureAtlasSprite particleMaterial){
        this(modelLayerLocation, texture, new PoseStack(), useAo, state, particleMaterial);
    }

    public BlockEntityStateModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, PoseStack poseStack, boolean useAo, BlockState state, TextureAtlasSprite particleMaterial){
        TextureAtlasSprite sprite = ResourceUtil.getSprite(texture);
        this.particleMaterial = particleMaterial;
        generateModel(modelLayerLocation, sprite, poseStack, useAo, state);
    }

    public BlockEntityStateModel(){
        particleMaterial = null;
    }

    public BlockEntityStateModel(TextureAtlasSprite particleMaterial){
        this.particleMaterial = particleMaterial;
    }

    @Override
    public void collectParts(RandomSource randomSource, List<BlockModelPart> list) {
        if (models.isEmpty()) return;

        long seed = randomSource.nextLong();

        for (BlockStateModel model : this.models) {
            randomSource.setSeed(seed);
            model.collectParts(randomSource, list);
        }
    }

    public BlockStateModel getPart(String name){
        return partsMap.get(name);
    }

    private void generateModel(ModelLayerLocation modelLayerLocation, TextureAtlasSprite sprite, PoseStack poseStack, boolean useAo, BlockState state){
        ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(modelLayerLocation);
        ModCompat.applyEMFRestPose(root, state);
        Map<String, ModelPart> children = root.children;
        children.forEach((key, part) -> {

            List<BakedQuad> bakedQuadsList = getBakedQuads(part, poseStack, sprite, shouldFixBFC(key));
            QuadCollection collection = createQuadCollection(bakedQuadsList);
            SimpleModelWrapper wrapper = new SimpleModelWrapper(collection, useAo, particleMaterial);
            SingleVariant singleVariant = new SingleVariant(wrapper);

            addModelPart(key, singleVariant);
        });
    }

    private List<BakedQuad> getBakedQuads(ModelPart part, PoseStack poseStack, TextureAtlasSprite sprite, boolean fixBfc){
        List<BakedQuad> bakedQuadsList = new ArrayList<>();
        part.visit(poseStack, (pose, name, idx, cube) -> {
            for(ModelPart.Polygon polygon : cube.polygons){
                if (polygon.vertices().length != 4) {
                    continue;
                }

                Vector3f normal = new Vector3f();

                polygon.normal().mul(pose.normal(), normal);
                
                Direction dir = getDirection(normal);

                int[] packedVertices = new int[32];

                for (int i = 0; i < 4; i++) {
                    ModelPart.Vertex vertex = polygon.vertices()[i];
                    Vector3f vec = pose.pose().transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), new Vector3f());
                    
                    float u = sprite.getU(vertex.u());
                    float v = sprite.getV(vertex.v());

                    int offset = i * 8;

                    // 1. Pack Spatial Positions (X, Y, Z)
                    packedVertices[offset + 0] = Float.floatToRawIntBits(vec.x());
                    packedVertices[offset + 1] = Float.floatToRawIntBits(vec.y());
                    packedVertices[offset + 2] = Float.floatToRawIntBits(vec.z());

                    // 2. Pack Default Color (White / Full Alpha: 0xFFFFFFFF)
                    packedVertices[offset + 3] = -1; 

                    // 3. Pack Texture Coordinates (U, V)
                    packedVertices[offset + 4] = Float.floatToRawIntBits(u);
                    packedVertices[offset + 5] = Float.floatToRawIntBits(v);

                    // 4. Pack Light/Overlay and Normals (Fallback placeholders)
                    packedVertices[offset + 6] = 0;
                    packedVertices[offset + 7] = 0; 
                }

                BakedQuad baked = new BakedQuad(
                    packedVertices,
                    0,
                    dir,
                    sprite,
                    true,
                    0
                );
                bakedQuadsList.add(baked);
                if(fixBfc){
                    int[] invertedVertices = new int[32];
                    for (int i = 0; i < 4; i++) {
                        int srcOffset = i * 8;
                        int destOffset = ((4 - i) % 4) * 8;
                        System.arraycopy(packedVertices, srcOffset, invertedVertices, destOffset, 8);
                    }
                    baked = new BakedQuad(
                        invertedVertices,
                    0,
                    dir,
                    sprite,
                    true,
                    0
                    );
                    bakedQuadsList.add(baked);
                }
            }
        });
        return bakedQuadsList;
    }

    private Direction getDirection(Vector3fc vec){
        return Direction.getApproximateNearest(vec.x(), vec.y(), vec.z());
    }

    private boolean shouldFixBFC(String key){
        return key.equals("vChains") || key.equals("normalChains");
    }

    private void addModelPart(String key, SingleVariant singleVariant){
        models.add(singleVariant);
        partsMap.put(key, singleVariant);
    }

    private QuadCollection createQuadCollection(List<BakedQuad> quads) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (BakedQuad quad : quads) {
            builder.addUnculledFace(quad);
        }
        return builder.build();
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return particleMaterial;
    }
}