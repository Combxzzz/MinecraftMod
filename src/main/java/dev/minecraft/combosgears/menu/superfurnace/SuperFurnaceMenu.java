package dev.minecraft.combosgears.menu.superfurnace;

import dev.minecraft.combosgears.ModBlock;
import dev.minecraft.combosgears.ModMenuType;
import dev.minecraft.combosgears.block.superfurnace.SuperFurnaceBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SuperFurnaceMenu extends AbstractContainerMenu {
    private final SuperFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public SuperFurnaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public SuperFurnaceMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuType.SUPER_FURNACE_MENU.get(), containerId);
        if (((SuperFurnaceBlockEntity) entity).getItemHandler().getSlots() < 3) {
            throw new IllegalStateException("SuperFurnaceBlockEntity tem menos de 3 slots!");
        }
        this.blockEntity = ((SuperFurnaceBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 57, 16));  // Entrada
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 57, 52));  // Combustível
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 117, 34)); // Saída

        layoutPlayerInventory(inv, 9, 83);
        this.addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int progressArrowSize = 22; // Tamanho em pixels da seta na textura
        if (maxProgress <= 0 || progress <= 0) {
            return 0;
        }
        // Divisao com teto (ceiling): garante que o pixel 22 (seta 100% completa)
        // apareca durante sua fatia proporcional final e durante o craft delay,
        // mantendo a mesma duracao uniforme de todos os outros 21 pixels.
        return Math.min(progressArrowSize, (progress * progressArrowSize + maxProgress - 1) / maxProgress);
    }

    public boolean isLit() {
        return data.get(2) > 0;
    }

    public int getScaledFuelProgress() {
        int litTime = data.get(2);
        int litDuration = data.get(3);
        int flameSize = 13;
        if (litDuration <= 0 || litTime <= 0) {
            return 0;
        }
        return (litTime * flameSize + litDuration - 1) / litDuration;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 3) {
                if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlock.SUPER_FURNACE.get());
    }

    private void layoutPlayerInventory(Inventory playerInventory, int leftCol, int topRow) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, leftCol + col * 18, topRow + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, leftCol + col * 18, topRow + 58));
        }
    }
}
