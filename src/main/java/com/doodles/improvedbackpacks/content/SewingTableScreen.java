package com.doodles.improvedbackpacks.content;

import com.doodles.improvedbackpacks.ImprovedBackpacks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SewingTableScreen extends AbstractContainerScreen<SewingTableContainer> {
   private static final ResourceLocation GUI = new ResourceLocation(ImprovedBackpacks.MODID, "textures/gui/container/sewing_table.png");
	
	public SewingTableScreen(SewingTableContainer menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}
}
