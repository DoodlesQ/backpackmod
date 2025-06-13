package com.doodles.genuinebackpacks.content.backpack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;

public class AbstractBackpackItem extends BlockItem implements Equipable {

	public AbstractBackpackItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		// If not Shift, return to regular use logic
		if (!player.isShiftKeyDown()) return InteractionResult.PASS;
		
		InteractionResult result = super.useOn(context);
		// Eat item even if in creative mode
		if (!context.getLevel().isClientSide && player.isCreative() && result.consumesAction()) player.setItemInHand(context.getHand(), ItemStack.EMPTY);
		
		return result;
	}
	
	public static void open(Level level, Player player, ItemStack pack, MenuProvider containerProvider) {
		if (!level.isClientSide) {
			CompoundTag tag = pack.getOrCreateTagElement("display");
			tag.putBoolean("open", true);
	        NetworkHooks.openScreen((ServerPlayer) player, containerProvider, buf -> buf.writeItemStack(pack, false));
		}
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.CHEST;
	}
	
	public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) { return true; }
	
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (isOpen(oldStack) || isOpen(newStack)) return false;
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	public static boolean wornBy(Player player, ItemStack stack) {
		return ItemStack.isSameItem(player.getItemBySlot(EquipmentSlot.CHEST), stack);
	}

	public static boolean isOpen(ItemStack stack) {
		CompoundTag tag = stack.getTagElement("display");
		return (tag != null && tag.contains("open") && tag.getBoolean("open"));
	}
	
	//Curios
	/*
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag __) {
		return CuriosApi.createCurioProvider(new ICurio() {
			@Override
			public ItemStack getStack() { return stack; }
		});
	}
	*/
}
