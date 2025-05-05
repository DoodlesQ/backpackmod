package com.doodles.genuinebackpacks.network;

import java.util.function.Supplier;

import com.doodles.genuinebackpacks.content.backpack.BackpackItem;
import com.doodles.genuinebackpacks.content.backpack.EnderBackpackItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class BackpackPacket {

	private final ItemStack pack;
	private final boolean ender;
	
	public BackpackPacket (ItemStack pack, boolean ender) {
		this.pack = pack;
		this.ender = ender;
	}
	
	public static void encode (final BackpackPacket packet, FriendlyByteBuf buf) {
		buf.writeItem(packet.pack);
		buf.writeBoolean(packet.ender);
	}
	
	public static BackpackPacket decode (FriendlyByteBuf buf) {
		return new BackpackPacket(buf.readItem(), buf.readBoolean());
	}
	
	public static void handle(final BackpackPacket packet, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (!packet.ender)
				BackpackItem.open(ctx.get().getSender().level(), ctx.get().getSender(), packet.pack);
			else
				EnderBackpackItem.open(ctx.get().getSender().level(), ctx.get().getSender(), packet.pack);
		});
        ctx.get().setPacketHandled(true);
	}
}
