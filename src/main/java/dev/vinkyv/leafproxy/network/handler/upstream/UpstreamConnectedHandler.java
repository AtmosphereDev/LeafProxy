package dev.vinkyv.leafproxy.network.handler.upstream;

import dev.vinkyv.leafproxy.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;

public class UpstreamConnectedHandler implements BedrockPacketHandler {
    private final Player player;

    public UpstreamConnectedHandler(Player player) {
        this.player = player;
    }

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        PacketSignal signal = BedrockPacketHandler.super.handlePacket(packet);
        return signal;
    }
}
