package net.ultrad00d.ForgottenCantrips.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class SharedInventoryScreen extends AbstractContainerScreen<SharedInventoryMenu> {
    private static final ResourceLocation CHEST_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID,"textures/gui/container/spectral_chest.png");

    public SharedInventoryScreen(SharedInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(CHEST_GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}