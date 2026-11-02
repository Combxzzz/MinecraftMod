package dev.minecraft.combosgears;

import dev.minecraft.combosgears.block.superfurnace.SuperFurnaceBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CombosGearsMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperFurnaceBlockEntity>> SUPER_FURNACE_BE =
            BLOCK_ENTITIES.register("super_furnace_be", () ->
                    BlockEntityType.Builder.of(
                            SuperFurnaceBlockEntity::new,
                            ModBlock.SUPER_FURNACE.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}