package dev.minecraft.combosgears;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CombosGearsMod.MODID);

    public static final DeferredItem<Item> RUBY_ITEM = ITEMS.register("ruby", () -> new Item(
            new Item.Properties().stacksTo(64)
    ));

    public static final DeferredItem<SwordItem> RUBY_SWORD = ITEMS.register("ruby_sword", () -> new SwordItem(
            Tiers.IRON,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(Tiers.IRON, 5, -2.4f)
            )
    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
