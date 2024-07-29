package dev.vinkyv.leafproxy.network.session;

import dev.vinkyv.leafproxy.logger.MainLogger;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.BedrockSession;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

@Getter
public class ProxyServerSession extends BedrockServerSession {
	@Setter
	private BedrockSession sendSession;

	public ProxyServerSession(BedrockPeer peer, int subClientId) {
		super(peer, subClientId);
	}

	@Override
	protected void onPacket(BedrockPacketWrapper wrapper) {
		BedrockPacket packet = wrapper.getPacket();

		if (packetHandler == null) {
			MainLogger.getLogger().info("Received packet without a packet handler for {} {}", this.getSocketAddress(), packet);
		} else if (packetHandler.handlePacket(packet) == PacketSignal.UNHANDLED  && this.sendSession != null) {
			ByteBuf buffer = wrapper.getPacketBuffer()
					.retainedSlice()
					.skipBytes(wrapper.getHeaderLength());

			UnknownPacket sendPacket = new UnknownPacket();
			sendPacket.setPayload(buffer);
			sendPacket.setPacketId(wrapper.getPacketId());
			this.sendSession.sendPacket(sendPacket);
		}
	}
}
