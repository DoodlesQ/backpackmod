package com.doodles.genuinebackpacks.content.backpack.gui;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.content.backpack.EnderBackpackItem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnderBackpackMenu extends AbstractContainerMenu {
	private final Container container;
	private final int containerRows;
	private final ItemStack backpack;
	private final ContainerLevelAccess access;

	public EnderBackpackMenu(int containerId, Player player, ItemStack backpack, Container container, ContainerLevelAccess access) {
		super(MenuType.GENERIC_9x3, containerId);
		this.backpack = backpack;
		this.access = access;
		int rows = 3;
		checkContainerSize(container, rows * 9);
		this.container = container;
		this.containerRows = rows;
		container.startOpen(player);
		
		int i = (this.containerRows - 4) * 18;
		for(int j = 0; j < this.containerRows; ++j) {
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(container, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for(int l = 0; l < 3; ++l) {
			for(int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(player.getInventory(), j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
			}
		}

		for(int j = 0; j < 9; ++j) {
			int x = 8+(j*18);
			Slot slot = new Slot(player.getInventory(), j, x, 161 + i);
			if (backpack.equals(player.getInventory().getItem(j))) {
				slot = new Slot(player.getInventory(), j, x, 161 + i) {
					@Override
					public boolean mayPickup(Player player) { return false; }
				};
			}
			this.addSlot(slot);
		}

		player.level().playSound(null, player.getOnPos(), GenuineBackpacks.ENDER_BACKPACK_OPEN_SOUND.get(), SoundSource.PLAYERS);
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	 public boolean stillValid(Player player) {
		boolean equipped = EnderBackpackItem.wornBy(player, this.backpack);
		boolean held = this.backpack.equals(player.getItemInHand(InteractionHand.MAIN_HAND));
		return equipped || held || AbstractContainerMenu.stillValid(this.access, player, GenuineBackpacks.BACKPACK_BLOCK.get());
	 }

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
	 */
	 public ItemStack quickMoveStack(Player player, int index) {
		 ItemStack itemstack = ItemStack.EMPTY;
		 Slot slot = this.slots.get(index);
		 if (slot != null && slot.hasItem()) {
			 ItemStack itemstack1 = slot.getItem();
			 itemstack = itemstack1.copy();
			 if (index < this.containerRows * 9) {
				 if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
					 return ItemStack.EMPTY;
				 }
			 } else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
				 return ItemStack.EMPTY;
			 }

			 if (itemstack1.isEmpty()) {
				 slot.setByPlayer(ItemStack.EMPTY);
			 } else {
				 slot.setChanged();
			 }
		 }

		 return itemstack;
	 }

	 /**
	  * Called when the container is closed.
	  */
	 public void removed(Player player) {
		 CompoundTag tag = this.backpack.getOrCreateTagElement("display");
		 tag.putBoolean("open", false);
		 player.level().playSound(null, player.getOnPos(), GenuineBackpacks.ENDER_BACKPACK_CLOSE_SOUND.get(), SoundSource.PLAYERS);
		 super.removed(player);
		 this.container.stopOpen(player);
	 }

	 public Container getContainer() {
		 return this.container;
	 }

	 public int getRowCount() {
		 return this.containerRows;
	 }
}