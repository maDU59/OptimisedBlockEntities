package fr.madu59.obe.client.renderer.entity;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import fr.madu59.obe.client.renderer.entity.ext.EntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class MeshableEntityTracker {
    private static final Map<SectionPos, Map<Integer, MeshableEntityData>> meshableEntities = new ConcurrentHashMap<>();

    public static void registerMeshableEntity(Entity entity, BlockPos pos) {
        if(!entity.level().isClientSide() || !((EntityExt)entity).isSupported()) return;

        int id = 0;
        try{
            id = entity.getId();
        }
        catch(Exception e){
            return;
        }

        MeshableEntityData data = null;
        SectionPos sectionPos = SectionPos.of(pos);

        if(entity.getType() == EntityTypes.CUSHION && entity instanceof Cushion cushion){
            data = new CushionSnapshot(entity.level(), id, sectionPos, cushion.getPos(), cushion.position(), cushion.getRotationVector(), cushion.getColor());
        }
        else if(entity.getType() == EntityTypes.ITEM_FRAME && entity instanceof ItemFrame itemFrame){
            data = new ItemFrameSnapshot(entity.level(), id, sectionPos, itemFrame.getPos(), itemFrame.position(), itemFrame.getRotationVector());
        }

        if(data == null) return;
        getSectionMap(sectionPos).put(id, data);
        ((EntityExt)entity).renderModeDelayed(RenderMode.TERRAIN);
        RenderModeManager.setDirty(pos);
    }

    public static void deregisterMeshableEntity(Entity entity, BlockPos pos) {
        if(!entity.level().isClientSide() || !((EntityExt)entity).isSupported()) return;

        int id = 0;
        try{
            id = entity.getId();
        }
        catch(Exception e){
            return;
        }

        getSectionMap(pos).remove(id);
        ChunkTaskHolder.addTask(SectionPos.of(pos), () -> ((EntityExt)entity).renderMode(RenderMode.ENTITY));
        RenderModeManager.setDirty(pos);
    }

    public static void deleteInvalidMeshableEntity(int id, BlockPos pos) {
        getSectionMap(pos).remove(id);
    }

    public static void updateMeshableEntity(Entity entity, BlockPos pos) {
        if(!entity.level().isClientSide() || !((EntityExt)entity).isSupported()) return;
        if(entity.isRemoved()) deregisterMeshableEntity(entity, pos);
        else registerMeshableEntity(entity, pos);
    }

    public static Collection<MeshableEntityData> getMeshableEntities(SectionPos pos) {
        Map<Integer, MeshableEntityData> map = meshableEntities.get(pos);
        if(map == null) return null;
        else return map.values();
    }

    private static Map<Integer, MeshableEntityData> getSectionMap(BlockPos pos){
        return getSectionMap(SectionPos.of(pos));
    }

    private static Map<Integer, MeshableEntityData> getSectionMap(SectionPos pos){
        return meshableEntities.computeIfAbsent(pos, k -> new ConcurrentHashMap<>());
    }

    public interface MeshableEntityData {
        int id();
        BlockPos blockPos();
        Vec3 pos();
        SectionPos sectionPos();
        Level level();
    }

    public record CushionSnapshot(Level level, int id, SectionPos sectionPos, BlockPos blockPos, Vec3 pos, Vec2 rotation, DyeColor color) implements MeshableEntityData {}

    public record ItemFrameSnapshot(Level level,int id, SectionPos sectionPos, BlockPos blockPos, Vec3 pos, Vec2 rotation) implements MeshableEntityData {}
}
