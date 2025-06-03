package com.doodles.genuinebackpacks.content.sewingtable;

import java.util.List;
import java.util.Optional;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.recipe.SewingRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SewingTableMenu extends AbstractContainerMenu {
    private final Player player;
    private final ContainerLevelAccess access;
    private final SewingTableTileEntity table;
    private final BlockPos pos;
    private SewingRecipe recipe;

	// Constuctor
	public SewingTableMenu(int containerId, Player player, BlockPos pos) {
		this(containerId, player, pos, ContainerLevelAccess.NULL);
	}
	public SewingTableMenu(int containerId, Player player, BlockPos pos, ContainerLevelAccess access) {
		super(GenuineBackpacks.SEWING_TABLE_MENU.get(), containerId);
		this.player = player;
		this.access = access;
		this.pos = pos;
		this.recipe = null;
		
        if (player.level().getBlockEntity(pos) instanceof SewingTableTileEntity table) {
        	this.table = table;
			this.addSlot(new SlotItemHandler(table.getItems(), 0, 15, 15) {
				public boolean mayPlace(ItemStack item) {
					return item.is(GenuineBackpacks.items.get("spool").get());
				}
				@Override
				public void setChanged() {
					table.setSpool(this.getItem().is(GenuineBackpacks.items.get("spool").get()));
					slotsChanged(new ISHWrapper(table.getItems()));
					super.setChanged();
				}
			});
			this.addSlot(new SlotItemHandler(table.getItems(), 1, 15, 37) {
				public boolean mayPlace(ItemStack item) {
					return item.is(Items.SHEARS);
				}
				@Override
				public void setChanged() {
					table.setShears(this.getItem().is(Items.SHEARS));
					slotsChanged(new ISHWrapper(table.getItems()));
					super.setChanged();
				}
			});
			this.addSlot(new InputSlot(table.getItems(), 2, 68, 14));
			this.addSlot(new InputSlot(table.getItems(), 3, 68, 38));
			this.addSlot(new ResultSlot(table.getItems(), 4, 134, 26) {
			});

			// Player Inventory
			for(int y = 0; y < 3; ++y) {
				for(int x = 0; x < 9; ++x) {
					this.addSlot(new Slot(player.getInventory(), 9+x+(y*9), 8+(x*18), 84+(y*18)));
				}
			}
			for(int x = 0; x < 9; ++x) {
				this.addSlot(new Slot(player.getInventory(), x, 8+(x*18), 142));
			}
			
			executeCraft();
        } else {
        	this.table = null;
        }
	}
	
	public ItemStackHandler getInventory() {
		return this.table.getItems();
	}
	
	private void updateCraft(Level level) {
		if (this.recipe == null) return;
		//level.playSound(null, this.pos, GenuineBackpacks.SEWING_CRAFT_SOUND.get(), SoundSource.BLOCKS);
		this.table.getItems().getStackInSlot(0).shrink(1);
		ItemStack shears = this.table.getItems().getStackInSlot(1);
		shears.hurt(1, RandomSource.create(), null);
		if (shears.getDamageValue() >= shears.getMaxDamage()) {
			this.table.getItems().setStackInSlot(1, ItemStack.EMPTY);
			this.table.setSpool(false);
			level.playSound(null, this.pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS);
		}
		for (ItemStack s : List.of(this.table.getItems().getStackInSlot(2), this.table.getItems().getStackInSlot(3)) ) {
			for (int i = 0; i < this.recipe.size(); i++) {
				if (this.recipe.getIngredients().get(i).test(s)) {
					s.shrink(this.recipe.getCounts().get(i));
				}
			}
		}
        this.table.setChanged();
		executeCraft();
	}
	
	@Override
    public void slotsChanged(Container inventory) {
		super.slotsChanged(inventory);
		executeCraft();
    }
	private void executeCraft() {
    	this.access.execute((level, pos) -> {
    		attemptCraft(this, level, this.player, new ISHWrapper(this.table.getItems()), this.table);
    	});
	}
    protected void attemptCraft(SewingTableMenu menu, Level level, Player player, Container inventory, SewingTableTileEntity table) {
    	if (!level.isClientSide) {
            ItemStack output = ItemStack.EMPTY;
    		Optional<SewingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(GenuineBackpacks.SEWING_RECIPE_TYPE.get(), inventory, level);
    		if (optional.isPresent()) {
                output = optional.get().assemble(inventory, level.registryAccess());
                this.recipe = optional.get();
    		} else {
    			//hardcoded easteregg recipes
    			/*
    			if (inventory.getItem(0).is(GenuineBackpacks.items.get("spool").get()) && inventory.getItem(1).is(Items.SHEARS)) {
	    			ItemStack l = inventory.getItem(2);
	    			ItemStack e = inventory.getItem(3);
	    			if (l.is(GenuineBackpacks.items.get("tanned_leather").get()) && l.getCount() >= 5) {
	    				if (e.is(Items.AMETHYST_BLOCK) && e.getCount() >= 3) {
	    					output = new ItemStack(GenuineBackpacks.BACKPACK.get());
	    					BackpackItem.setSpecial(output, BackpackItem.TRANS);
	    	    			this.recipe = new SewingRecipe(e, output);
	    				}
	    				if (e.is(Items.HONEYCOMB_BLOCK) && e.getCount() >= 3) {
	    					output = new ItemStack(GenuineBackpacks.BACKPACK.get());
	    					BackpackItem.setSpecial(output, BackpackItem.BEE);
	    	    			this.recipe = new SewingRecipe(e, output);
	    				}
	    			}
    			}
    			*/
    		}
            inventory.setItem(4, output);
            table.setChanged();
    	}
    }

    private boolean canMoveTo(ItemStack target, ItemStack item) {
    	return target.isEmpty() || (ItemStack.isSameItem(item, target) && target.isStackable() && target.getMaxStackSize() > target.getCount());
    }
    
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		// If shift-clicking an actual slot
		if (slot != null && slot.hasItem()) {
			ItemStack item_moved = slot.getItem();
	        itemstack = item_moved.copy();
	        ItemStack item_target0 = this.slots.get(0).getItem();
	        ItemStack item_target1 = this.slots.get(1).getItem();
	        ItemStack item_target2 = this.slots.get(2).getItem();
	        ItemStack item_target3 = this.slots.get(3).getItem();
	        
	        // if output slot
	        if (index == 4) {
	        	if (!this.moveItemStackTo(item_moved, 5, 41, true)) {
	        		return ItemStack.EMPTY;
	        	}
	            slot.onQuickCraft(item_moved, itemstack);
	        // if coming from inventory
	        } else if (index > 3) {
	        	boolean canMove0 = canMoveTo(item_target0, item_moved);
	        	boolean canMove1 = canMoveTo(item_target1, item_moved);
	        	boolean canMove2 = canMoveTo(item_target2, item_moved);
	        	boolean canMove3 = canMoveTo(item_target3, item_moved);
	        	// if no room in table
	        	if (!canMove0 && !canMove1 && !canMove2 && !canMove3) {
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
			        if (!this.moveItemStackTo(item_moved, 0, 4, false)) {
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

	@Override
	public boolean stillValid(Player player) {
		return AbstractContainerMenu.stillValid(this.access, player, GenuineBackpacks.SEWING_TABLE.get());
	}
	
	private class InputSlot extends SlotItemHandler {
		ItemStackHandler items;
		public InputSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
			this.items = itemHandler;
		}
		
		@Override
		public void setChanged() {
			slotsChanged(new ISHWrapper(items));
			super.setChanged();
		}
		
		public boolean mayPlace(ItemStack item) { return true; }
	}
	
	private class ResultSlot extends SlotItemHandler {
		public ResultSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}
		
		public boolean mayPlace(ItemStack stack) { return false; }
		
		public void onTake(Player player, ItemStack stack) {
			updateCraft(player.level());
		}
	}
	
	private class ISHWrapper extends SimpleContainer {
		
		private final ItemStackHandler inventory;
		
		public ISHWrapper(ItemStackHandler inventory) {
			super(inventory.getSlots());
			this.inventory = inventory;
			NonNullList<ItemStack> items = NonNullList.create();
			for (int i = 0; i < inventory.getSlots(); i++) {
				this.setItem(i, inventory.getStackInSlot(i));
				this.setChanged();
				items.add(inventory.getStackInSlot(i));
			}
		}
		
		@SuppressWarnings("unused")
		public NonNullList<ItemStack> getItems() {
			NonNullList<ItemStack> items = NonNullList.create();
			for (int i = 0; i < this.getContainerSize(); i++) {
				items.add(this.getItem(i));
			}
			return items;
		}
		
		@Override
		public void setItem(int i, ItemStack s) {
			super.setItem(i, s);
			inventory.setStackInSlot(i, s);
		}
	}
}

