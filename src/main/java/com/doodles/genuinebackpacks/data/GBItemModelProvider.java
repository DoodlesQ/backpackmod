package com.doodles.genuinebackpacks.data;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class GBItemModelProvider extends ItemModelProvider {

	public GBItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
		super(output, modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (RegistryObject<Item> i : GenuineBackpacks.items.values()) basicItem(i.get());
		/*
		for (RegistryObject<BackpackItem> i : GenuineBackpacks.backpacks.values())
			getBuilder(i.getId().toString()).parent(new ModelFile.UncheckedModelFile("genuinebackpacks:item/backpack"));
		*/
	}
}
