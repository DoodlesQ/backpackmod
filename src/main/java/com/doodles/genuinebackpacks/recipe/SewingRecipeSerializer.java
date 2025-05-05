package com.doodles.genuinebackpacks.recipe;

import com.doodles.genuinebackpacks.content.backpack.BackpackItem;
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
        
        JsonArray ingarray = GsonHelper.getAsJsonArray(json, "items");
        JsonArray countarray = GsonHelper.getAsJsonArray(json, "counts");
        for (int i = 0; i < ingarray.size(); i++) {
        	ingredients.add(Ingredient.fromJson(ingarray.get(i).getAsJsonObject()));
            counts.add(countarray.get(i).getAsInt());
        }
        Ingredient result = null;
        if (GsonHelper.isValidNode(json, "packColor")) {
        	BackpackItem pack = (BackpackItem) ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(json, "result"));
        	result = Ingredient.of(pack.getDyed(GsonHelper.getAsInt(json, "packColor")));
        }
        if (GsonHelper.isValidNode(json, "pockets")) {
        	BackpackItem pack = (BackpackItem) ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(json, "result"));
        	ItemStack stack = new ItemStack(pack);
        	int pockets = GsonHelper.getAsInt(json, "pockets");
        	if ((pockets&1)==1) 	BackpackItem.addPocket(stack, BackpackItem.TINY);
        	if ((pockets>>1&1)==1) 	BackpackItem.addPocket(stack, BackpackItem.MEDIUM);
        	if ((pockets>>2&1)==1) 	BackpackItem.addPocket(stack, BackpackItem.LARGE);
        	result = Ingredient.of(stack);
        }
        if (result == null) result = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "result"));
        
        return new SewingRecipe(id, ingredients, counts, result);
	}
    
	@Override
	public SewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        NonNullList<Integer> counts = NonNullList.create();
        for (int i = 0; i < buffer.readInt(); i++)
        	ingredients.add(Ingredient.fromNetwork(buffer));
        for (int i = 0; i < buffer.readInt(); i++)
        	counts.add(buffer.readInt());
		Ingredient result = Ingredient.fromNetwork(buffer);
        return new SewingRecipe(id, ingredients, counts, result);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SewingRecipe recipe) {
		buffer.writeInt(recipe.recipeItems.size());
		for (Ingredient i : recipe.recipeItems) { i.toNetwork(buffer); }
		buffer.writeInt(recipe.counts.size());
		for (int i : recipe.counts) { buffer.writeInt(i); }
		recipe.result.toNetwork(buffer);
	}
}
