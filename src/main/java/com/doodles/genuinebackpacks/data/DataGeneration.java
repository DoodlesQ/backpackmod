package com.doodles.genuinebackpacks.data;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@EventBusSubscriber(modid = GenuineBackpacks.MODID, bus = MOD)
public class DataGeneration {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        
        //generator.addProvider(event.includeClient(), new IBBlockStatesProvider(output, ImprovedBackpacks.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new GBBlockTagsProvider(output, event.getLookupProvider(), GenuineBackpacks.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new GBRecipesProvider(output));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(),
            List.of(new LootTableProvider.SubProviderEntry(GBLootTableProvider::new, LootContextParamSets.BLOCK)))
		);
	}
}
