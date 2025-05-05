package com.doodles.genuinebackpacks.data;

import java.util.Map;
import java.util.stream.Collectors;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class GBLootTableProvider extends VanillaBlockLoot {
    @Override
    protected void generate() {
        dropSelf(GenuineBackpacks.SEWING_TABLE.get());
        //createDropTable(ImprovedBackpacks.SEWING_TABLE.get(), ImprovedBackpacks.SEWING_TABLE_ENTITY.get());
    }
    /*
    private void createDropTable(Block block, BlockEntityType<?> type) {
        LootPool.Builder builder = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1)) //Always drop internals
            .add(LootItem.lootTableItem(block)  //Drop the block itself
        		//.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)) //With the name of the tileentity
        		.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY) //With the NBT of the tileentity
    				.copy(SewingTableEntity.ITEMS_TAG, "BlockEntityTag." + SewingTableEntity.ITEMS_TAG, CopyNbtFunction.MergeStrategy.REPLACE)) //And the inventory data
        		.apply(SetContainerContents.setContents(type)
    				.withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents")))) //And drop the contents as dynamic loot
    		);
        
        add(block, LootTable.lootTable().withPool(builder));
    }
    */

    //This code is taken with zero changes from https://www.mcjty.eu/docs/1.20/ep2#data-generation
    //because apparently this is required despite the documentation not mentioning it even a little bit
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getEntries().stream()
            .filter(e -> e.getKey().location().getNamespace().equals(GenuineBackpacks.MODID))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList()
		);
    }
}
