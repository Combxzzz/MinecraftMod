package dev.minecraft.combosgears.screen.superfurnace;

import dev.minecraft.combosgears.CombosGearsMod;
import dev.minecraft.combosgears.menu.superfurnace.SuperFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SuperFurnaceScreen extends AbstractContainerScreen<SuperFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CombosGearsMod.MODID, "textures/gui/super_furnace_gui.png");

    private static final ResourceLocation LIT_PROGRESS =
            ResourceLocation.fromNamespaceAndPath(CombosGearsMod.MODID, "textures/assets/superfurnace/lit_progress.png");

    private static final ResourceLocation PROGRESS =
            ResourceLocation.fromNamespaceAndPath(CombosGearsMod.MODID, "textures/assets/superfurnace/burn_progress.png");

    public SuperFurnaceScreen(SuperFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderProgressArrow(guiGraphics, x, y);
        renderFuelFlame(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            int progressWidth = Math.clamp(menu.getScaledProgress(), 0, 22);
            guiGraphics.blit(
                    PROGRESS,
                    x + 81, y + 33,
                    0, 0,
                    progressWidth, 15,
                    22, 15
            );
        }
    }

    private void renderFuelFlame(GuiGraphics guiGraphics, int x, int y) {
        int flameHeight = Math.clamp(menu.getScaledFuelProgress(), 0, 13);
        if (flameHeight == 0) {
            return;
        }
        guiGraphics.blit(
                LIT_PROGRESS,
                x + 56, y + 36 + (13 - flameHeight),
                0, 13 - flameHeight,
                13, flameHeight,
                13, 13
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}