package dev.vinkyv.leafproxy.network.handler;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

public class LoginHandler implements BedrockPacketHandler {
	private final LeafServer proxy;
	private final BedrockServerSession session;

	private boolean loginInit;

	public LoginHandler(LeafServer proxy, BedrockServerSession session) {
		this.proxy = proxy;
		this.session = session;
	}

	private boolean tryLogin() {
		if (this.loginInit) {
			return true;
		}
		// Add banned-ips check
		this.loginInit = true;
		return true;
	}


	public PacketSignal handle(LoginPacket packet) {
		BedrockCodec codec = this.session.getCodec();
		if (!this.tryLogin() || codec != LeafServer.CODEC) {
			this.session.disconnect("Wrong login flow");
			return PacketSignal.HANDLED;
		}
		this.session.setCodec(LeafServer.CODEC);

		Leaf.getLogger().info("[{}] <-> Trying to connect with protocol=({})!", this.session.getSocketAddress(), this.session.getCodec().getProtocolVersion());

		PlayStatusPacket playStatusPacket = new PlayStatusPacket();
		playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
		session.sendPacketImmediately(playStatusPacket);

		ResourcePacksInfoPacket resourcePacksInfoPacket = new ResourcePacksInfoPacket();
		resourcePacksInfoPacket.setForcedToAccept(false);
		resourcePacksInfoPacket.setForcingServerPacksEnabled(false);
		resourcePacksInfoPacket.setScriptingEnabled(false);
		session.sendPacketImmediately(resourcePacksInfoPacket);

		session.setPacketHandler(new ResourcePackClientResponseHandler(proxy, session));

		return PacketSignal.HANDLED;
	}
}
