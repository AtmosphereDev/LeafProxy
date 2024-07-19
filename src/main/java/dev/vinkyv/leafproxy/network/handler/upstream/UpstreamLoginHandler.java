package dev.vinkyv.leafproxy.network.handler.upstream;

import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.logger.MainLogger;
import dev.vinkyv.leafproxy.player.Player;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

public class UpstreamLoginHandler implements BedrockPacketHandler {
	private final LeafServer proxy;
	private final BedrockServerSession session;
	private Player player;

	public UpstreamLoginHandler(LeafServer proxy, BedrockServerSession session) {
		this.proxy = proxy;
		this.session = session;
	}

	private boolean tryLogin() {
		//TODO: implement strong login check
		return true;
	}

	@Override
	public PacketSignal handle(RequestNetworkSettingsPacket packet) {
		int protocolVersion = packet.getProtocolVersion();
		int currentProtocol = LeafServer.CODEC.getProtocolVersion();

		if (protocolVersion != currentProtocol) {
			PlayStatusPacket playStatusPacket = new PlayStatusPacket();
			if (protocolVersion > currentProtocol) playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
			else playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
			this.session.sendPacketImmediately(playStatusPacket);
			this.session.disconnect();
			return PacketSignal.HANDLED;
		}

		PacketCompressionAlgorithm compressionAlgorithm = LeafServer.compressionAlgorithm;
		NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
		networkSettingsPacket.setCompressionThreshold(1);
		networkSettingsPacket.setCompressionAlgorithm(compressionAlgorithm);
		this.session.setCompression(compressionAlgorithm);
		this.session.sendPacketImmediately(networkSettingsPacket);

		return PacketSignal.HANDLED;
	}


	public PacketSignal handle(LoginPacket packet) {
		BedrockCodec codec = this.session.getCodec();
		if (!this.tryLogin() || codec != LeafServer.CODEC) {
			this.session.disconnect("Wrong login flow");
			return PacketSignal.HANDLED;
		}
		this.session.setCodec(LeafServer.CODEC);

		MainLogger.getLogger().info("[{}] <-> Trying to connect with protocol=({})!", this.session.getSocketAddress(), this.session.getCodec().getProtocolVersion());

		PlayStatusPacket playStatusPacket = new PlayStatusPacket();
		playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
		session.sendPacketImmediately(playStatusPacket);

		ResourcePacksInfoPacket resourcePacksInfoPacket = new ResourcePacksInfoPacket();
		resourcePacksInfoPacket.setForcedToAccept(false);
		resourcePacksInfoPacket.setForcingServerPacksEnabled(false);
		resourcePacksInfoPacket.setScriptingEnabled(false);
		session.sendPacketImmediately(resourcePacksInfoPacket);

		return PacketSignal.HANDLED;
	}

	@Override
	public PacketSignal handle(ClientToServerHandshakePacket packet) {
		PlayStatusPacket status = new PlayStatusPacket();
		status.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
		this.session.sendPacket(status);
		this.player.initPlayer();
		return PacketSignal.HANDLED;
	}
}
