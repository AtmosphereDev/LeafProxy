package dev.vinkyv.leafproxy.network.session;

import dev.vinkyv.leafproxy.logger.MainLogger;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.cloudburstmc.protocol.bedrock.BedrockClientSession;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockSession;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

@Setter
@Getter
public class ProxyClientSession extends BedrockClientSession {
    private BedrockSession sendSession;
    private long playerId;
    private ProxyPlayerSession player;

    public ProxyClientSession(BedrockPeer peer, int subClientId) {
        super(peer, subClientId);
    }

    @Override
    protected void onPacket(BedrockPacketWrapper wrapper) {
        BedrockPacket packet = wrapper.getPacket();

        MainLogger.getLogger().debug("[S -> C] {}", wrapper.getPacket().getPacketType());

        if (this.packetHandler == null) {
            MainLogger.getLogger().warning("Received packet without a packet handler for {} {}", this.getSocketAddress(), packet);
        } else if (this.packetHandler.handlePacket(packet) == PacketSignal.UNHANDLED && this.sendSession != null) {
            ByteBuf buffer = wrapper.getPacketBuffer()
                    .retainedSlice()
                    .skipBytes(wrapper.getHeaderLength());

            UnknownPacket repacket = new UnknownPacket();
            repacket.setPayload(buffer);
            repacket.setPacketId(wrapper.getPacketId());
            this.sendSession.sendPacket(repacket);
        }
    }
}
