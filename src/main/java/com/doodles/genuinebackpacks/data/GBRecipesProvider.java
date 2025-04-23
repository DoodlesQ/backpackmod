package com.doodles.genuinebackpacks.data;

import java.util.function.Consumer;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataProvider.Factory;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class GBRecipesProvider extends RecipeProvider {

	public GBRecipesProvider(PackOutput output) { super(output); }

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> writer) {
		//Sewing Table
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GenuineBackpacks.SEWING_TABLE.get())
			.pattern("sl").pattern("ww").pattern("ww")
			.define('s', Items.STICK).define('l', Items.LEATHER).define('w', ItemTags.PLANKS)
			.group("improvedbackpacks")
			.unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.LEATHER).build()))
			.save(writer);
		
		//Sewing Spool
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GenuineBackpacks.SEWING_SPOOL.get())
			.pattern(" s").pattern(" t").pattern(" s")
			.define('s', Tags.Items.RODS_WOODEN).define('t', Tags.Items.STRING)
			.group("improvedbackpacks")
			.unlockedBy("has_string", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.STRING).build()))
			.save(writer);
		
		//Bound Leather
		SewingRecipeBuilder.build("sewing/bound_leather", GenuineBackpacks.SEWING_SPOOL.get())
			.add(Tags.Items.LEATHER, 1)
			.save(writer);
	}
}
