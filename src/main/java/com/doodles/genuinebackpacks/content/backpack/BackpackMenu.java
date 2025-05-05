package com.doodles.genuinebackpacks.content.backpack;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackMenu extends AbstractContainerMenu {
	
    private final ContainerLevelAccess access;
    private final ItemStack backpack;
    public int slotCount = 63;
    private int rows;
    
    private ItemStackHandler items;

	public BackpackMenu(int id, Player player, ItemStack backpack) {
		this(id, player, backpack, ContainerLevelAccess.NULL);
	}
	protected BackpackMenu(int id, Player player, ItemStack backpack, ContainerLevelAccess access) {
		super(GenuineBackpacks.BACKPACK_MENU.get(), id);
		
		this.access = access;
		this.backpack = backpack;
		this.slotCount = BackpackItem.getTotalSlots(backpack);
		this.items = BackpackItem.loadItems(backpack);
		
		this.rows = (int) Math.ceil((this.slotCount-1) / 9.0f);
		int offset = 69 + 18 * (this.rows - 2);
		
		int i = 0;
		for (int y = 0; y < this.rows; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new SlotItemHandler(this.items, i, 8+(x*18), 19+(y*18)));
				if (++i > this.slotCount) break;
			}
		}
		this.slotCount = i;
		
		// Player Inventory
		for(int y = 0; y < 3; ++y) {
			for(int x = 0; x < 9; ++x) {
				Slot slot = new Slot(player.getInventory(), 9+x+(y*9), 8+(x*18), offset+(y*18));
				this.addSlot(slot);
			}
		}
		for(int j = 0; j < 9; ++j) {
			int x = 8+(j*18);
			Slot slot = new Slot(player.getInventory(), j, x, offset+58);
			if (backpack.equals(player.getSlot(j).get())) {
				slot = new Slot(player.getInventory(), j, x, offset+58) {
					@Override
					public boolean mayPickup(Player player) { return false; }
				};
			}
			this.addSlot(slot);
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack item_moved = slot.getItem();
			itemstack = item_moved.copy();
			
			if (index < this.slotCount) {
				if (!this.moveItemStackTo(item_moved, this.slotCount, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(item_moved, 0, this.slotCount, false)) {
				return ItemStack.EMPTY;
			}

			if (item_moved.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		// fallback
		return itemstack;
	}

	@Override
	public boolean stillValid(Player player) {
		boolean equipped = BackpackItem.wornBy(player, this.backpack);
		boolean held = this.backpack.equals(player.getItemInHand(InteractionHand.MAIN_HAND));
		return equipped || held || AbstractContainerMenu.stillValid(this.access, player, GenuineBackpacks.BACKPACK_BLOCK.get());
	}

	@Override
    public void broadcastChanges() {
		super.broadcastChanges();
		BackpackItem.saveItems(backpack, items);
    }

	@Override
    public void removed(Player player) {
		CompoundTag tag = this.backpack.getOrCreateTagElement("display");
		tag.putBoolean("open", false);
		super.removed(player);
    }
}
