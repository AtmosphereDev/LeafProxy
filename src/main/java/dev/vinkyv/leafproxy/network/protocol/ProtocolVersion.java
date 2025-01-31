package dev.vinkyv.leafproxy.network.protocol;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v685.Bedrock_v685;
import org.cloudburstmc.protocol.bedrock.codec.v686.Bedrock_v686;
import org.cloudburstmc.protocol.bedrock.codec.v712.Bedrock_v712;
import org.cloudburstmc.protocol.bedrock.codec.v729.Bedrock_v729;
import org.cloudburstmc.protocol.bedrock.codec.v748.Bedrock_v748;
import org.cloudburstmc.protocol.bedrock.codec.v766.Bedrock_v766;

public enum ProtocolVersion {
    MINECRAFT_PE_1_21_0(685, Bedrock_v685.CODEC),
    MINECRAFT_PE_1_21_2(686, Bedrock_v686.CODEC),
    MINECRAFT_PE_1_21_20(712, Bedrock_v712.CODEC),
    MINECRAFT_PE_1_21_30(729, Bedrock_v729.CODEC),
    MINECRAFT_PE_1_21_40(748, Bedrock_v748.CODEC),
    MINECRAFT_PE_1_21_50_29(765, Bedrock_v766.CODEC),
    MINECRAFT_PE_1_21_50(766, Bedrock_v766.CODEC);

    private static final ProtocolVersion[] VALUES = values();
    private static final Int2ObjectMap<ProtocolVersion> VERSIONS = new Int2ObjectOpenHashMap<>();
    static {
        for (ProtocolVersion version : values()) {
            VERSIONS.putIfAbsent(version.getProtocol(), version);
        }
    }

    @Getter
    private final int protocol;
    @Getter
    private BedrockCodec bedrockCodec;

    ProtocolVersion(int protocol, BedrockCodec codec) {
        this.protocol = protocol;
        this.bedrockCodec = codec;
    }

    public static ProtocolVersion get(int protocol) {
        return VERSIONS.get(protocol);
    }
}
