package com.doodles.genuinebackpacks.content.backpack.gui;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.network.RenamePacket;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BackpackRenameScreen extends Screen {
	private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(GenuineBackpacks.MODID, "textures/gui/backpack_rename.png");
	private EditBox name;
	private Button rename;
	private final ItemStack item;
	private final Player player;
	private final InteractionHand hand;
	private boolean leveled;
	private boolean changed;
	private final int cost = 3;
	private int[] root = {0, 0};
	private int[] size = {124, 79};
	
	public BackpackRenameScreen(ItemStack item, Player player, InteractionHand hand) {
		super(GenuineBackpacks.ct("container.%s.rename"));
		this.item = item;
		this.player = player;
		this.hand = hand;
		this.leveled = false;
		this.changed = false;
	}

	protected void init() {
		super.init();
		
		this.root[0] = (this.width - this.size[0]) / 2;
		this.root[1] = (this.height - this.size[1]) / 2;

		this.name = new EditBox(this.font, this.root[0]+10, this.root[1]+20, 103, 12, this.title);
		this.name.setCanLoseFocus(false);
		this.name.setTextColor(-1);
		this.name.setTextColorUneditable(-1);
		this.name.setBordered(false);
		this.name.setMaxLength(50);
		this.name.setValue(this.item.getHoverName().getString());
		this.addWidget(this.name);
	    this.setInitialFocus(this.name);
	    this.name.setEditable(true);
		
		this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), (__) -> {
				this.onClose();
			}).bounds(this.root[0]+7, this.root[1]+52, 53, 20).build());
		this.rename = this.addRenderableWidget(Button.builder(GenuineBackpacks.ct("gui.%s.rename"), (__) -> {
				GenuineBackpacks.NETWORK.sendToServer(new RenamePacket(this.name.getValue(), this.item.getHoverName().getString(), this.hand));
				this.onClose();
			}).bounds(this.root[0]+64, this.root[1]+52, 53, 20).build());
		this.rename.active = false;
	}
	
    public void tick() {
    	this.name.tick();
    	
    	this.rename.active = false;
    	if (this.changed && this.leveled) this.rename.active = true;
    	
    	this.leveled = player.experienceLevel>=this.cost || player.isCreative();
    	this.changed = !this.name.getValue().equals(this.item.getHoverName().getString());
    }
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		graphics.pose().translate(this.root[0], this.root[1], 0);
		graphics.blit(GUI, 0, 0, 0, 0, this.size[0], this.size[1]);
		graphics.drawString(this.font, this.title, 62 - font.width(this.title) / 2, 6, 0x3f3f3f, false);
		graphics.drawString(this.font, Component.translatable("container.repair.cost", this.cost), 12, 38, this.leveled?0x7efc20:0xfc5f5f);
		graphics.pose().popPose();
		//graphics.drawCenteredString(this.font, this.title, 62, 6, 0x3f3f3f);
		//this.rebuildWidgets();
		super.render(graphics, mouseX, mouseY, partialTick);
		this.name.render(graphics, mouseX, mouseY, partialTick);
	}
}
