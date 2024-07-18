package dev.vinkyv.leafproxy.network.handler;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.logger.MainLogger;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.util.List;
import java.util.UUID;

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
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(RequestChunkRadiusPacket packet) {
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(InteractPacket packet) {
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(EmoteListPacket packet) {
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(MovePlayerPacket packet) {
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(AnimatePacket packet) {
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(PlayerActionPacket packet) {
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}

	public PacketSignal handle(TextPacket packet) {
		MainLogger.getLogger().info("[{}] <-> Unhandled packet packet={}!", this.session.getSocketAddress(), packet.getPacketType().name());
		return PacketSignal.HANDLED;
	}
}
