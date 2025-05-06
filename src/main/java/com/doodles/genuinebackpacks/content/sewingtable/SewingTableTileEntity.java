package com.doodles.genuinebackpacks.content.sewingtable;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class SewingTableTileEntity extends BlockEntity {
	public static final String ITEMS_TAG = "Inventory";
	public static final String SPOOL_TAG = "Spool";
	public static final String SHEARS_TAG = "Shears";

	private boolean spool = false;
	private boolean shears = false;
	
    // Item Storage
    private final ItemStackHandler items = createItemHandler(5);
    // Capability for Item Storage
    ///*
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new GBItemHandler(items) {
    	// Disable item automation
    	@Override
        public ItemStack insertItem(int _0, ItemStack stack, boolean _1) { return stack; }
        @Override
        public ItemStack extractItem(int _0, int _1, boolean _2) { return ItemStack.EMPTY; }
    });
    
    // Constructor
	public SewingTableTileEntity(BlockPos pos, BlockState blockState) {
		super(GenuineBackpacks.SEWING_TABLE_ENTITY.get(), pos, blockState);
	}

    // Define ability for other mods to access Item Capability
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir) {
    	//return super.getCapability(cap, dir);
    	///*
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        } else {
            return super.getCapability(cap, dir);
        }
        //*/
    }
    
	// Destroy Capability when block is destroyed
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
        //inputItemHandler.invalidate();
        //resultItemHandler.invalidate();
    }

    // Item Storage Handler
    private ItemStackHandler createItemHandler(int slots) {
        return new ItemStackHandler(slots) {
        	// Define behavior for when item storage changes; update world at block position
            @Override
            protected void onContentsChanged(int slot) {
            	super.onContentsChanged(slot);
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
    }
    
    public ItemStackHandler getItems() {
    	return items;
    }
    
    public void setSpool(boolean b) {
    	this.spool = b;
	}
    public void setShears(boolean b) {
    	this.shears = b;
	}
    
    public static void updateBlockState(Level level, BlockPos pos, BlockState state, SewingTableTileEntity table) {
    	if (level.isClientSide) {
			boolean spoolOld = state.getValue(SewingTableBlock.SPOOL);
			boolean shearsOld = state.getValue(SewingTableBlock.SHEARS);
			
			state = state.setValue(SewingTableBlock.SPOOL, table.spool).setValue(SewingTableBlock.SHEARS, table.shears);
	
			if (table.spool != spoolOld || table.shears != shearsOld) {
		        level.setBlock(pos, state, 3);
			}
    	}
    }

	public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
		SewingTableTileEntity table = (SewingTableTileEntity) blockEntity;
		SewingTableTileEntity.updateBlockState(level, pos, state, table);
	}

    // Save & Load Inventory Data
    private void saveInv(CompoundTag tag) {
        tag.put(ITEMS_TAG, items.serializeNBT());
        tag.putBoolean(SPOOL_TAG, this.spool);
        tag.putBoolean(SHEARS_TAG, this.shears);
    }
    private void loadInv(CompoundTag tag) {
        if (tag.contains(ITEMS_TAG)) {
        	items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        }
        if (tag.contains(SPOOL_TAG)) { this.spool = tag.getBoolean(SPOOL_TAG); }
        if (tag.contains(SHEARS_TAG)) { this.shears = tag.getBoolean(SHEARS_TAG); }
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveInv(tag);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadInv(tag);
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveInv(tag);
        return tag;
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) loadInv(tag);
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null)  handleUpdateTag(tag);
    }
	
	private class GBItemHandler implements IItemHandlerModifiable {
		private final IItemHandlerModifiable handler;
	    public GBItemHandler(IItemHandlerModifiable handler) { this.handler = handler; }
	    @Override
	    public int getSlots() { return handler.getSlots(); }
	    @Override
	    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return handler.insertItem(slot, stack, simulate); }
	    @Override
	    public ItemStack extractItem(int slot, int amount, boolean simulate) { return handler.extractItem(slot, amount, simulate); }
	    @Override
	    public int getSlotLimit(int slot) { return handler.getSlotLimit(slot); }
	    @Override
	    public boolean isItemValid(int slot, ItemStack stack) { return handler.isItemValid(slot, stack); }
		@Override
		public ItemStack getStackInSlot(int slot) { return handler.getStackInSlot(slot); }
	    @Override
	    public void setStackInSlot(int slot, ItemStack stack) { handler.setStackInSlot(slot, stack); }
	}

}