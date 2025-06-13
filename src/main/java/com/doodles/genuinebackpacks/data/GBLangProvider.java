package com.doodles.genuinebackpacks.data;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class GBLangProvider extends LanguageProvider {

	public GBLangProvider(PackOutput output, String modid, String locale) {
		super(output, modid, locale);
	}
	
    public static String rl (String s) { return String.format(s, GenuineBackpacks.MODID); }

	@Override
	protected void addTranslations() {
		add(rl("itemGroup.%s.backpack_tab"), "Genuine Backpacks");
		add(GenuineBackpacks.SEWING_TABLE.get(), "Sewing Table");
		add(rl("container.%s.sewing_table"), "Sewing Table");
		add(GenuineBackpacks.items.get("spool").get(), "Spool of Thread");
		add(GenuineBackpacks.items.get("bound_leather").get(), "Bound Leather");
		add(GenuineBackpacks.items.get("tanned_leather").get(), "Tanned Leather");
		add(GenuineBackpacks.items.get("tiny_pocket").get(), "Small Pocket");
		add(GenuineBackpacks.items.get("medium_pocket").get(), "Medium Pocket");
		add(GenuineBackpacks.items.get("large_pocket").get(), "Large Pocket");
		//add(GenuineBackpacks.BACKPACK.get(), "Backpack");
		add(GenuineBackpacks.BACKPACK_BLOCK.get(), "Backpack");
		add(rl("container.%s.rename"), "Rename Backpack");
		add(rl("gui.%s.rename"), "Rename");
		add(rl("gui.%s.backpack.filled"),	"Filled Slots: %1$d/%2$d");
		add(rl("gui.%s.backpack.items"), "Items: %1$d/%2$d");
		add(rl("gui.%s.backpack.tiny"), "Small Pockets: %1$d/3");
		add(rl("gui.%s.backpack.medium"), "Medium Pockets: %1$d/3");
		add(rl("gui.%s.backpack.large"), "Large Pockets: %1$d/2");
		add(rl("gui.%s.pockets"), "Use a Sewing Table to attach to a backpack");
		add(rl("gui.%s.pockets.increase"), "Adds slots to backpack: %1$d");
		add(rl("gui.%s.pockets.capacity"), "Max count on backpack: %1$d");
		add(rl("key.%s.backpack"), "Open Backpack");
		add(rl("item.%s.ender_backpack"), "Ender Backpack");
		add(rl("container.%s.ender_backpack"), "Ender Backpack");
		add(rl("item.%s.bee"), "Beepack");
		add(rl("subtitles.%s.block.sewing_table"), "Sewing Table used");
		add(rl("subtitles.%s.item.backpack_open"), "Backpack opened");
		add(rl("subtitles.%s.item.backpack_close"), "Backpack closed");
		add(rl("subtitles.%s.item.ender_backpack_open"), "Ender Backpack opened");
		add(rl("subtitles.%s.item.ender_backpack_close"), "Ender Backpack closed");
	}
}
