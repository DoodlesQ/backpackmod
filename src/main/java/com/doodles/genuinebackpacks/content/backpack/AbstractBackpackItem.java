package com.doodles.genuinebackpacks.content.backpack;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

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
	
	public static void open(Level level, Player player, ItemStack pack) {
		GenuineBackpacks.LOGGER.info("FAILURE");
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.CHEST;
	}

	public static boolean wornBy(Player player, ItemStack test) {
		return ItemStack.isSameItem(player.getItemBySlot(EquipmentSlot.CHEST), test);
	}
}
