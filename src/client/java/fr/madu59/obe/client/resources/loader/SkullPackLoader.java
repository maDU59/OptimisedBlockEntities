package fr.madu59.obe.client.resources.loader;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import com.mojang.serialization.Codec;

import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;

import java.util.Map;

public class SkullPackLoader extends SimpleJsonResourceReloadListener<Map<String, ResourceLocation>> implements IdentifiableResourceReloadListener {
    private static final Codec<Map<String, ResourceLocation>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC);

    public SkullPackLoader() {
        super(MAP_CODEC, FileToIdConverter.json("blocks/obe_skulls"));
    }

    @Override
    protected void apply(Map<ResourceLocation, Map<String, ResourceLocation>> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        SkullBlockUtil.BUILT_IN_TEXTURES.clear();

        for (Map.Entry<ResourceLocation, Map<String, ResourceLocation>> entry : prepared.entrySet()) {
            ResourceLocation fileResourceLocation = entry.getKey();

            if (!fileResourceLocation.getPath().equals("config")) {
                continue;
            }

            Map<String, ResourceLocation> mappingsInFile = entry.getValue();

            SkullBlockUtil.BUILT_IN_TEXTURES.putAll(mappingsInFile);
        }
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.tryParse("obe:skulls");
    }
}
