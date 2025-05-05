package com.doodles.genuinebackpacks.data;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = GenuineBackpacks.MODID, bus = MOD)
public class DataGeneration {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper efh = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new GBBlockTagsProvider(output, event.getLookupProvider(), GenuineBackpacks.MODID, efh));
        generator.addProvider(event.includeServer(), new GBLangProvider(output, GenuineBackpacks.MODID, "en_us"));
        generator.addProvider(event.includeServer(), new GBItemModelProvider(output, GenuineBackpacks.MODID, efh));
        generator.addProvider(event.includeServer(), new GBBlockStateProvider(output, GenuineBackpacks.MODID, efh));
        /*
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(),
            List.of(new LootTableProvider.SubProviderEntry(GBLootTableProvider::new, LootContextParamSets.BLOCK)))
		);
		*/
        generator.addProvider(event.includeServer(), new GBRecipesProvider(output));
	}
}
