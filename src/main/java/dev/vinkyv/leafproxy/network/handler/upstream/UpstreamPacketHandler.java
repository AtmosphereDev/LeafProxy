package dev.vinkyv.leafproxy.network.handler.upstream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jwt.SignedJWT;
import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.logger.MainLogger;
import dev.vinkyv.leafproxy.network.handler.downstream.DownstreamPacketHandler;
import dev.vinkyv.leafproxy.network.protocol.ProtocolVersion;
import dev.vinkyv.leafproxy.network.session.ProxyPlayerSession;
import dev.vinkyv.leafproxy.network.session.ProxyServerSession;
import dev.vinkyv.leafproxy.utils.HandshakeUtils;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainer;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.net.InetSocketAddress;
import java.util.List;

public class UpstreamPacketHandler implements BedrockPacketHandler {
    private final ProxyServerSession session;
    private ProxyPlayerSession player;
    private final LeafServer proxy;
    private JsonObject clientData;
    private JsonObject extraData;

    public UpstreamPacketHandler(ProxyServerSession session, LeafServer proxy) {
        this.session = session;
        this.proxy = proxy;
    }

    @Override
    public void onDisconnect(String reason) {
        if (session.isConnected()) session.disconnect(reason);
        if (player != null) {
            MainLogger.getLogger().info("Disconnected {}", player.getUpstream().getPeer().getSocketAddress().toString());
            if (player.getDownstream().isConnected()) player.getDownstream().disconnect(reason);
            if (player.getUpstream().isConnected()) player.getUpstream().disconnect(reason);
            proxy.players.remove(player);
        }
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        int protocolVersion = packet.getProtocolVersion();

        if (protocolVersion > LeafServer.MAXCODEC.getProtocolVersion() || protocolVersion < LeafServer.CODEC.getProtocolVersion()) {
            PlayStatusPacket playStatusPacket = new PlayStatusPacket();

            if (protocolVersion > LeafServer.MAXCODEC.getProtocolVersion()) {
                playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
            }

            if (protocolVersion < LeafServer.CODEC.getProtocolVersion()) {
                playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            }

            session.sendPacketImmediately(playStatusPacket);
            return PacketSignal.HANDLED;
        }

        session.setCodec(ProtocolVersion.get(protocolVersion).getBedrockCodec());

        NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
        networkSettingsPacket.setCompressionThreshold(Leaf.getConfig().getCompressionThreshold());
        networkSettingsPacket.setCompressionAlgorithm(LeafServer.compressionAlgorithm);

        session.sendPacketImmediately(networkSettingsPacket);
        session.setCompression(LeafServer.compressionAlgorithm);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LoginPacket packet) {
        try {
            List<String> chain = packet.getChain();

            JsonObject payload = (JsonObject) JsonParser.parseString(SignedJWT.parse(chain.get(chain.size() - 1)).getPayload().toString());

            extraData = HandshakeUtils.parseExtraData(packet, payload);
            SignedJWT extraDataJwt = SignedJWT.parse(packet.getExtra());
            clientData = HandshakeUtils.parseClientData(extraDataJwt, extraData, session);
            initializeProxySession();
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TextPacket packet) {
        if (packet.getType() == TextPacket.Type.CHAT) {
            packet.setType(TextPacket.Type.RAW);
            packet.setMessage(packet.getSourceName() + " > " + packet.getMessage());
            proxy.players.forEach(((s, player) -> player.sendPacket(packet)));
            return PacketSignal.HANDLED;
        }
        return PacketSignal.UNHANDLED;
    }

    @Override
    public PacketSignal handle(ItemStackResponsePacket packet) {
        // TODO: Make it workable
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerboundLoadingScreenPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerboundDiagnosticsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PacketViolationWarningPacket packet) {
        MainLogger.getLogger().debug(packet.toString());
        session.sendPacket(packet);
        return PacketSignal.HANDLED;
    }

    private void initializeProxySession() {
        MainLogger.getLogger().info("Connecting {}", extraData.get("displayName").getAsString());
        MainLogger.getLogger().info(this.session.getSocketAddress().toString());
        proxy.newClient(new InetSocketAddress(Leaf.getConfig().getServerAddress(), Leaf.getConfig().getServerPort()),downstream -> {
            downstream.setCodec(LeafServer.CODEC);
            downstream.setSendSession(this.session);
            downstream.getPeer().getCodecHelper().setEncodingSettings(EncodingSettings.CLIENT);
            this.session.setSendSession(downstream);

            ProxyPlayerSession playerSession = new ProxyPlayerSession(this.session, downstream);
            this.player = playerSession;

            downstream.setPlayer(playerSession);
            this.session.setPlayer(playerSession);

            SignedJWT signedClientData = HandshakeUtils.createExtraData(playerSession.getEncryptionKey(), extraData);
            SignedJWT signedExtraData = HandshakeUtils.encodeJWT(playerSession.getEncryptionKey(), clientData);

            LoginPacket loginPacket = new LoginPacket();
            loginPacket.getChain().add(signedClientData.serialize());
            loginPacket.setExtra(signedExtraData.serialize());
            loginPacket.setProtocolVersion(LeafServer.CODEC.getProtocolVersion());

            this.player.setLoginPacket(loginPacket);
            this.player.setName(extraData.get("displayName").getAsString());
            this.player.setXuid(extraData.get("XUID").getAsString());
            downstream.setPacketHandler(new DownstreamPacketHandler(downstream, proxy, this.player));
            downstream.setLogging(true);

            proxy.players.put(extraData.get("displayName").getAsString(), player);

            RequestNetworkSettingsPacket packet = new RequestNetworkSettingsPacket();
            packet.setProtocolVersion(LeafServer.CODEC.getProtocolVersion());
            downstream.sendPacketImmediately(packet);

            MainLogger.getLogger().info("{} connected!", extraData.get("displayName").getAsString());
        });
    }
}
