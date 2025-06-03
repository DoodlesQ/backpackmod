package com.doodles.genuinebackpacks.compat.jei;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.doodles.genuinebackpacks.recipe.SewingRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class JEISewingRecipeCategory implements IRecipeCategory<SewingRecipe> {

	public final Component title;
	public final IDrawable icon;
	public final IDrawable background;
	public static RecipeType<SewingRecipe> type = RecipeType.create(GenuineBackpacks.MODID, "sewing", SewingRecipe.class);
	
	public JEISewingRecipeCategory (IGuiHelper helper) {
		title = GenuineBackpacks.ct("container.%s.sewing_table");
		icon = helper.createDrawableItemStack(new ItemStack(GenuineBackpacks.SEWING_TABLE.get()));
		background = helper.createDrawable(ResourceLocation.fromNamespaceAndPath(GenuineBackpacks.MODID, "textures/gui/jei/sewing_table.png"), 0, 0, 126, 60);
	}
	
	@Override
	public RecipeType<SewingRecipe> getRecipeType() {
		return type;
	}

	@Override
	public Component getTitle() { return title; }
	@Override
	public IDrawable getIcon() { return icon; }
	@Override
	public IDrawable getBackground() { return background; }

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, SewingRecipe recipe, IFocusGroup focuses) {
		ItemStack item0 = recipe.getInputItem(0);
		ItemStack item1 = ItemStack.EMPTY;
		item0.setCount(recipe.getCounts().get(0));
		builder.addInputSlot(22, 10).addItemStack(item0);
		if (recipe.size() > 1) {
			item1 = recipe.getInputItem(1);
			item1.setCount(recipe.getCounts().get(1));
		}
		builder.addInputSlot(22, 34).addItemStack(item1);
		builder.addOutputSlot(88, 22).addItemStack(GBRecipeUtils.getResult(recipe));
	}

}
