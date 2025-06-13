package com.doodles.genuinebackpacks.content.backpack;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BackpackTileEntity extends BlockEntity {
 	
	public ItemStack backpack;
	private boolean ender;
	private BackpackItem.Special egg;

	public BackpackTileEntity(BlockPos pos, BlockState state) {
		super(GenuineBackpacks.BACKPACK_BLOCK_ENTITY.get(), pos, state);
		this.ender = state.getValue(BackpackBlock.ENDER);
		this.egg = state.getValue(BackpackBlock.SPECIAL);
	}
	
	public void setBackpack(ItemStack backpack) {
		ItemStack truepack = BackpackBlock.getDefaultPack(this.ender, this.egg);
		this.backpack = (backpack != null && ItemStack.isSameItem(backpack, truepack)) ? backpack : truepack;
		setChanged();
	}
	
	private void saveBackpack(CompoundTag tag) {
        if (this.backpack != null) {
        	CompoundTag packTag = this.backpack.getTag();
        	tag.put("backpack", packTag != null ? packTag : new CompoundTag());
        }
        tag.putBoolean("ender", this.ender);
        tag.putString("egg", this.egg.name());
	}
	private void loadBackpack(CompoundTag tag) {
        if (tag.contains("ender")) this.ender = tag.getBoolean("ender");
        if (tag.contains("egg")) this.egg = BackpackItem.Special.valueOf(tag.getString("egg"));
    	ItemStack backpack = BackpackBlock.getDefaultPack(this.ender, this.egg);
        if (tag.contains("backpack")) backpack.setTag(tag.getCompound("backpack"));
    	setBackpack(backpack);
	}
	
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveBackpack(tag);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadBackpack(tag);
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveBackpack(tag);
        return tag;
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) loadBackpack(tag);
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
}
