package com.doodles.genuinebackpacks.data;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.content.backpack.BackpackBlock;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GBBlockStateProvider extends BlockStateProvider {

	public GBBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
		super(output, modid, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		VariantBlockStateBuilder builder = getVariantBuilder(GenuineBackpacks.BACKPACK_BLOCK.get());
		builder.forAllStates(state -> {
			ExistingModelFile backpack = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(GenuineBackpacks.MODID, getBackpackModel(state)));
			return ConfiguredModel.builder()
				.modelFile(backpack)
				.rotationY((int) state.getValue(BackpackBlock.FACING).toYRot())
				.build();
		});
	}
	
	private String getBackpackModel(BlockState state) {
		String type  = state.getValue(BackpackBlock.ENDER) ? "ender_" : "";
		String pack = "backpack";
		if (!state.getValue(BackpackBlock.ENDER)) {
			switch(state.getValue(BackpackBlock.SPECIAL)) {
				case TRANS: pack = "easter_egg"; break;
				case BEE:   pack = "bee"; break;
				default:    break;
			}
		}
		String mount = state.getValue(BackpackBlock.MOUNTED) ? "_mounted" : "";
		return String.format("block/%1$s%2$s%3$s", type, pack, mount);
	}
}
