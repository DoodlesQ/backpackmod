package com.doodles.improvedbackpacks.recipe;

import com.doodles.improvedbackpacks.ImprovedBackpacks;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SewingRecipe implements Recipe<Container> {

	private final ResourceLocation id;
	final NonNullList<Ingredient> recipeItems;
	final NonNullList<Integer> counts;
	final ItemStack result;

	public SewingRecipe(ResourceLocation id, NonNullList<Ingredient> items, NonNullList<Integer> counts, ItemStack result) {
		this.id = id;
		this.recipeItems = items;
		this.counts = counts;
		this.result = result;
	}
	
	@Override
	public boolean matches(Container container, Level level) {
		if (!container.getItem(0).is(ImprovedBackpacks.SEWING_SPOOL.get()) || !container.getItem(1).is(Items.SHEARS)) return false;
		for (int i = 0; i <= 1; i++) {
			int j = i == 0 ? 1 : 0;
			boolean slot0 = container.getItem(2).is(this.recipeItems.get(i).getItems()[0].getItem()) && container.getItem(2).getCount() >= this.counts.get(i);
			boolean slot1 = container.getItem(3).is(this.recipeItems.get(j).getItems()[0].getItem()) && container.getItem(3).getCount() >= this.counts.get(j);
			if (slot0 && slot1) return true;
		}
		return false;
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess registryAccess) {
		return this.result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ResourceLocation getId() { return this.id; }
	@Override
	public NonNullList<Ingredient> getIngredients() { return this.recipeItems; }
	public NonNullList<Integer> getCounts() { return this.counts; }
	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) { return this.result; }
	@Override
	public RecipeSerializer<?> getSerializer() { return ImprovedBackpacks.SEWING_RECIPE_JSON.get(); }
	@Override
	public RecipeType<?> getType() { return ImprovedBackpacks.SEWING_RECIPE_TYPE.get(); }

}
