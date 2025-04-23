package com.doodles.genuinebackpacks.data;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.content.SewingTableBlock;

import net.minecraft.data.DataProvider;
import net.minecraft.data.DataProvider.Factory;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class GBBlockStatesProvider extends BlockStateProvider {

	public GBBlockStatesProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
		super(output, modid, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		registerSewingTable();
	}
	
	private void registerSewingTable() {
		RegistryObject<SewingTableBlock> table = GenuineBackpacks.SEWING_TABLE;
		String path = "sewing_table";
		
		BlockModelBuilder base = models().getBuilder("block/"+path);
		
	}
}
