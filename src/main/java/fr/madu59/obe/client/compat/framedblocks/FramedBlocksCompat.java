package fr.madu59.obe.client.compat.framedblocks;

import fr.madu59.obe.client.api.registry.RegistryApi;
import io.github.xfacthd.framedblocks.common.FBContent;

public class FramedBlocksCompat {
    public static void init() {
        RegistryApi.registerBlockEntityType("chest", FBContent.BE_TYPE_FRAMED_CHEST.get());
        RegistryApi.registerModelBuilder(FBContent.BE_TYPE_FRAMED_CHEST.get(), (state, originalModel) -> originalModel);
    }
}
