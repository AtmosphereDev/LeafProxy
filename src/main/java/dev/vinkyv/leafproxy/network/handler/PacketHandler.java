package dev.vinkyv.leafproxy.network.handler;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public interface PacketHandler<T extends BedrockPacket> {
	void handle(T packet);
}
