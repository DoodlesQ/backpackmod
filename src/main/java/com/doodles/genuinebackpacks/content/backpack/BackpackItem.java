package com.doodles.genuinebackpacks.content.backpack;

import java.util.List;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.content.backpack.gui.BackpackMenu;
import com.doodles.genuinebackpacks.content.backpack.gui.BackpackRenameScreen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackItem extends AbstractBackpackItem implements DyeableLeatherItem {
	
	public enum PocketType {
		SMALL(3),
		MEDIUM(3),
		LARGE(2);
		private final int max;
		private PocketType(int max) { this.max = max; }
		public int getMax() { return this.max; }
	}
	public enum Special implements StringRepresentable {
		NONE("none"),
		TRANS("trans"),
		BEE("bee");
		private final String name;
		private Special(String name) { this.name = name; }
		@Override
		public String getSerializedName() { return this.name; }
	}
    
	public BackpackItem(Properties properties) {
		super(GenuineBackpacks.BACKPACK_BLOCK.get(), properties);
	}
	
	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack);
	}
	
	public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
		BackpackItem.setSpecial(stack, Special.NONE);
		Component name = stack.getHoverName();
		if (name.getString().equalsIgnoreCase("Beepack")) BackpackItem.setSpecial(stack, Special.BEE);
		if (name.getString().equalsIgnoreCase("Trans Rights")) BackpackItem.setSpecial(stack, Special.TRANS);
		super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		switch (BackpackItem.getSpecial(stack)) {
			case BEE: 	return Rarity.UNCOMMON;
			case TRANS: return Rarity.RARE;
			default:	return super.getRarity(stack);
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
    	ItemStack i = player.getItemInHand(usedHand);
		if (usedHand == InteractionHand.MAIN_HAND) {
			if (player.isShiftKeyDown()) {
				if (level.isClientSide) Minecraft.getInstance().setScreen(new BackpackRenameScreen(i, player, usedHand));
			} else open(level, player, i);
		}
		return InteractionResultHolder.pass(i);
	}
	
	public static void open(Level level, Player player, ItemStack pack) {
		open(level, player, pack, player.blockPosition());
	}
	public static void open(Level level, Player player, ItemStack pack, BlockPos pos) {
		MenuProvider containerProvider = new MenuProvider() {
            @Override
            public Component getDisplayName() { return pack.getHoverName(); }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory __, Player playerEntity) {
                return new BackpackMenu(windowId, playerEntity, pack, ContainerLevelAccess.create(level, pos));
            }
        };
		AbstractBackpackItem.open(level, player, pack, containerProvider);
	}
	
	public static void saveItems (ItemStack stack, ItemStackHandler handler) {
		CompoundTag tag = stack.getOrCreateTagElement("inventory");
		tag.put("items", handler.serializeNBT());
	}
	public static ItemStackHandler loadItems (ItemStack stack) {
		CompoundTag tag = stack.getTagElement("inventory");
		ItemStackHandler out = new ItemStackHandler(63);
		if (tag != null && tag.contains("items")) out.deserializeNBT(tag.getCompound("items"));
		return out;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		int t = getPockets(stack, PocketType.SMALL);
		int m = getPockets(stack, PocketType.MEDIUM);
		int l = getPockets(stack, PocketType.LARGE);
		int slots = getTotalSlots(stack);
		int[] data = getFilled(loadItems(stack));
		CompoundTag tag = stack.getTagElement("display");
		if (tag == null || !tag.getBoolean("open")) {
			tooltip.add(GenuineBackpacks.ct("gui.%s.backpack.filled", data[0], slots).withStyle(ChatFormatting.GRAY));
			if (flag.isAdvanced()) tooltip.add(GenuineBackpacks.ct("gui.%s.backpack.items", data[1], slots*64).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.empty());
			tooltip.add(GenuineBackpacks.ct("gui.%s.backpack.tiny", t).withStyle(ChatFormatting.GRAY));
			tooltip.add(GenuineBackpacks.ct("gui.%s.backpack.medium", m).withStyle(ChatFormatting.GRAY));
			tooltip.add(GenuineBackpacks.ct("gui.%s.backpack.large", l).withStyle(ChatFormatting.GRAY));
		}
	}
	
	public static int[] getFilled(ItemStackHandler inv) {
		int[] data = {0, 0};
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack s = inv.getStackInSlot(i);
			if (!s.isEmpty()) {
				data[0]++;
				data[1] += s.getCount();
			}
		}
		return data;
	}
	
	@Override
	public void setColor(ItemStack stack, int color) {
		setDye(stack, color);
	}
	public static void setDye(ItemStack stack, int color) {
		CompoundTag flag = stack.getOrCreateTag();
		flag.putInt("HideFlags", ItemStack.TooltipPart.DYE.getMask());
	    stack.getOrCreateTagElement("display").putInt("color", color);
	}

	@Override
	public int getColor(ItemStack stack) {
		return getDye(stack);
	}
	public static int getDye(ItemStack stack) {
		CompoundTag tag = stack.getTagElement("display");
		return (tag != null && tag.contains("color", 99)) ? tag.getInt("color") : 0xd15d4d;
	}
	
	// Convert a DyeColor into a integer color value
	public static int extractColor(DyeColor color) {
		float[] tdc = color.getTextureDiffuseColors();
		int r = (int) (tdc[0] * 255.0f);
		int g = (int) (tdc[1] * 255.0f);
		int b = (int) (tdc[2] * 255.0f);
		int rg = (r << 8) + g;
		return (rg << 8) + b;
	}
	
	public static boolean hasPockets(ItemStack stack) {
		CompoundTag tag = stack.getTagElement("pockets");
		return hasPockets(tag, PocketType.SMALL) || hasPockets(tag, PocketType.MEDIUM) || hasPockets(tag, PocketType.LARGE);
	}
	public static boolean hasPockets(ItemStack stack, PocketType type) {
		CompoundTag tag = stack.getTagElement("pockets");
		return hasPockets(tag, type);
	}
	private static boolean hasPockets(CompoundTag tag, PocketType type) {
		return tag != null && tag.contains(type.name(), 99);
	}
	
	public static int getPockets(ItemStack stack, PocketType type) {
		CompoundTag tag = stack.getTagElement("pockets");
		return hasPockets(tag, type) ? tag.getInt(type.name()) : 0;
	}
	
	public static void setPockets(ItemStack stack, PocketType type, int n) {
		stack.getOrCreateTagElement("pockets").putInt(type.name(), n);
	}
	public static void addPocket(ItemStack stack, PocketType type) {
		setPockets(stack, type, Math.min(getPockets(stack, type)+1, type.getMax()));
	}
	
	public static void setSpecial(ItemStack stack, Special v) {
		stack.getOrCreateTagElement("egg").putString("type", v.name());
	}
	public static Special getSpecial(ItemStack stack) {
		CompoundTag tag = stack.getTagElement("egg");
		return (tag != null && tag.contains("type")) ? Special.valueOf(tag.getString("type")) : Special.NONE;
	}
	
	public static int getTotalSlots(ItemStack stack) {
		int tinyCount = getPockets(stack, PocketType.SMALL);
		int mediCount = getPockets(stack, PocketType.SMALL);
		int largCount = getPockets(stack, PocketType.SMALL);
		return 18+tinyCount*3+mediCount*6+largCount*9;
	}
}
