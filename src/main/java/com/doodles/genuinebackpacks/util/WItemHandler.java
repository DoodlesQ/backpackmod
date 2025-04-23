package com.doodles.genuinebackpacks.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class WItemHandler implements IItemHandlerModifiable {
	private final IItemHandlerModifiable handler;
	
    public WItemHandler(IItemHandlerModifiable handler) { this.handler = handler; }

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
