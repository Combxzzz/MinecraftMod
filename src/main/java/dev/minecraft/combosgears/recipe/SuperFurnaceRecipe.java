package dev.minecraft.combosgears.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class SuperFurnaceRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient inputItem;
    private final ItemStack output;
    private final int cookingTime;

    public SuperFurnaceRecipe(Ingredient inputItem, ItemStack output, int cookingTime) {
        this.inputItem = inputItem;
        this.output = output;
        this.cookingTime = cookingTime;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public Ingredient getInputItem() {
        return inputItem;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SUPER_FURNACE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SUPER_FURNACE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SuperFurnaceRecipe> {
        public static final MapCodec<SuperFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient")
                        .forGetter((SuperFurnaceRecipe recipe) -> recipe.inputItem),

                ItemStack.STRICT_CODEC.fieldOf("result")
                        .forGetter((SuperFurnaceRecipe recipe) -> recipe.output),

                Codec.INT.fieldOf("cooking_time").orElse(200)
                        .forGetter((SuperFurnaceRecipe recipe) -> recipe.cookingTime)

        ).apply(instance, SuperFurnaceRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SuperFurnaceRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.inputItem,
                ItemStack.STREAM_CODEC, recipe -> recipe.output,
                ByteBufCodecs.INT, recipe -> recipe.cookingTime,
                SuperFurnaceRecipe::new
        );

        @Override
        public MapCodec<SuperFurnaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SuperFurnaceRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
