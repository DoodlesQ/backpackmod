package com.doodles.genuinebackpacks.content.backpack.gui;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu>{
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(GenuineBackpacks.MODID, "textures/gui/container/backpack.png");

    private final int invHeight = 97;
    private int packHeight = 54;
    private int slots;
    private int rows;
    //private int center;
    
	public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		
		this.slots = menu.slotCount - 1;
		this.rows = (int) Math.ceil(menu.slotCount / 9.0f);
		this.packHeight += 18 * (this.rows - 2);
		this.imageHeight = this.packHeight + this.invHeight;
		this.inventoryLabelY = this.imageHeight - 94;
		//this.center = this.imageHeight / 2;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		//graphics.blit(GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		renderBackpack(graphics);
		renderInventory(graphics);
	}
	
	private void renderBackpack(GuiGraphics graphics) {
		graphics.blit(GUI, this.leftPos, this.topPos, 0, 0, 176, this.packHeight);
		int i = 0;
		for (int y = 0; y < this.rows; y++) {
			for (int x = 0; x < 9; x++) {
				graphics.blit(GUI, this.leftPos+7+(x*18), this.topPos+18+(y*18), 176, 0, 18, 18);
				if (++i > this.slots) break;
			}
		}
	}
	private void renderInventory(GuiGraphics graphics) {
		graphics.blit(GUI, this.leftPos, this.topPos+this.packHeight, 0, 143, 176, this.invHeight);
	}

}
