package dev.minecraft.combosgears;

import com.mojang.logging.LogUtils;
import dev.minecraft.combosgears.recipe.ModRecipes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(CombosGearsMod.MODID)
public class CombosGearsMod {
    public static final String MODID = "combosgears";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CombosGearsMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register(modEventBus);
        ModBlock.register(modEventBus);
        ModBlockEntity.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuType.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("System up!");
    }
}
