package com.doodles.improvedbackpacks.menus;

import com.doodles.improvedbackpacks.ImprovedBackpacks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SewingTableScreen extends AbstractContainerScreen<SewingTableMenu> {
   private static final ResourceLocation BACKGROUND = new ResourceLocation(ImprovedBackpacks.MODID, "textures/gui/container/sewing_table.png");
	
	public SewingTableScreen(SewingTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	@Override
	protected void init() {
	    super.init();
	}
	@Override
	public void containerTick() {
	    super.containerTick();
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
	    this.renderBackground(graphics);
	    super.render(graphics, mouseX, mouseY, partialTick);
	}
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
	    super.renderLabels(graphics, mouseX, mouseY);
	}
	
	@Override
	public void onClose() {
	    // Stop any handlers here

	    // Call last in case it interferes with the override
	    super.onClose();
	}

	@Override
	public void removed() {
	    // Reset initial states here

	    // Call last in case it interferes with the override
	    super.removed();
	}
}
