package com.doodles.genuinebackpacks.data;

import java.util.function.Consumer;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.content.backpack.BackpackItem;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

public class GBRecipesProvider extends RecipeProvider {

	public GBRecipesProvider(PackOutput output) { super(output); }

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> writer) {
		//Sewing Table
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GenuineBackpacks.SEWING_TABLE.get())
			.pattern("sl").pattern("ww").pattern("ww")
			.define('s', Items.STICK).define('l', Items.LEATHER).define('w', ItemTags.PLANKS)
			.group("sewing_table")
			.unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.LEATHER).build()))
			.save(writer);
		
		//Sewing Spool
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GenuineBackpacks.items.get("spool").get())
			.pattern(" s").pattern(" t").pattern(" s")
			.define('t', Tags.Items.RODS_WOODEN).define('s', Tags.Items.STRING)
			.group("spool_of_thread")
			.unlockedBy("has_string", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.STRING).build()))
			.save(writer);
		
		//Bound Leather
		SewingRecipeBuilder.build("bound_leather", GenuineBackpacks.items.get("bound_leather").get())
			.add(Items.LEATHER, 1)
			.save(writer);
		
		SimpleCookingRecipeBuilder.smelting(
				Ingredient.of(GenuineBackpacks.items.get("bound_leather").get()), RecipeCategory.MISC,
				GenuineBackpacks.items.get("tanned_leather").get(), 0.1f, 200)
			.unlockedBy("has_bound_leather", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(GenuineBackpacks.items.get("bound_leather").get()).build()))
			.save(writer);
		
		//Backpacks
		for (DyeItem dye : GenuineBackpacks.WOOL.keySet()) {
			BackpackItem b = GenuineBackpacks.BACKPACK.get();
			SewingRecipeBuilder.build(dye.getDyeColor().toString()+"_backpack", b)
				.add(GenuineBackpacks.items.get("tanned_leather").get(), 5)
				.add(GenuineBackpacks.WOOL.get(dye), 3)
				.setDye(BackpackItem.extractColor(dye.getDyeColor()))
				.save(writer);
		}
		for (DyeItem dye : GenuineBackpacks.WOOL.keySet()) {
			BackpackItem b = GenuineBackpacks.BACKPACK.get();
			SewingRecipeBuilder.build(dye.getDyeColor().toString()+"_backpack_restitch", b)
				.add(b, 1)
				.add(GenuineBackpacks.WOOL.get(dye), 3)
				.setDye(BackpackItem.extractColor(dye.getDyeColor()))
				.save(writer);
		}
		
		SewingRecipeBuilder.build("tiny_pocket", GenuineBackpacks.items.get("tiny_pocket").get())
			.add(GenuineBackpacks.items.get("tanned_leather").get(), 1)
			.add(Tags.Items.INGOTS_IRON, 1)
			.save(writer);
		SewingRecipeBuilder.build("medium_pocket", GenuineBackpacks.items.get("medium_pocket").get())
			.add(GenuineBackpacks.items.get("tanned_leather").get(), 2)
			.add(Tags.Items.INGOTS_GOLD, 1)
			.save(writer);
		SewingRecipeBuilder.build("large_pocket", GenuineBackpacks.items.get("large_pocket").get())
			.add(GenuineBackpacks.items.get("tanned_leather").get(), 3)
			.add(Tags.Items.GEMS_DIAMOND, 1)
			.save(writer);
		
		SewingRecipeBuilder.build("backpack_tiny_pocket", GenuineBackpacks.BACKPACK.get())
			.add(GenuineBackpacks.BACKPACK.get(), 1)
			.add(GenuineBackpacks.items.get("tiny_pocket").get(), 1)
			.setPockets(0)
			.save(writer);
		SewingRecipeBuilder.build("backpack_medium_pocket", GenuineBackpacks.BACKPACK.get())
			.add(GenuineBackpacks.BACKPACK.get(), 1)
			.add(GenuineBackpacks.items.get("medium_pocket").get(), 1)
			.setPockets(1)
			.save(writer);
		SewingRecipeBuilder.build("backpack_large_pocket", GenuineBackpacks.BACKPACK.get())
			.add(GenuineBackpacks.BACKPACK.get(), 1)
			.add(GenuineBackpacks.items.get("large_pocket").get(), 1)
			.setPockets(2)
			.save(writer);
		
		//Ender Backpack
		SewingRecipeBuilder.build("ender_backpack", GenuineBackpacks.ENDER_BACKPACK.get())
			.add(GenuineBackpacks.items.get("tanned_leather").get(), 5)
			.add(Items.ENDER_CHEST, 3)
			.save(writer);
	}
	
}
