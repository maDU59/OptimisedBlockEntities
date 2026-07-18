package fr.madu59.obe.client.resources.loader;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import com.mojang.serialization.Codec;

import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;

import java.util.Map;

public class SkullPackLoader extends SimpleJsonResourceReloadListener<Map<String, Identifier>> {
    private static final Codec<Map<String, Identifier>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, Identifier.CODEC);

    public SkullPackLoader() {
        super(MAP_CODEC, FileToIdConverter.json("blocks/obe_skulls"));
    }

    @Override
    protected void apply(Map<Identifier, Map<String, Identifier>> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        SkullBlockUtil.BUILT_IN_TEXTURES.clear();

        for (Map.Entry<Identifier, Map<String, Identifier>> entry : prepared.entrySet()) {
            Identifier fileIdentifier = entry.getKey();

            if (!fileIdentifier.getPath().equals("config")) {
                continue;
            }

            Map<String, Identifier> mappingsInFile = entry.getValue();

            SkullBlockUtil.BUILT_IN_TEXTURES.putAll(mappingsInFile);
        }
    }
}
