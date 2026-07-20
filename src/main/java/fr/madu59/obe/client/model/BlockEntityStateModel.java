package fr.madu59.obe.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart.Polygon;
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
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;

public class BlockEntityStateModel implements BakedModel{
    private SimpleBakedModel modelPart;
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

    private void generateModel(ModelLayerLocation modelLayerLocation, TextureAtlasSprite sprite, PoseStack poseStack, boolean useAo, BlockState state){
        ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(modelLayerLocation);
        ModCompat.applyEMFRestPose(root, state);

        Map<String, ModelPart> children = root.children;
        List<BakedQuad> bakedQuadsList = new ArrayList<>();
        children.forEach((key, part) -> {
            if(shouldAddModelPart(key, state)){
                getBakedQuads(bakedQuadsList, part, poseStack, sprite, key, state);
            }
        });

        Map<Direction, List<BakedQuad>> dirs = new HashMap<>();
        modelPart = new SimpleBakedModel(bakedQuadsList, dirs, useAo, true, false, particleMaterial, null, null);
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

    private void getBakedQuads(List<BakedQuad> output, ModelPart part, PoseStack poseStack, TextureAtlasSprite sprite, String partName, BlockState state){

        boolean fixBfc = shouldFixBFC(partName);

        Vector3f[] positions = new Vector3f[4];
        Vector3f normal = new Vector3f();

        part.visit(poseStack, (pose, partPath, cubeIndex, cube) -> {
            for(ModelPart.Polygon polygon : cube.polygons){
                if (polygon.vertices.length != 4) continue;

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


                    positions[i] = vec;
                }

                if (shouldSkipQuad(polygon, positions, state, partName)) continue;

                BakedQuad baked = new BakedQuad(
                    packedVertices,
                    0,
                    dir,
                    sprite,
                    true
                );
                output.add(baked);
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
                    output.add(baked);
                }
            }
        });
    }

    private Direction getDirection(Vector3fc vec){
        return Direction.getNearest(vec.x(), vec.y(), vec.z());
    }

    private boolean shouldFixBFC(String key){
        return key.equals("vChains") || key.equals("normalChains");
    }

    private boolean shouldSkipQuad(Polygon polygon, Vector3f[] positions, BlockState state, String partName){
        if(!SettingsManager.MODEL_OPTIMIZATION.getValue() || state == null) return false;
        if(state.getBlock() instanceof ChestBlock || state.getBlock() instanceof EnderChestBlock){
            if(partName.equals("lid") || partName.equals("bottom")) {
                boolean isHorizontal = positions[0].y() == positions[1].y() && positions[1].y() == positions[2].y() && positions[2].y() == positions[3].y();

                if (isHorizontal) {
                    float yPos = positions[0].y();
                    return yPos <= (10.1f / 16f) && yPos >= (8.9f / 16f);
                }
            }
        }
        else if(state.getBlock() instanceof ShulkerBoxBlock){
            if (partName.equals("lid") || partName.equals("base")) {
                float threshold = 1f/32f;
                boolean touchesCorner = false;

                for (Vector3f pos : positions) {
                    boolean touchX = pos.x() <= threshold || pos.x() >= (1.0f - threshold);
                    boolean touchY = pos.y() <= threshold || pos.y() >= (1.0f - threshold);
                    boolean touchZ = pos.z() <= threshold || pos.z() >= (1.0f - threshold);

                    if (touchX && touchY && touchZ) {
                        touchesCorner = true;
                        break;
                    }
                }

                return !touchesCorner;
            }
        }
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleMaterial;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState arg0, Direction arg1, RandomSource arg2) {
        List<BakedQuad> output = new ArrayList<BakedQuad>();
        if(arg1 != null || modelPart == null) return output;
        output.addAll(modelPart.getQuads(arg0, arg1, arg2));
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

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutoutMipped());
    }
}