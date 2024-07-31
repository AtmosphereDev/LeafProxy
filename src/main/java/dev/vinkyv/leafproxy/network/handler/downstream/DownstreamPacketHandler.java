package dev.vinkyv.leafproxy.network.handler.downstream;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.logger.MainLogger;
import dev.vinkyv.leafproxy.network.session.ProxyClientSession;
import dev.vinkyv.leafproxy.utils.UnknownBlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.bedrock.util.JsonUtils;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.jose4j.json.JsonUtil;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.lang.JoseException;

import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class DownstreamPacketHandler implements BedrockPacketHandler {
    private final ProxyClientSession session;
    private final LeafServer proxy;
    private final LoginPacket loginPacket;
    private final KeyPair proxyKeyPair;

    public DownstreamPacketHandler(ProxyClientSession session, LeafServer proxy, KeyPair proxyKeyPair, LoginPacket loginPacket) {
        this.session = session;
        this.proxy = proxy;
        this.proxyKeyPair = proxyKeyPair;
        this.loginPacket = loginPacket;
    }

    @Override
    public PacketSignal handle(NetworkSettingsPacket packet) {
        this.session.setCompression(packet.getCompressionAlgorithm());
        MainLogger.getLogger().info("Compression algorithm picked {}", packet.getCompressionAlgorithm());

        this.session.sendPacketImmediately(this.loginPacket);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerToClientHandshakePacket packet) {
        try {
            JsonWebSignature jws = new JsonWebSignature();
            jws.setCompactSerialization(packet.getJwt());
            JSONObject saltJwt = new JSONObject(JsonUtil.parseJson(jws.getUnverifiedPayload()));
            String x5u = jws.getHeader(HeaderParameterNames.X509_URL);
            ECPublicKey serverKey = EncryptionUtils.parseKey(x5u);
            SecretKey key = EncryptionUtils.getSecretKey(proxyKeyPair.getPrivate(), serverKey,
                    Base64.getDecoder().decode(JsonUtils.childAsType(saltJwt, "salt", String.class)));
            session.enableEncryption(key);
        } catch (JoseException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        ClientToServerHandshakePacket clientToServerHandshakePacket = new ClientToServerHandshakePacket();
        session.sendPacketImmediately(clientToServerHandshakePacket);

        MainLogger.getLogger().info("Downstream connected!");
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(DisconnectPacket packet) {
        MainLogger.getLogger().info("[{}] Disconnected!", this.getClass().getName());
        this.session.disconnect();
        // Let the client see the reason too.
        return PacketSignal.UNHANDLED;
    }

    @Override
    public PacketSignal handle(StartGamePacket packet) {
        packet.setServerEngine("LeafProxy");
        packet.setSeed(Leaf.getConfig().seed);

        SimpleDefinitionRegistry<ItemDefinition> itemDefinitions = SimpleDefinitionRegistry.<ItemDefinition>builder()
                .addAll(packet.getItemDefinitions())
                .add(ItemDefinition.AIR)
                .build();

        this.session.getPeer().getCodecHelper().setItemDefinitions(itemDefinitions);

        DefinitionRegistry<BlockDefinition> registry;
        // TODO: Reimplement this
        if (packet.isBlockNetworkIdsHashed()) {
            registry = new UnknownBlockDefinition();
        } else {
            registry = new UnknownBlockDefinition();
        }

        this.session.getPeer().getCodecHelper().setBlockDefinitions(registry);
        return PacketSignal.HANDLED;
    }
}
