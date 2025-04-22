package com.doodles.improvedbackpacks.content;

import com.doodles.improvedbackpacks.ImprovedBackpacks;
import com.doodles.improvedbackpacks.util.WItemHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class SewingTableEntity extends BlockEntity {
	public static final String ITEMS_TAG = "Inventory";
	public static final String SPOOL_TAG = "Spool";
	public static final String SHEARS_TAG = "Shears";

	private static Boolean spool = false;
	private static Boolean shears = false;
	
    // Item Storage
    private final ItemStackHandler inputs = createItemHandler(4);
    private final ItemStackHandler result = createItemHandler(1);
    // Capability for Item Storage
    /*
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new WItemHandler(items) {
    	// Disable item automation
    	@Override
        public ItemStack insertItem(int _0, ItemStack stack, boolean _1) { return stack; }
        @Override
        public ItemStack extractItem(int _0, int _1, boolean _2) { return ItemStack.EMPTY; }
    });
    */
    //*
    private final LazyOptional<IItemHandler> inputItemHandler = LazyOptional.of(() -> new WItemHandler(inputs) {
    	// Disable item automation
    	@Override
        public ItemStack insertItem(int _0, ItemStack stack, boolean _1) { return stack; }
        @Override
        public ItemStack extractItem(int _0, int _1, boolean _2) { return ItemStack.EMPTY; }
    });
    private final LazyOptional<IItemHandler> resultItemHandler = LazyOptional.of(() -> new WItemHandler(result) {
    	// Disable item automation
    	@Override
        public ItemStack insertItem(int _0, ItemStack stack, boolean _1) { return stack; }
        @Override
        public ItemStack extractItem(int _0, int _1, boolean _2) { return ItemStack.EMPTY; }
    });
    //*/
    
    // Constructor
	public SewingTableEntity(BlockPos pos, BlockState blockState) {
		super(ImprovedBackpacks.SEWING_TABLE_ENTITY.get(), pos, blockState);
	}

    // Define ability for other mods to access Item Capability
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inputItemHandler.cast();
        } else {
            return super.getCapability(cap, dir);
        }
    }
    
	// Destroy Capability when block is destroyed
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        //itemHandler.invalidate();
        inputItemHandler.invalidate();
        resultItemHandler.invalidate();
    }

    // Item Storage Handler
    private ItemStackHandler createItemHandler(int slots) {
        return new ItemStackHandler(slots) {
        	// Define behavior for when item storage changes; update world at block position
            @Override
            protected void onContentsChanged(int __) {
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
    }
    public ItemStackHandler getItems() {
        ItemStackHandler items = createItemHandler(5);
    	for (int i = 0; i < 4; i++) {
    		items.setStackInSlot(i, inputs.getStackInSlot(i));
    	}
    	items.setStackInSlot(4, result.getStackInSlot(0));
    	return items;
    }
    public void setItems(ItemStackHandler items) {
    	for (int i = 0; i < 4; i++) {
    		inputs.setStackInSlot(i, items.getStackInSlot(i));
    	}
    	result.setStackInSlot(0, items.getStackInSlot(4));
    }
    public ItemStackHandler getInput() { return inputs; }
    public ItemStackHandler getResult() { return result; }
    
    // Save & Load Inventory Data
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ItemStackHandler items = this.getItems();
        tag.put(ITEMS_TAG, items.serializeNBT());
        tag.putBoolean(SPOOL_TAG, spool);
        tag.putBoolean(SHEARS_TAG, shears);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ITEMS_TAG)) {
            ItemStackHandler items = createItemHandler(5);
        	items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        	this.setItems(items);
        }
        if (tag.contains(SPOOL_TAG)) { spool = tag.getBoolean(SPOOL_TAG); }
        if (tag.contains(SHEARS_TAG)) { shears = tag.getBoolean(SHEARS_TAG); }
    }

	public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
		SewingTableEntity table = (SewingTableEntity) blockEntity;
		
		Boolean spoolOld = state.getValue(SewingTableBlock.SPOOL);
		spool = Boolean.valueOf(table.getItems().getStackInSlot(0).is(ImprovedBackpacks.SEWING_SPOOL.get()));
		Boolean shearsOld = state.getValue(SewingTableBlock.SHEARS);
		shears = Boolean.valueOf(table.getItems().getStackInSlot(1).is(Items.SHEARS));
		
		state = state.setValue(SewingTableBlock.SPOOL, spool).setValue(SewingTableBlock.SHEARS, shears);

		if (spool != spoolOld || shears != shearsOld) {
	        level.setBlock(pos, state, 3);
			setChanged(level, pos, state);
		}
	}
}