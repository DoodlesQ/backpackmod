package com.doodles.genuinebackpacks.content.backpack;

import org.joml.Quaternionf;

import com.doodles.genuinebackpacks.GenuineBackpacks;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class BackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public BackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) { super(renderer); }

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float _0, float _1, float partialTick, float _3, float _4, float _5) {
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		if (chest.is(GenuineBackpacks.BACKPACK.get()) || chest.is(GenuineBackpacks.ENDER_BACKPACK.get())) {
			renderBackpack(poseStack, buffer, packedLight, player, partialTick, getParentModel(), chest);
		}
	}
	
	private void renderBackpack(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float partialTick, HumanoidModel<AbstractClientPlayer> playerModel, ItemStack backpack) {

		boolean ender = backpack.is(GenuineBackpacks.ENDER_BACKPACK.get());
		BackpackItem.Special egg = BackpackItem.getSpecial(backpack);
		BlockState blockState = GenuineBackpacks.BACKPACK_BLOCK.get().defaultBlockState()
			.setValue(BackpackBlock.ENDER, ender)
			.setValue(BackpackBlock.SPECIAL, egg)
			.setValue(BackpackBlock.MOUNTED, true);
		
		int color = (!ender && egg == BackpackItem.Special.NONE) ? BackpackItem.getDye(backpack) : 0xffffff;
		float[] rgb = {(color >> 16 & 255) / 255.0f,
					   (color >>  8 & 255) / 255.0f,
					   (color       & 255) / 255.0f
					  };

		playerModel.setupAnim((AbstractClientPlayer) player, 0f, 0f, partialTick, 0f, 0f);
		
		poseStack.pushPose();

		poseStack.scale(0.875f, 0.875f, 0.875f);
		playerModel.body.translateAndRotate(poseStack);
		poseStack.rotateAround(new Quaternionf(0f, 0f, 1f, 0f), 0f, 0f, 0f);
		poseStack.translate(-0.5f, -0.83125f, 0.125f);
		
		BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		BakedModel blockModel = blockRenderer.getBlockModel(blockState);
		blockRenderer.getModelRenderer().renderModel(
			poseStack.last(),
			buffer.getBuffer(RenderType.cutout()),
			blockState,
			blockModel,
			rgb[0], rgb[1], rgb[2],
			packedLight,
			OverlayTexture.NO_OVERLAY,
			ModelData.EMPTY,
			RenderType.cutout()
		);
		
		poseStack.popPose();
	}
}
