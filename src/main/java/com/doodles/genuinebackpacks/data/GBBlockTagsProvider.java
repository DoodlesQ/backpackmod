package com.doodles.genuinebackpacks.data;

import java.util.concurrent.CompletableFuture;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GBBlockTagsProvider extends BlockTagsProvider {

	public GBBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		tag(BlockTags.MINEABLE_WITH_AXE).add(GenuineBackpacks.SEWING_TABLE.get());
	}
}
