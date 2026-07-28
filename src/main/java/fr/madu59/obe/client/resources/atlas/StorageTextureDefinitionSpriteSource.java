package fr.madu59.obe.client.resources.atlas;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.MapCodec;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedStorageDiagnostics;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/** Discovers texture IDs before stitching from Storage's data-driven definitions. */
public final class StorageTextureDefinitionSpriteSource implements SpriteSource {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OBE.MOD_ID, "storage_texture_definitions");
    public static final StorageTextureDefinitionSpriteSource INSTANCE = new StorageTextureDefinitionSpriteSource();
    public static final MapCodec<StorageTextureDefinitionSpriteSource> CODEC = MapCodec.unit(INSTANCE);
    private static final FileToIdConverter DEFINITIONS = FileToIdConverter.json("storage_texture_definitions");
    private static SpriteSourceType type;

    private StorageTextureDefinitionSpriteSource() {
    }

    public static void setType(SpriteSourceType registeredType) {
        type = registeredType;
    }

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        Set<ResourceLocation> textures = new LinkedHashSet<>();
        for (Map.Entry<ResourceLocation, Resource> entry : DEFINITIONS.listMatchingResources(resourceManager).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                textures.addAll(collectTextureIds(JsonParser.parseReader(reader)));
            } catch (IOException | RuntimeException exception) {
                OBE.LOGGER.warn("Could not inspect storage texture definition {}", entry.getKey(), exception);
            }
        }
        for (ResourceLocation texture : textures) {
            ResourceLocation file = TEXTURE_ID_CONVERTER.idToFile(texture);
            resourceManager.getResource(file).ifPresentOrElse(
                    resource -> {
                        output.add(texture, resource);
                        SophisticatedStorageDiagnostics.discoveredAtlasSprite();
                    },
                    () -> {
                        SophisticatedStorageDiagnostics.missingAtlasResource();
                        OBE.LOGGER.warn("Storage texture definition references missing sprite {}", texture);
                    }
            );
        }
    }

    static Set<ResourceLocation> collectTextureIds(JsonElement definition) {
        Set<ResourceLocation> textures = new LinkedHashSet<>();
        if (definition != null && definition.isJsonObject()) {
            JsonObject object = definition.getAsJsonObject();
            collectStrings(object.get("textures"), textures);
        }
        return Set.copyOf(textures);
    }

    private static void collectStrings(JsonElement element, Set<ResourceLocation> output) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            ResourceLocation parsed = ResourceLocation.tryParse(element.getAsString());
            if (parsed != null) {
                output.add(parsed);
            }
            return;
        }
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(child -> collectStrings(child, output));
        } else if (element.isJsonObject()) {
            element.getAsJsonObject().entrySet().forEach(entry -> collectStrings(entry.getValue(), output));
        }
    }

    @Override
    public SpriteSourceType type() {
        if (type == null) {
            throw new IllegalStateException("OBE storage texture sprite source was not registered");
        }
        return type;
    }
}
