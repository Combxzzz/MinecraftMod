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

    public SuperFurnaceScreen(SuperFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Renderiza o fundo principal da GUI (substituição limpa para o 1.21)
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderProgressArrow(guiGraphics, x, y);
        renderFuelFlame(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            int l = menu.getScaledProgress();
            // Desenha a seta de progresso preenchendo da esquerda para a direita
            guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 14, l + 1, 16, 256, 256);
        }
    }

    private void renderFuelFlame(GuiGraphics guiGraphics, int x, int y) {
        int k = menu.getScaledFuelProgress();
        // Desenha o fogo do combustível de baixo para cima
        guiGraphics.blit(TEXTURE, x + 56, y + 36 + (14 - k), 176, 14 - k, 14, k + 1, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
