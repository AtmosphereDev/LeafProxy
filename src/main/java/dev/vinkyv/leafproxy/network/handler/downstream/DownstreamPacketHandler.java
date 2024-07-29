package dev.vinkyv.leafproxy.network.handler.downstream;

import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.network.session.ProxyClientSession;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

public class DownstreamPacketHandler implements BedrockPacketHandler {
    private final ProxyClientSession session;
    private final LeafServer proxy;
    private final LoginPacket loginPacket;

    public DownstreamPacketHandler(ProxyClientSession session, LeafServer proxy, LoginPacket loginPacket) {
        this.session = session;
        this.proxy = proxy;
        this.loginPacket = loginPacket;
    }

    @Override
    public PacketSignal handle(NetworkSettingsPacket packet) {
        this.session.setCompression(packet.getCompressionAlgorithm());

        this.session.sendPacketImmediately(this.loginPacket);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerToClientHandshakePacket packet) {
        ClientToServerHandshakePacket clientToServerHandshakePacket = new ClientToServerHandshakePacket();
        session.sendPacketImmediately(clientToServerHandshakePacket);
        return PacketSignal.HANDLED;
    }
}
