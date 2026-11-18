package fr.madu59.obe.client.api.rendering;

import fr.madu59.obe.OBE;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class MeshingPredicatesAPI {
    private static final List<Predicate<BlockEntity>> PREDICATES = new CopyOnWriteArrayList<>();

    /**
     * Registers a predicate to determine if a block entity should be meshed
     * @param predicate The predicate to register. Returns true if the block entity should be meshed, false otherwise
     * @since 1.2.12
     */
    public static void registerPredicate(Predicate<BlockEntity> predicate) {
        PREDICATES.add(predicate);
    }

    @ApiStatus.Internal
    public static boolean shouldMesh(BlockEntity be) {
        for (Predicate<BlockEntity> predicate : PREDICATES) {
            try{
                if (!predicate.test(be)) {
                    return false;
                }
            } catch (Exception e) {
                OBE.LOGGER.error("Error occurred while testing meshing predicate", e);
            }
        }
        return true;
    }
}
