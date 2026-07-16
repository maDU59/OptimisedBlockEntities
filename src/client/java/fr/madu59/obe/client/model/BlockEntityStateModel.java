package fr.madu59.obe.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityStateModel implements BlockStateModel{
    private SimpleModelWrapper modelPart;
    private final Material.Baked particleMaterial;

    public BlockEntityStateModel(ModelLayerLocation modelLayerLocation, Identifier texture, boolean useAo, BlockState state, Material.Baked particleMaterial){
        this(modelLayerLocation, texture, new PoseStack(), useAo, state, particleMaterial);
    }

    public BlockEntityStateModel(ModelLayerLocation modelLayerLocation, Identifier texture, PoseStack poseStack, boolean useAo, BlockState state, Material.Baked particleMaterial){
        TextureAtlasSprite sprite = ResourceUtil.getSprite(texture);
        this.particleMaterial = particleMaterial;
        generateModel(modelLayerLocation, sprite, poseStack, useAo, state);
    }

    public BlockEntityStateModel(){
        particleMaterial = null;
    }

    public BlockEntityStateModel(TextureAtlasSprite particleMaterial){
        this.particleMaterial = new Material.Baked(particleMaterial, false);
    }

    public BlockEntityStateModel(Material.Baked particleMaterial){
        this.particleMaterial = particleMaterial;
    }

    @Override
    public void collectParts(RandomSource randomSource, List<BlockStateModelPart> output) {
        if (modelPart == null) return;

        long seed = randomSource.nextLong();

        randomSource.setSeed(seed);
        output.add(modelPart);
    }

    private void generateModel(ModelLayerLocation modelLayerLocation, TextureAtlasSprite sprite, PoseStack poseStack, boolean useAo, BlockState state){
        ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(modelLayerLocation);
        ModCompat.applyEMFRestPose(root, state);

        Map<String, ModelPart> children = root.children;
        List<BakedQuad> bakedQuadsList = new ArrayList<>();
        children.forEach((key, part) -> {
            getBakedQuads(bakedQuadsList, part, poseStack, sprite, key, state);
        });

        QuadCollection collection = createQuadCollection(bakedQuadsList);
        modelPart = new SimpleModelWrapper(collection, useAo, particleMaterial);
    }

    private void getBakedQuads(List<BakedQuad> output, ModelPart part, PoseStack poseStack, TextureAtlasSprite sprite, String partName, BlockState state){

        boolean fixBfc = shouldFixBFC(partName);

        Material.Baked bakedMat = new Material.Baked(sprite, false);
        MaterialInfo matInfo = MaterialInfo.of(bakedMat, Transparency.TRANSPARENT, -1, true, 0);

        Vector3f[] positions = new Vector3f[4];
        long[] uvs = new long[4];
        Vector3f normal = new Vector3f();

        part.visit(poseStack, (pose, partPath, cubeIndex, cube) -> {
            for(ModelPart.Polygon polygon : cube.polygons){
                if (polygon.vertices().length != 4) continue;

                polygon.normal().mul(pose.normal(), normal);
                
                Direction dir = getDirection(normal);

                for (int i = 0; i < 4; i++) {
                    ModelPart.Vertex vertex = polygon.vertices()[i];
                    // From ModelPart#getExtentsForGui
                    positions[i] = pose.pose().transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), new Vector3f());

                    float u = sprite.getU(vertex.u());
                    float v = sprite.getV(vertex.v());
                    uvs[i] = UVPair.pack(u, v);
                }

                if (shouldSkipQuad(polygon, positions, state, partName)) continue;

                BakedQuad baked = new BakedQuad(
                        positions[0], positions[1], positions[2], positions[3],
                        uvs[0], uvs[1], uvs[2], uvs[3],
                        dir,
                        matInfo
                );
                output.add(baked);
                if(fixBfc){
                    // Same geometry but with inverted winding order so they are visible from the other side of the model
                    baked = new BakedQuad(positions[0], positions[3], positions[2], positions[1], uvs[0], uvs[3], uvs[2], uvs[1], dir.getOpposite(), matInfo );
                    output.add(baked);
                }
            }
        });
    }

    private Direction getDirection(Vector3fc vec){
        return Direction.getApproximateNearest(vec.x(), vec.y(), vec.z());
    }

    private boolean shouldFixBFC(String key){
        return key.equals("vChains") || key.equals("normalChains");
    }

    private boolean shouldSkipQuad(Polygon polygon, Vector3f[] positions, BlockState state, String partName){
        if(!SettingsManager.MODEL_OPTIMIZATION.getValue()) return false;
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


    private QuadCollection createQuadCollection(List<BakedQuad> quads) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (BakedQuad quad : quads) {
            builder.addUnculledFace(quad);
        }
        return builder.build();
    }

    @Override
    public Material.Baked particleMaterial() {
        return particleMaterial;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return 0;
    }
}