package fr.madu59.obe.client.resources.loader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;

import java.util.Map;

public class SkullPackLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public SkullPackLoader() {
        super(GSON, "blocks/obe_skulls");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SkullBlockUtil.BUILT_IN_TEXTURES.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : prepared.entrySet()) {
            ResourceLocation fileResourceLocation = entry.getKey();

            if (!fileResourceLocation.getPath().equals("config")) {
                continue;
            }

            JsonElement element = entry.getValue();
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                
                for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
                    String skullType = jsonEntry.getKey();
                    String texturePath = jsonEntry.getValue().getAsString();
                    
                    ResourceLocation textureLocation = ResourceLocation.tryParse(texturePath);
                    if (textureLocation != null) {
                        SkullBlockUtil.BUILT_IN_TEXTURES.put(skullType, textureLocation);
                    }
                }
            }
        }
    }
}
