package dev.minecraft.combosgears.recipe;

import dev.minecraft.combosgears.CombosGearsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE =
            DeferredRegister.create(Registries.RECIPE_TYPE, CombosGearsMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CombosGearsMod.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SuperFurnaceRecipe>> SUPER_FURNACE_TYPE =
            RECIPE_TYPE.register("super_furnace", () -> new RecipeType<SuperFurnaceRecipe>() {
                @Override
                public String toString() {
                    return "super_furnace";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SuperFurnaceRecipe>> SUPER_FURNACE_SERIALIZER =
            RECIPE_SERIALIZER.register("super_furnace", SuperFurnaceRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPE.register(eventBus);
        RECIPE_SERIALIZER.register(eventBus);
    }
}
