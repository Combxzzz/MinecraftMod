package dev.minecraft.combosgears.block.superfurnace;

import dev.minecraft.combosgears.ModBlockEntity;
import dev.minecraft.combosgears.menu.superfurnace.SuperFurnaceMenu;
import dev.minecraft.combosgears.recipe.ModRecipes;
import dev.minecraft.combosgears.recipe.SuperFurnaceRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class SuperFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CRAFT_HOLD_TICKS = 7; // Duracao em ticks (~0.75s) com a seta 100% completa antes de ejetar o item

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private int progress = 0;
    private int maxProgress = 0;
    private int litTime = 0;
    private int litDuration = 0;
    private int craftDelay = 0;

    private ResourceLocation previousRecipe = null;

    private final ContainerData data;

    public SuperFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.SUPER_FURNACE_BE.get(), pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SuperFurnaceBlockEntity.this.progress;
                    case 1 -> SuperFurnaceBlockEntity.this.maxProgress;
                    case 2 -> SuperFurnaceBlockEntity.this.litTime;
                    case 3 -> SuperFurnaceBlockEntity.this.litDuration;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SuperFurnaceBlockEntity.this.progress = value;
                    case 1 -> SuperFurnaceBlockEntity.this.maxProgress = value;
                    case 2 -> SuperFurnaceBlockEntity.this.litTime = value;
                    case 3 -> SuperFurnaceBlockEntity.this.litDuration = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("super_furnace.progress", progress);
        tag.putInt("super_furnace.craft_delay", craftDelay);
        tag.putInt("super_furnace.lit_time", litTime);
        tag.putInt("super_furnace.lit_duration", litDuration);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("super_furnace.progress");
        craftDelay = tag.getInt("super_furnace.craft_delay");
        litTime = tag.getInt("super_furnace.lit_time");
        litDuration = tag.getInt("super_furnace.lit_duration");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SuperFurnaceBlockEntity entity) {
        boolean isLit = entity.isLit();
        boolean changed = false;

        if (entity.isLit()) {
            entity.litTime--;
        }

        Optional<RecipeHolder<SuperFurnaceRecipe>> recipe = entity.getCurrentRecipe();

        if (recipe.isPresent()) {
            ResourceLocation currentRecipeId = recipe.get().id();

            if (entity.previousRecipe != null && !currentRecipeId.equals(entity.previousRecipe)) {
                if (entity.progress != 0) {
                    entity.progress = 0;
                }
                entity.craftDelay = 0;
                changed = true;
            }

            entity.previousRecipe = currentRecipeId;

            int newMaxProgress = recipe.get().value().getCookingTime();

            if (entity.maxProgress != newMaxProgress) {
                entity.maxProgress = newMaxProgress;
                changed = true;
            }
        } else {
            if (entity.maxProgress != 0) {
                entity.maxProgress = 0;
                changed = true;
            }

            if (entity.progress != 0) {
                entity.progress = 0;
                entity.craftDelay = 0;
                changed = true;
            }

            if (entity.previousRecipe != null) {
                entity.previousRecipe = null;
                changed = true;
            }
        }

        boolean canCraft = recipe.isPresent()
                && entity.canInsertAmountIntoOutputSlot(recipe.get().value())
                && entity.canInsertItemIntoOutputSlot(recipe.get().value());

        if (entity.isLit() || entity.hasFuel()) {
            if (!entity.isLit() && canCraft) {
                entity.litTime = entity.getBurnDuration(entity.itemHandler.getStackInSlot(1));
                entity.litDuration = entity.litTime;
                if (entity.isLit()) {
                    changed = true;
                    if (!entity.itemHandler.getStackInSlot(1).isEmpty()) {
                        entity.itemHandler.getStackInSlot(1).shrink(1);
                    }
                }
            }

            BlockState currentState = level.getBlockState(pos);
            if (currentState.getValue(SuperFurnaceBlock.LIT) != entity.isLit()) {
                level.setBlock(pos, currentState.setValue(SuperFurnaceBlock.LIT, entity.isLit()), 3);
            }

            if (entity.isLit() && canCraft) {
                if (entity.progress < entity.maxProgress) {
                    entity.progress++;
                    entity.craftDelay = 0;
                    changed = true;
                } else {
                    entity.craftDelay++;
                    if (entity.craftDelay >= CRAFT_HOLD_TICKS) {
                        entity.craftItem(recipe.get().value());
                        entity.progress = 0;
                        entity.craftDelay = 0;
                    }
                    changed = true;
                }
            } else if (recipe.isEmpty()) {
                entity.progress = 0;
                entity.craftDelay = 0;
            }
        } else if (!entity.isLit() && entity.progress > 0) {
            entity.progress = Math.max(0, entity.progress - 2);
            entity.craftDelay = 0;
        }

        if (isLit != entity.isLit()) {
            changed = true;
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    private boolean hasFuel() {
        return !this.itemHandler.getStackInSlot(1).isEmpty();
    }

    private int getBurnDuration(ItemStack fuelStack) {
        if (fuelStack.isEmpty()) {
            return 0;
        }

        return fuelStack.getBurnTime(ModRecipes.SUPER_FURNACE_TYPE.get());
    }

    private Optional<RecipeHolder<SuperFurnaceRecipe>> getCurrentRecipe() {
        SingleRecipeInput input = new SingleRecipeInput(
                this.itemHandler.getStackInSlot(0)
        );

        return this.level.getRecipeManager().getRecipeFor(
                ModRecipes.SUPER_FURNACE_TYPE.get(),
                input,
                this.level
        );
    }

    private boolean canInsertItemIntoOutputSlot(SuperFurnaceRecipe recipe) {
        ItemStack result = recipe.getResultItem(this.level.registryAccess());
        ItemStack outputSlot = this.itemHandler.getStackInSlot(2);
        return outputSlot.isEmpty() || outputSlot.is(result.getItem());
    }

    private boolean canInsertAmountIntoOutputSlot(SuperFurnaceRecipe recipe) {
        ItemStack result = recipe.getResultItem(this.level.registryAccess());
        ItemStack outputSlot = this.itemHandler.getStackInSlot(2);
        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    private void craftItem(SuperFurnaceRecipe recipe) {
        ItemStack result = recipe.getResultItem(this.level.registryAccess());
        this.itemHandler.extractItem(0, 1, false);
        this.itemHandler.insertItem(2, result.copy(), false);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.combosgears.super_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SuperFurnaceMenu(containerId, playerInventory, this, this.data);
    }
}
