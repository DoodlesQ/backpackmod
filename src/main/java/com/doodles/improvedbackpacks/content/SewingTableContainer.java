package com.doodles.improvedbackpacks.content;

import java.util.List;

import com.doodles.improvedbackpacks.ImprovedBackpacks;
import com.doodles.improvedbackpacks.recipe.SewingRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

// TODO:
//
// Ask if shear damage is bad code
// Ask if slotsChanged is bad code

public class SewingTableContainer extends AbstractContainerMenu {
    private final Player player;
    private final ContainerLevelAccess access;
    private final List<SewingRecipe> recipes;

	// Constuctor
	public SewingTableContainer(int containerId, Player player, BlockPos pos) {
		this(containerId, player, pos, ContainerLevelAccess.NULL);
	}
	public SewingTableContainer(int containerId, Player player, BlockPos pos, ContainerLevelAccess access) {
		super(ImprovedBackpacks.SEWING_TABLE_MENU.get(), containerId);
		this.player = player;
		this.access = access;
		this.recipes = this.player.level().getRecipeManager().getAllRecipesFor(ImprovedBackpacks.SEWING_RECIPE_TYPE.get());
		
        if (player.level().getBlockEntity(pos) instanceof SewingTableEntity table) {
			this.addSlot(new SlotItemHandler(table.getInput(), 0, 20, 24) {
				public boolean mayPlace(ItemStack item) {
					return item.is(ImprovedBackpacks.SEWING_SPOOL.get());
				}
			});
			this.addSlot(new SlotItemHandler(table.getInput(), 1, 20, 47) {
				public boolean mayPlace(ItemStack item) {
					return item.is(Items.SHEARS);
				}
			});
			this.addSlot(new SlotItemHandler(table.getInput(), 2, 42, 35) {
				public boolean mayPlace(ItemStack item) { return true; }
			});
			this.addSlot(new SlotItemHandler(table.getInput(), 3, 87, 35) {
				public boolean mayPlace(ItemStack item) { return true; }
			});
			this.addSlot(new SlotItemHandler(table.getResult(), 0, 141, 35) {
				// Deny Placement in Slot
				public boolean mayPlace(ItemStack stack) { return false; }
				
				public void onTake(Player player, ItemStack stack) {
					ItemStackHandler i = table.getInput();
					i.setStackInSlot(2, ItemStack.EMPTY);
					i.setStackInSlot(3, ItemStack.EMPTY);
					i.extractItem(0, 1, false);
					i.getStackInSlot(1).hurt(1, RandomSource.create(), null);
				}
			});

			// Player Inventory
			for(int i = 0; i < 3; ++i) {
				for(int j = 0; j < 9; ++j) {
					this.addSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
				}
			}
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(player.getInventory(), k, 8 + k * 18, 142));
			}
        }
	}
	
    public void slotsChanged(Container inventory) {
    	super.slotsChanged(inventory);
    	this.access.execute((level, p2) -> {
            slotsChangedCraft(this, level, this.player, inventory);
    	});
    }
    protected static void slotsChangedCraft(SewingTableContainer menu, Level level, Player player, Container inventory) {
    	if (!level.isClientSide) {
    		//ServerPlayer splayer = (ServerPlayer)player;
    		for (SewingRecipe r : menu.recipes) {
    			if (r.matches(inventory, level)) {
    				ItemStack result = r.getResultItem(level.registryAccess());
    				inventory.setItem(4, result);
    		        //splayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, result));
    			}
    		}
    	}
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

	@Override
	public boolean stillValid(Player player) {
		return AbstractContainerMenu.stillValid(this.access, player, ImprovedBackpacks.SEWING_TABLE.get());
	}
	
	
}
