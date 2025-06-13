package com.doodles.genuinebackpacks.content.backpack;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.content.backpack.gui.EnderBackpackMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderBackpackItem extends AbstractBackpackItem {

	public EnderBackpackItem(Properties properties) {
		super(GenuineBackpacks.BACKPACK_BLOCK.get(), properties);
	}
	
	@Override
	public Component getName(ItemStack stack) {
		return GenuineBackpacks.ct("item.%s.ender_backpack");
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
    	ItemStack i = player.getItemInHand(usedHand);
		if (usedHand == InteractionHand.MAIN_HAND) {
			if (!player.isShiftKeyDown()) open(level, player, i);
		}
		return InteractionResultHolder.pass(i);
	}
	
	public static void open(Level level, Player player, ItemStack pack) {
		open(level, player, pack, player.blockPosition());
	}

	public static void open(Level level, Player player, ItemStack pack, BlockPos pos) {
		PlayerEnderChestContainer enderchest = player.getEnderChestInventory();
		if (enderchest != null) {
			MenuProvider containerProvider = new MenuProvider() {
	            @Override
	            public Component getDisplayName() { return Component.translatable("container.genuinebackpacks.ender_backpack"); }
	
	            @Override
	            public AbstractContainerMenu createMenu(int id, Inventory inv, Player playerEntity) {
	                return new EnderBackpackMenu(id, playerEntity, pack, enderchest, ContainerLevelAccess.create(level, pos));
	            }
	        };
	        AbstractBackpackItem.open(level, player, pack, containerProvider);
		}
	}
}
