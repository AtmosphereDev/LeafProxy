package dev.vinkyv.leafproxy.network.handler;

import dev.vinkyv.leafproxy.LeafServer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.util.List;

public class UpstreamPacketHandler implements BedrockPacketHandler {
	//private final ProxyServerSession server;
	//private final ProxyPlayerSession player;

	@Override
	public PacketSignal handle(RequestNetworkSettingsPacket packet) {
		int protocolVersion = packet.getProtocolVersion();
		if (protocolVersion != LeafServer.CODEC.getProtocolVersion()) {
			PlayStatusPacket statusPacket = new PlayStatusPacket();
			if (protocolVersion > LeafServer.CODEC.getProtocolVersion()) {
				statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
			} else {
				statusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
			}

			//session.sendPacketImmediately(statusPacket);
			return PacketSignal.HANDLED;
		}

		return PacketSignal.HANDLED;
	}

	@Override
	public PacketSignal handle(LoginPacket packet) {
		String extraData = packet.getExtra();
		List<String> chainData = packet.getChain();
		initializeProxySession();
		return PacketSignal.HANDLED;
	}

	private void initializeProxySession() {

	}
}
