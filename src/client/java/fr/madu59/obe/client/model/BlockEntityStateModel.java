package fr.madu59.obe.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityStateModel implements BlockStateModel{
    private final List<SingleVariant> models = new ArrayList<>();
    private final Map<String, BlockStateModel> partsMap = new HashMap<>();
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

    @Override
    public void collectParts(RandomSource randomSource, List<BlockStateModelPart> list) {
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

                Vector3f[] positions = new Vector3f[4];
                long[] uvs = new long[4];

                for (int i = 0; i < 4; i++) {
                    ModelPart.Vertex vertex = polygon.vertices()[i];
                    Vector3f vec = pose.pose().transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), new Vector3f());
                    positions[i] = vec;

                    float u = sprite.getU(vertex.u());
                    float v = sprite.getV(vertex.v());
                    uvs[i] = UVPair.pack(u, v);
                }

                Material.Baked bakedMat = new Material.Baked(sprite, false);
                MaterialInfo matInfo = MaterialInfo.of(bakedMat, Transparency.TRANSPARENT, -1, true, 0);

                BakedQuad baked = new BakedQuad(
                        positions[0], positions[1], positions[2], positions[3],
                        uvs[0], uvs[1], uvs[2], uvs[3],
                        dir,
                        matInfo
                );
                bakedQuadsList.add(baked);
                if(fixBfc){
                    // Same geometry but with inverted winding order so they are visible from the other side of the model
                    baked = new BakedQuad(positions[0], positions[3], positions[2], positions[1], uvs[0], uvs[3], uvs[2], uvs[1], dir, matInfo );
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
    public Material.Baked particleMaterial() {
        return particleMaterial;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return 0;
    }
}