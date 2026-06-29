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
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;

public class BlockEntityStateModel implements BakedModel{
    private final List<SimpleBakedModel> models = new ArrayList<>();
    private final Map<String, BakedModel> partsMap = new HashMap<>();
    private final TextureAtlasSprite particleMaterial;
    private final boolean useAo;

    public BlockEntityStateModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, boolean useAo, BlockState state, TextureAtlasSprite particleMaterial){
        this(modelLayerLocation, texture, new PoseStack(), useAo, state, particleMaterial);
    }

    public BlockEntityStateModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, PoseStack poseStack, boolean useAo, BlockState state, TextureAtlasSprite particleMaterial){
        TextureAtlasSprite sprite = ResourceUtil.getSprite(texture);
        this.particleMaterial = particleMaterial;
        this.useAo = useAo;
        generateModel(modelLayerLocation, sprite, poseStack, useAo, state);
    }

    public BlockEntityStateModel(){
        this.particleMaterial = null;
        this.useAo = false;
    }

    public BlockEntityStateModel(TextureAtlasSprite particleMaterial){
        this.particleMaterial = particleMaterial;
        this.useAo = false;
    }

    public BakedModel getPart(String name){
        return partsMap.get(name);
    }

    private void generateModel(ModelLayerLocation modelLayerLocation, TextureAtlasSprite sprite, PoseStack poseStack, boolean useAo, BlockState state){
        ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(modelLayerLocation);
        ModCompat.applyEMFRestPose(root, state);
        Map<String, ModelPart> children = root.children;
        children.forEach((key, part) -> {
            if(shouldAddModelPart(key, state)){
                List<BakedQuad> bakedQuadsList = getBakedQuads(part, poseStack, sprite, shouldFixBFC(key));

                Map<Direction, List<BakedQuad>> dirs = new HashMap<>();
                SimpleBakedModel bakedModel = new SimpleBakedModel(bakedQuadsList, dirs, true, useAo, false, particleMaterial, null, null);

                addModelPart(key, bakedModel);
            }
        });
    }

    private boolean shouldAddModelPart(String key, BlockState state){
        if(state == null) return true;
        else if(state.getBlock() instanceof WallHangingSignBlock){
            return key != "vChains";
        }
        else if(state.getBlock() instanceof CeilingHangingSignBlock){
            return key != "normalChains";
        }
        else if(state.getBlock() instanceof WallBannerBlock){
            return key != "pole" && key != "flag";
        }
        else if(state.getBlock() instanceof BannerBlock){
            return key != "flag";
        }
        else if(state.getBlock() instanceof WallSignBlock){
            return key != "stick";
        }
        return true;
    }

    private List<BakedQuad> getBakedQuads(ModelPart part, PoseStack poseStack, TextureAtlasSprite sprite, boolean fixBfc){
        List<BakedQuad> bakedQuadsList = new ArrayList<>();
        part.visit(poseStack, (pose, name, idx, cube) -> {
            for(ModelPart.Polygon polygon : cube.polygons){
                if (polygon.vertices.length != 4) {
                    continue;
                }

                Vector3f normal = new Vector3f();

                polygon.normal.mul(pose.normal(), normal);
                
                Direction dir = getDirection(normal);

                int[] packedVertices = new int[32];

                for (int i = 0; i < 4; i++) {
                    ModelPart.Vertex vertex = polygon.vertices[i];

                    Vector3f pos = new Vector3f(vertex.pos);

                    Vector3f vec = pose.pose().transformPosition(pos.mul(1.0F / 16.0F));
                    
                    float u = sprite.getU(vertex.u);
                    float v = sprite.getV(vertex.v);

                    int offset = i * 8;

                    packedVertices[offset + 0] = Float.floatToRawIntBits(vec.x());
                    packedVertices[offset + 1] = Float.floatToRawIntBits(vec.y());
                    packedVertices[offset + 2] = Float.floatToRawIntBits(vec.z());

                    packedVertices[offset + 3] = -1; 

                    packedVertices[offset + 4] = Float.floatToRawIntBits(u);
                    packedVertices[offset + 5] = Float.floatToRawIntBits(v);

                    packedVertices[offset + 6] = 0;
                    packedVertices[offset + 7] = 0;
                }

                BakedQuad baked = new BakedQuad(
                    packedVertices,
                    0,
                    dir,
                    sprite,
                    true
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
                    dir.getOpposite(),
                    sprite,
                    true
                    );
                    bakedQuadsList.add(baked);
                }
            }
        });
        return bakedQuadsList;
    }

    private Direction getDirection(Vector3fc vec){
        return Direction.getNearest(vec.x(), vec.y(), vec.z());
    }

    private boolean shouldFixBFC(String key){
        return key.equals("vChains") || key.equals("normalChains");
    }

    private void addModelPart(String key, SimpleBakedModel simpleBakedModel){
        models.add(simpleBakedModel);
        partsMap.put(key, simpleBakedModel);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleMaterial;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState arg0, Direction arg1, RandomSource arg2) {
        List<BakedQuad> output = new ArrayList<BakedQuad>();
        if(arg1 != null) return output;
        for(BakedModel model : models){
            output.addAll(model.getQuads(arg0, arg1, arg2));
        }
        return output;
    }

    @Override
    public ItemTransforms getTransforms() {
        return null;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return useAo;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public ItemOverrides getOverrides() {
        return null;
    }
}