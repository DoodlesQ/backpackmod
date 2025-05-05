package com.doodles.genuinebackpacks.network;

import java.util.function.Supplier;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RenamePacket {
	
	private final String name;
	private final String oldName;
	private final InteractionHand hand;
	
	public RenamePacket (String name, String oldName, InteractionHand hand) {
		this.name = name;
		this.oldName = oldName;
		this.hand = hand;
	}
	
	public static void encode (final RenamePacket packet, FriendlyByteBuf buf) {
		buf.writeUtf(packet.name);
		buf.writeUtf(packet.oldName);
		buf.writeEnum(packet.hand);
	}
	
	public static RenamePacket decode (FriendlyByteBuf buf) {
		return new RenamePacket(buf.readUtf(), buf.readUtf(), buf.readEnum(InteractionHand.class));
	}
	
	public static void handle(final RenamePacket packet, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player testplayer = ctx.get().getSender();
			if (testplayer instanceof ServerPlayer player) {
				ItemStack item = player.getItemInHand(packet.hand);
				if ((item.is(GenuineBackpacks.BACKPACK.get()))
						&& item.getHoverName().getString().equals(packet.oldName)
						&& (player.experienceLevel >= 3 || player.isCreative())) {
					item.setHoverName(Component.literal(packet.name));
					player.setExperienceLevels(-3);
				}
			}
		});
        ctx.get().setPacketHandled(true);
	}
}
