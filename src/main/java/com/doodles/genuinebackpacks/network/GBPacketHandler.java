package com.doodles.genuinebackpacks.network;

import com.doodles.genuinebackpacks.GenuineBackpacks;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class GBPacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	
	public static SimpleChannel registerNetworkHandler () {
		final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(GenuineBackpacks.MODID, "network"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
		);
		
		GenuineBackpacks.NETWORK = channel;
		
		channel.registerMessage(0, RenamePacket.class, RenamePacket::encode, RenamePacket::decode, RenamePacket::handle);
		channel.registerMessage(1, BackpackPacket.class, BackpackPacket::encode, BackpackPacket::decode, BackpackPacket::handle);
		
		return channel;
	}
	
}