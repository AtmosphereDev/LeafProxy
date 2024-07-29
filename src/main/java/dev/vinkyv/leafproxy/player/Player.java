package dev.vinkyv.leafproxy.player;

import dev.vinkyv.leafproxy.LeafServer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;

public class Player {
    private final LeafServer proxy;
    private final BedrockServerSession connection;
    private final PacketCompressionAlgorithm compression;

    public Player(LeafServer proxy, BedrockServerSession session, PacketCompressionAlgorithm compressionAlgorithm) {
        this.proxy = proxy;
        this.connection = session;
        this.compression = compressionAlgorithm;
    }

    public void initPlayer() {

    }

    public void initialConnect() {
    }
}
