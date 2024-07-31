package dev.vinkyv.leafproxy.network.session;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

import java.security.KeyPair;

@Getter
public class ProxyPlayerSession {
	private final ProxyServerSession upstream;
	private final ProxyClientSession downstream;
	private final KeyPair encryptionKey = EncryptionUtils.createKeyPair();;

	public ProxyPlayerSession(ProxyServerSession upstream, ProxyClientSession downstream) {
		this.upstream = upstream;
		this.downstream = downstream;
	}
}
