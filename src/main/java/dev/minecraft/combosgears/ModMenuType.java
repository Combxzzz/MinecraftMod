package dev.minecraft.combosgears;

import dev.minecraft.combosgears.menu.superfurnace.SuperFurnaceMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuType {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, CombosGearsMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SuperFurnaceMenu>> SUPER_FURNACE_MENU =
            MENUS.register("super_furnace_menu", () -> IMenuTypeExtension.create(SuperFurnaceMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
