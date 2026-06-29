package fr.madu59.obe;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OBE.MOD_ID)
public class OBE {
	public static final String MOD_ID = "obe";

	public static final Logger LOGGER = LogUtils.getLogger();

	public OBE(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            new OBEClient(modEventBus);
        });
	}
}