package dev.vinkyv.leafproxy.network.handler;

import dev.vinkyv.leafproxy.LeafServer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

public class XDHandler implements BedrockPacketHandler {

	private final LeafServer proxy;
	private final BedrockServerSession session;

	public XDHandler(LeafServer proxy, BedrockServerSession session) {
		this.proxy = proxy;
		this.session = session;
	}

	public PacketSignal handle(SetLocalPlayerAsInitializedPacket packet) {
		TextPacket textPacket = new TextPacket();
		textPacket.setXuid("");
		textPacket.setType(TextPacket.Type.SYSTEM);
		textPacket.setMessage("Welcome to §aLeafProxy§r!");
		session.sendPacket(textPacket);
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(ClientCacheStatusPacket packet) {
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(RequestChunkRadiusPacket packet) {
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(InteractPacket packet) {
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(EmoteListPacket packet) {
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(MovePlayerPacket packet) {
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(AnimatePacket packet) {
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(PlayerActionPacket packet) {
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(TextPacket packet) {
		return PacketSignal.HANDLED;
	}
}
