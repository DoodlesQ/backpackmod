package com.doodles.improvedbackpacks.menus;

import com.doodles.improvedbackpacks.ImprovedBackpacks;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class SewingTableMenu extends AbstractContainerMenu {
	private ContainerLevelAccess access;
	
	private final Container resultSlots = new ResultContainer();
	final Container inputSlots = new SimpleContainer(4) {
		public void setChanged() {
			super.setChanged();
			SewingTableMenu.this.slotsChanged(this);
		}
	};

	// Client Constructor
	public SewingTableMenu(int containerId, Inventory playerInv) {
		this(containerId, playerInv, ContainerLevelAccess.NULL);
	}
	// Server Constuctor
	public SewingTableMenu(int containerId, Inventory playerInv, ContainerLevelAccess access) {
		super(ImprovedBackpacks.SEWING_TABLE_MENU.get(), containerId);
		this.access = access;
		this.addSlot(new Slot(this.inputSlots, 0, 20, 24) {
			public boolean mayPlace(ItemStack item) {
				return item.is(ImprovedBackpacks.SEWING_SPOOL.get());
			}
		});
		this.addSlot(new Slot(this.inputSlots, 1, 20, 47) {
			public boolean mayPlace(ItemStack item) {
				return item.is(Items.SHEARS);
			}
		});
		this.addSlot(new Slot(this.inputSlots, 2, 42, 35) {
			public boolean mayPlace(ItemStack item) {
				return true;
			}
		});
		this.addSlot(new Slot(this.inputSlots, 3, 87, 35) {
			public boolean mayPlace(ItemStack item) {
				return true;
			}
		});
		this.addSlot(new Slot(this.resultSlots, 4, 141, 35) {
			// Deny Placement in Slot
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
			public void onTake(Player player, ItemStack stack) {
				SewingTableMenu.this.inputSlots.setItem(3, ItemStack.EMPTY);
				SewingTableMenu.this.inputSlots.setItem(4, ItemStack.EMPTY);
			}
		});

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
		}
	}
    public void slotsChanged(Container inventory) {
    	super.slotsChanged(inventory);
    }

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		// If shift-clicking an actual slot
		if (slot != null && slot.hasItem()) {
			ItemStack item_moved = slot.getItem();
	        itemstack = item_moved.copy();
	        ItemStack item_target0 = this.inputSlots.getItem(0);
	        ItemStack item_target1 = this.inputSlots.getItem(1);
	        ItemStack item_target2 = this.inputSlots.getItem(2);
	        ItemStack item_target3 = this.inputSlots.getItem(3);
	        
	        // if output slot
	        if (index == 4) {
	        	if (!this.moveItemStackTo(item_moved, 5, 41, true)) {
	        		return ItemStack.EMPTY;
	        	}
	            slot.onQuickCraft(item_moved, itemstack);
	        // if coming from inventory
	        } else if (index > 3) {
	        	// if no room in table
	        	if (!item_target0.isEmpty() && !item_target1.isEmpty() && !item_target2.isEmpty() && !item_target3.isEmpty()) {
	        		if (index >= 5 && index < 32) {
	        			// if coming from biginv, move to hotbar
	        			if (!this.moveItemStackTo(item_moved, 32, 41, false)) {
	        				return ItemStack.EMPTY;
	        			}
	        		} else if (index >= 32 && index < 41 && !this.moveItemStackTo(item_moved, 5, 32, false)) {
	        			// else, vice versa
	        			return ItemStack.EMPTY;
	        		}
	        	} else {
	        		if (item_moved.is(ImprovedBackpacks.SEWING_SPOOL.get()) && item_target0.isEmpty()) {
			        	if (!this.moveItemStackTo(item_moved, 0, 1, false)) {
			        		return ItemStack.EMPTY;
			        	}
			        } else if (item_moved.is(Items.SHEARS) && item_target1.isEmpty()) {
			        	if (!this.moveItemStackTo(item_moved, 1, 2, false)) {
			        		return ItemStack.EMPTY;
			        	}
			        } else if (!this.moveItemStackTo(item_moved, 2, 4, false)) {
		        		return ItemStack.EMPTY;
			        }
	        	}
	        } else if (!this.moveItemStackTo(item_moved, 5, 41, false)) {
	        	return ItemStack.EMPTY;
	        }

	        if (item_moved.isEmpty()) {
	        	slot.setByPlayer(ItemStack.EMPTY);
	        } else {
	        	slot.setChanged();
	        }

	        if (item_moved.getCount() == itemstack.getCount()) {
	        	return ItemStack.EMPTY;
	        }

	        slot.onTake(player, item_moved);
		}
		// fallback
		return itemstack;
	}

	// Define menu as part of Sewing Table block
	@Override
	public boolean stillValid(Player player) {
		return AbstractContainerMenu.stillValid(this.access, player, ImprovedBackpacks.SEWING_TABLE.get());
	}

}
