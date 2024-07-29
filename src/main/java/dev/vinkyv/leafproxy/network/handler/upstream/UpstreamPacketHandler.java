package dev.vinkyv.leafproxy.network.handler.upstream;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.Filter;
import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.logger.MainLogger;
import dev.vinkyv.leafproxy.network.handler.downstream.DownstreamPacketHandler;
import dev.vinkyv.leafproxy.network.session.ProxyServerSession;
import dev.vinkyv.leafproxy.utils.AuthData;
import dev.vinkyv.leafproxy.utils.ChainData;
import dev.vinkyv.leafproxy.utils.ForgeryUtils;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.bedrock.util.JsonUtils;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.jose4j.json.internal.json_simple.JSONObject;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.List;
import java.util.Map;

public class UpstreamPacketHandler implements BedrockPacketHandler {
    private final ProxyServerSession session;
    private final LeafServer proxy;
    private List<String> chainData;
    private JSONObject skinData;
    private JSONObject extraData;
    private AuthData authData;

    public UpstreamPacketHandler(ProxyServerSession session, LeafServer proxy) {
        this.session = session;
        this.proxy = proxy;
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        int protocolVersion = packet.getProtocolVersion();

        if (protocolVersion != LeafServer.CODEC.getProtocolVersion()) {
            PlayStatusPacket playStatusPacket = new PlayStatusPacket();
            if (protocolVersion > LeafServer.CODEC.getProtocolVersion()) {
                playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD);
            } else {
                playStatusPacket.setStatus(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD);
            }

            session.sendPacketImmediately(playStatusPacket);
            return PacketSignal.HANDLED;
        }
        session.setCodec(LeafServer.CODEC);

        NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
        networkSettingsPacket.setCompressionThreshold(0);
        networkSettingsPacket.setCompressionAlgorithm(LeafServer.compressionAlgorithm);

        session.sendPacketImmediately(networkSettingsPacket);
        session.setCompression(LeafServer.compressionAlgorithm);

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LoginPacket packet) {
        try {
            ChainValidationResult chain = EncryptionUtils.validateChain(packet.getChain());

            extraData = new JSONObject(JsonUtils.childAsType(chain.rawIdentityClaims(), "extraData", Map.class));

            this.authData = new AuthData(chain.identityClaims().extraData.displayName,
                    chain.identityClaims().extraData.identity, chain.identityClaims().extraData.xuid);
            chainData = packet.getChain();
            this.skinData = new ChainData().decodeToken(packet.getChain().get(1));
            initializeProxySession();
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        initializeProxySession();
        return PacketSignal.HANDLED;
    }

    private void initializeProxySession() {
        MainLogger.getLogger().info("Creating new client");
        proxy.newClient(new InetSocketAddress("127.0.0.1", 19132), downstream -> {
            downstream.setCodec(LeafServer.CODEC);
            downstream.setSendSession(session);
            downstream.getPeer().getCodecHelper().setEncodingSettings(EncodingSettings.CLIENT);
            this.session.setSendSession(downstream);

            KeyPair proxyKeyPair = EncryptionUtils.createKeyPair();

            String authData = ForgeryUtils.forgeAuthData(proxyKeyPair, extraData);
            String skinData = ForgeryUtils.forgeSkinData(proxyKeyPair, this.skinData);
            chainData.remove(chainData.size() - 1);
            chainData.add(authData);

            LoginPacket loginPacket = new LoginPacket();
            loginPacket.getChain().addAll(chainData);
            //loginPacket.setExtra(skinData);
            loginPacket.setProtocolVersion(LeafServer.CODEC.getProtocolVersion());

            downstream.setPacketHandler(new DownstreamPacketHandler(downstream, proxy, loginPacket));

            RequestNetworkSettingsPacket packet = new RequestNetworkSettingsPacket();
            packet.setProtocolVersion(LeafServer.CODEC.getProtocolVersion());
            downstream.sendPacketImmediately(packet);
            MainLogger.getLogger().info("New client created!");
        });
    }
}
