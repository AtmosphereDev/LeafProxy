package dev.vinkyv.leafproxy.network.session;

import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;

public class ProxiedServerSesson extends BedrockServerSession {

	public ProxiedServerSesson(BedrockPeer peer, int subClientId) {
		super(peer, subClientId);
	}
}
