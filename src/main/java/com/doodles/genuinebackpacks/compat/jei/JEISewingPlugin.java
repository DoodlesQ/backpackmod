package com.doodles.genuinebackpacks.compat.jei;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEISewingPlugin implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(GenuineBackpacks.MODID, "sewing_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration register) {
		register.addRecipeCategories(new JEISewingRecipeCategory(register.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration register) {
		register.addRecipes(JEISewingRecipeCategory.type, GBRecipeUtils.getRecipes());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration register) {
		register.addRecipeCatalyst(new ItemStack(GenuineBackpacks.SEWING_TABLE.get()), JEISewingRecipeCategory.type);
	}

	/*
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration register) {
		register.addRecipeTransferHandler(SewingTableMenu.class, GenuineBackpacks.SEWING_TABLE_MENU.get(), JEISewingRecipeCategory.type, 0, 2, 5, 36);
	}
	*/
}
