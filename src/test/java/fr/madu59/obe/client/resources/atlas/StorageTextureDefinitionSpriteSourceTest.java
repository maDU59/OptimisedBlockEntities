package fr.madu59.obe.client.resources.atlas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import net.minecraft.resources.ResourceLocation;

class StorageTextureDefinitionSpriteSourceTest {
    @Test
    void discoversNestedCustomTextureIdsAndIgnoresParentAndMetadata() {
        var json = JsonParser.parseString("""
                {
                  "parent": "pack:base_definition",
                  "textures": {
                    "single": {"base": "pack:entity/custom/base", "layers": ["pack:entity/custom/overlay"]},
                    "left": {"base": "pack:entity/custom/left"}
                  },
                  "metadata": "pack:not_a_texture"
                }
                """);

        assertEquals(Set.of(
                ResourceLocation.parse("pack:entity/custom/base"),
                ResourceLocation.parse("pack:entity/custom/overlay"),
                ResourceLocation.parse("pack:entity/custom/left")
        ), StorageTextureDefinitionSpriteSource.collectTextureIds(json));
    }
}
