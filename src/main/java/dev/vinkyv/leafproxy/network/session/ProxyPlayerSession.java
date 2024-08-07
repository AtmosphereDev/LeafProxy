package dev.vinkyv.leafproxy.network.session;

import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

import java.security.KeyPair;

@Getter
public class ProxyPlayerSession {
	private final ProxyServerSession upstream;
	private final ProxyClientSession downstream;
	private final KeyPair encryptionKey = EncryptionUtils.createKeyPair();
	@Setter
	private LoginPacket loginPacket;
	@Setter
	private String name;
	@Setter
	private String xuid;

	public ProxyPlayerSession(ProxyServerSession upstream, ProxyClientSession downstream) {
		this.upstream = upstream;
		this.downstream = downstream;
	}

	public void sendPacket(BedrockPacket packet) {
		getUpstream().sendPacket(packet);
	}
}
