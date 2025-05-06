package com.doodles.genuinebackpacks.data;

import java.util.function.Consumer;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SewingRecipeBuilder implements FinishedRecipe {

	private final ResourceLocation id;
	NonNullList<Ingredient> recipeItems;
	NonNullList<Integer> counts;
	final ItemStack result;
	int packColor;
	boolean[] pockets;

	public SewingRecipeBuilder(ResourceLocation id, NonNullList<Ingredient> items, NonNullList<Integer> counts, ItemStack result, int packColor, boolean[] pockets) {
		this.id = id;
		this.recipeItems = items;
		this.counts = counts;
		this.result = result;
		this.packColor = packColor;
		this.pockets = pockets;
	}
	public static SewingRecipeBuilder build(String id, ItemStack r) {
		return new SewingRecipeBuilder(new ResourceLocation(GenuineBackpacks.MODID, id), NonNullList.create(), NonNullList.create(), r, -1, new boolean[]{false, false, false});
	}
	
	public static SewingRecipeBuilder build(String id, ItemLike r) {
		return build(id, new ItemStack(r));
	}
	public SewingRecipeBuilder add(TagKey<Item> t, int c) {
		return this.add(Ingredient.of(t), c);
	}
	public SewingRecipeBuilder add(ItemLike i, int c) {
		return this.add(Ingredient.of(i), c);
	}
	public SewingRecipeBuilder add(Ingredient i, int c) {
		this.recipeItems.add(i);
		this.counts.add(c);
		return this;
	}
	public SewingRecipeBuilder setDye(int color) {
		this.packColor = color;
		return this;
	}
	public SewingRecipeBuilder setPockets(int i) {
		this.pockets[i] = !this.pockets[i];
		return this;
	}
	
	public void save(Consumer<FinishedRecipe> writer) {
		writer.accept(new SewingRecipeBuilder(this.id, this.recipeItems, this.counts, this.result, this.packColor, this.pockets));
	}
	
	@Override
	public void serializeRecipeData(JsonObject json) {
		JsonArray items = new JsonArray();
		items.add(this.recipeItems.get(0).toJson());
		if (this.recipeItems.size() > 1)
			items.add(this.recipeItems.get(1).toJson());
		json.add("items", items);
		JsonArray counts = new JsonArray();
		counts.add(this.counts.get(0));
		if (this.recipeItems.size() > 1)
			counts.add(this.counts.get(1));
		json.add("counts", counts);
		json.add("result", Ingredient.of(this.result).toJson());
		if (this.packColor >= 0) json.addProperty("packColor", this.packColor);
		boolean t = this.pockets[0];
		boolean m = this.pockets[1];
		boolean l = this.pockets[2];
		if (t||m||l) {
			json.addProperty("pockets", (t?1:0)+(m?2:0)+(l?4:0));
		}
		//json.addProperty("category", "misc");
		//json.addProperty("group", "genuinebackpacks");
	}

	@Override
	public ResourceLocation getId() { return this.id; }
	@Override
	public RecipeSerializer<?> getType() { return GenuineBackpacks.SEWING_RECIPE_JSON.get(); }

	@Override
	public JsonObject serializeAdvancement() { return null; }
	@Override
	public ResourceLocation getAdvancementId() { return null; }
}
