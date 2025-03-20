package com.doodles.improvedbackpacks.blockentities;

import com.doodles.improvedbackpacks.ImprovedBackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SewingTableEntity extends BlockEntity {
   public SewingTableEntity(BlockPos pos, BlockState blockState) {
	      super(ImprovedBackpacks.SEWING_TABLE_ENTITY.get(), pos, blockState);
   }
}