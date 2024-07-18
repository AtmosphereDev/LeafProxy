package dev.vinkyv.leafproxy.network.handler;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.scheduler.LeafScheduler;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

public class RequestNetworkSettingsHandler implements BedrockPacketHandler {

	private final LeafServer proxy;
	private final BedrockServerSession session;

	public RequestNetworkSettingsHandler(LeafServer proxy, BedrockServerSession session) {
		this.proxy = proxy;
		this.session = session;
	}

	public PacketSignal handle(RequestNetworkSettingsPacket packet) {
		int protocolVersion = packet.getProtocolVersion();
		int currentProtocol = LeafServer.CODEC.getProtocolVersion();

		if (protocolVersion != currentProtocol) {
			PlayStatusPacket playStatusPacket = new PlayStatusPacket();
			if (protocolVersion > currentProtocol) playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
			else playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
			session.sendPacketImmediately(playStatusPacket);
			return PacketSignal.HANDLED;
		}

		PacketCompressionAlgorithm compressionAlgorithm = PacketCompressionAlgorithm.ZLIB;
		NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
		networkSettingsPacket.setCompressionThreshold(0);
		networkSettingsPacket.setCompressionAlgorithm(compressionAlgorithm);
		session.sendPacketImmediately(networkSettingsPacket);
		session.setCompression(compressionAlgorithm);
		session.setPacketHandler(new LoginHandler(proxy, session));

		return PacketSignal.HANDLED;
	}
}
