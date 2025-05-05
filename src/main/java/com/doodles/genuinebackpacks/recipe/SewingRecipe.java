package com.doodles.genuinebackpacks.recipe;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.content.backpack.BackpackItem;

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
	final Ingredient result;

	public SewingRecipe(ResourceLocation id, NonNullList<Ingredient> items, NonNullList<Integer> counts, Ingredient result) {
		this.id = id;
		this.recipeItems = items;
		this.counts = counts;
		this.result = result;
		
		if (items.size() != counts.size()) throw new ArrayStoreException("Ingredient and Count mismatch for recipe "+id.toString());
	}

	public SewingRecipe(ItemStack e, ItemStack output) {
		//easteregg
		this.id = null;
		this.recipeItems = NonNullList.create();
		this.recipeItems.add(Ingredient.of(GenuineBackpacks.items.get("tanned_leather").get()));
		this.recipeItems.add(Ingredient.of(e));
		this.counts = NonNullList.of(0, 5, 3);
		this.result = Ingredient.of(output);
	}

	@Override
	public boolean matches(Container container, Level level) {
		if (!container.getItem(0).is(GenuineBackpacks.items.get("spool").get()) || !container.getItem(1).is(Items.SHEARS)) return false;
		int imatch = 0;
		int cmatch = 0;
		for (int i = 0; i < this.recipeItems.size(); i++) {
			for (int j = 2; j <= 3; j++) {
				if (this.recipeItems.get(i).test(container.getItem(j))) {
					imatch++;
					if (container.getItem(j).getCount() >= this.counts.get(i)) {
						cmatch++;
						break;
					}
				}
			}
		}
		return imatch == cmatch && imatch == this.recipeItems.size();
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess access) {
		ItemStack result = getResultItem(access);
		ItemStack incoming = container.getItem(2).is(GenuineBackpacks.BACKPACK.get()) ? container.getItem(2) : container.getItem(3).is(GenuineBackpacks.BACKPACK.get()) ? container.getItem(3) : null;
		if (incoming != null && result.is(GenuineBackpacks.BACKPACK.get())) {
			BackpackItem rpack = (BackpackItem)result.getItem();
			boolean recolor = rpack.hasCustomColor(result);
			if (incoming.hasCustomHoverName()) result.setHoverName(incoming.getHoverName());
			if (!recolor) rpack.setColor(result, rpack.getColor(incoming));
			BackpackItem.setPockets(result, BackpackItem.TINY, BackpackItem.getPockets(incoming, BackpackItem.TINY) + BackpackItem.getPockets(result, BackpackItem.TINY));
			BackpackItem.setPockets(result, BackpackItem.MEDIUM, BackpackItem.getPockets(incoming, BackpackItem.MEDIUM) + BackpackItem.getPockets(result, BackpackItem.MEDIUM));
			BackpackItem.setPockets(result, BackpackItem.LARGE, BackpackItem.getPockets(incoming, BackpackItem.LARGE) + BackpackItem.getPockets(result, BackpackItem.LARGE));
			BackpackItem.setSpecial(result, BackpackItem.getSpecial(incoming));
			BackpackItem.saveItems(result, BackpackItem.loadItems(incoming));
		}
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() { return this.recipeItems; }
	public NonNullList<Integer> getCounts() { return this.counts; }
	public int size() { return this.recipeItems.size(); }
	
	//public int getCount(int i) { return this.counts.get(i); }

	@Override
	public ResourceLocation getId() { return this.id; }
	@Override
	public RecipeSerializer<?> getSerializer() { return GenuineBackpacks.SEWING_RECIPE_JSON.get(); }
	@Override
	public RecipeType<?> getType() { return GenuineBackpacks.SEWING_RECIPE_TYPE.get(); }
	@Override
	public boolean canCraftInDimensions(int width, int height) { return true; }

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return this.result.getItems()[0].copy();
	}

}
