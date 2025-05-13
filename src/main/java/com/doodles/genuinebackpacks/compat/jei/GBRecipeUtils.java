package com.doodles.genuinebackpacks.compat.jei;

import java.util.List;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.recipe.SewingRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;

public class GBRecipeUtils {

	private static ClientLevel level = Minecraft.getInstance().level;
	
	public static ItemStack getResult(SewingRecipe recipe) {
		if (level == null) throw new NullPointerException("Null level when getting recipe result");
		return recipe.getResultItem(level.registryAccess());
	}
	
	public static List<SewingRecipe> getRecipes() {
		return level.getRecipeManager().getAllRecipesFor(GenuineBackpacks.SEWING_RECIPE_TYPE.get());
	}
}
