package com.doodles.improvedbackpacks.recipe;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class SewingRecipeSerializer implements RecipeSerializer<SewingRecipe> {
	
	@Override
	public SewingRecipe fromJson(ResourceLocation id, JsonObject json) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        NonNullList<Integer> counts = NonNullList.create();
        //ingredients.add(Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "item0")));
        //ingredients.add(Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "item1")));
        
        JsonArray ingarray = GsonHelper.getAsJsonArray(json, "items");
        ingredients.add(Ingredient.fromJson(ingarray.get(0).getAsJsonObject()));
        ingredients.add(Ingredient.fromJson(ingarray.get(1).getAsJsonObject()));
        JsonArray countarray = GsonHelper.getAsJsonArray(json, "counts");
        counts.add(countarray.get(0).getAsInt());
        counts.add(countarray.get(1).getAsInt());
        
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        return new SewingRecipe(id, ingredients, counts, result);
        //return this.factory.create(id, ingredients, counts, result);
	}
    
	@Override
	public SewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        NonNullList<Integer> counts = NonNullList.create();
        ingredients.add(Ingredient.fromNetwork(buffer));
        ingredients.add(Ingredient.fromNetwork(buffer));
        counts.add(buffer.readInt());
        counts.add(buffer.readInt());
		ItemStack result = buffer.readItem();
        return new SewingRecipe(id, ingredients, counts, result);
		//return this.factory.create(id, ingredients, counts, result);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SewingRecipe recipe) {
		for (Ingredient i : recipe.recipeItems) { i.toNetwork(buffer); }
		for (int i : recipe.counts) { buffer.writeInt(i); }
		buffer.writeItem(recipe.result);
	}
}
