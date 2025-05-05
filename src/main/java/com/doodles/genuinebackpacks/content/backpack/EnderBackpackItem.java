package com.doodles.genuinebackpacks.content.backpack;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

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
		if (!level.isClientSide) {
			PlayerEnderChestContainer enderchest = player.getEnderChestInventory();
			if (enderchest != null) {
				MenuProvider containerProvider = new MenuProvider() {
		            @Override
		            public Component getDisplayName() { return Component.translatable("container.genuinebackpacks.ender_backpack"); }
		
		            @Override
		            public AbstractContainerMenu createMenu(int id, Inventory inv, Player playerEntity) {
		                return new EnderBackpackMenu(id, inv, pack, enderchest, ContainerLevelAccess.create(level, pos));
		            }
		        };
				CompoundTag tag = pack.getOrCreateTagElement("display");
				tag.putBoolean("open", true);
		        NetworkHooks.openScreen((ServerPlayer) player, containerProvider);
			}
		}
	}

	public static boolean wornBy(Player player) {
		return player.getItemBySlot(EquipmentSlot.CHEST).is(GenuineBackpacks.ENDER_BACKPACK.get());
	}
}
