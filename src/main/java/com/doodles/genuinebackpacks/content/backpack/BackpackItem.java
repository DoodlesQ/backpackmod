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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraftforge.network.NetworkHooks;

public class BackpackItem extends AbstractBackpackItem implements DyeableLeatherItem {
	
	public static final int TINY = 0;
	public static final int MEDIUM = 1;
	public static final int LARGE = 2;
	public static final int[] POCKET_SIZE = {3, 3 ,2};
	public static final String[] POCKET_NAME = {"tiny", "medium", "large"};
	public static final int TRANS = 1;
	public static final int BEE = 2;
	public static final int SPECIAL = 2;
    
	public BackpackItem(Properties properties) {
		super(GenuineBackpacks.BACKPACK_BLOCK.get(), properties);
	}
	
	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack);
	}
	
	public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
		BackpackItem.setSpecial(stack, 0);
		Component name = stack.getHoverName();
		if (name.getString().equalsIgnoreCase("Beepack")) BackpackItem.setSpecial(stack, BEE);
		if (name.getString().equalsIgnoreCase("Trans Rights")) BackpackItem.setSpecial(stack, TRANS);
		super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		switch (BackpackItem.getSpecial(stack)) {
			case BEE: 	return Rarity.UNCOMMON;
			case TRANS: return Rarity.RARE;
		}
		return super.getRarity(stack);
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
		if (!level.isClientSide) {
			MenuProvider containerProvider = new MenuProvider() {
	            @Override
	            public Component getDisplayName() { return pack.getHoverName(); }
	
	            @Override
	            public AbstractContainerMenu createMenu(int windowId, Inventory __, Player playerEntity) {
	                return new BackpackMenu(windowId, playerEntity, pack, ContainerLevelAccess.create(level, pos));
	            }
	        };
			CompoundTag tag = pack.getOrCreateTagElement("display");
			tag.putBoolean("open", true);
	        NetworkHooks.openScreen((ServerPlayer) player, containerProvider, buf -> buf.writeItemStack(pack, false));
		}
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
		int t = getPockets(stack, TINY);
		int m = getPockets(stack, MEDIUM);
		int l = getPockets(stack, LARGE);
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
	
	public ItemStack getDyed(DyeColor dye) {
		return getDyed(extractColor(dye));
	}
	public ItemStack getDyed(int hexcolor) {
		ItemStack dyed = new ItemStack(this);
		int r = Math.min((hexcolor&0xFF)+0x10, 0xFF);
		int g = Math.min(((hexcolor>>8)&0xFF)+0x10, 0xFF)<<8;
		int b = Math.min(((hexcolor>>16)&0xFF)+0x10, 0xFF)<<16;
		this.setColor(dyed, r+g+b);//Math.min(hexcolor, 0x303030));
		return dyed;
	}
	
	@Override
	public void setColor(ItemStack stack, int color) {
		CompoundTag flag = stack.getOrCreateTag();
		flag.putInt("HideFlags", ItemStack.TooltipPart.DYE.getMask());
	    stack.getOrCreateTagElement("display").putInt("color", color);
	}
	
	@Override
	public int getColor(ItemStack stack) {
		return getColor(stack, true);
	}
	
	public static int getColor(ItemStack stack, boolean verify) {
		CompoundTag tag = stack.getTagElement("display");
		return !verify || (tag != null && tag.contains("color", 99)) ? tag.getInt("color") : 0xd15d4d;
	}
	
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
		return hasPockets(stack, TINY, tag) || hasPockets(stack, MEDIUM, tag) || hasPockets(stack, LARGE, tag);
	}
	public static boolean hasPockets(ItemStack stack, int type) {
		CompoundTag tag = stack.getTagElement("pockets");
		return hasPockets(stack, type, tag);
	}
	private static boolean hasPockets(ItemStack stack, int type, CompoundTag tag) {
		return tag != null && tag.contains(POCKET_NAME[type], 99);
	}
	
	public static int getPockets(ItemStack stack, int type) {
		CompoundTag tag = stack.getTagElement("pockets");
		return hasPockets(stack, type, tag) ? tag.getInt(POCKET_NAME[type]) : 0;
	}
	
	public static void setPockets(ItemStack stack, int type, int n) {
		stack.getOrCreateTagElement("pockets").putInt(POCKET_NAME[type], n);
	}
	public static void addPocket(ItemStack stack, int type) {
		setPockets(stack, type, Math.min(getPockets(stack, type)+1, POCKET_SIZE[type]));
	}
	
	public static void setSpecial(ItemStack stack, int v) {
		stack.getOrCreateTagElement("egg").putInt("type", v);
	}
	public static int getSpecial(ItemStack stack) {
		CompoundTag tag = stack.getTagElement("egg");
		return (tag != null && tag.contains("type")) ? tag.getInt("type") : 0;
	}
	
	
	public static int getTotalSlots(ItemStack stack) {
		int tinyCount = getPockets(stack, TINY);
		int mediCount = getPockets(stack, MEDIUM);
		int largCount = getPockets(stack, LARGE);
		return 18+tinyCount*3+mediCount*6+largCount*9;
	}
	
	public static boolean wornBy(Player player) {
		return player.getItemBySlot(EquipmentSlot.CHEST).is(GenuineBackpacks.BACKPACK.get());
	}

}
