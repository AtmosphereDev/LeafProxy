package dev.vinkyv.leafproxy.network.converter;

import dev.vinkyv.leafproxy.logger.MainLogger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.nbt.util.VarInts;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelChunkConverter {
    private static final String SOURCE_PALETTE_FILE = "block_pallete_685.nbt";
    private static final String TARGET_PALETTE_FILE = "block_pallete_712.nbt";

    private final Map<Integer, Integer> blockMapping;

    public LevelChunkConverter() throws IOException {
        blockMapping = loadBlockPaletteMapping();
    }

    private Map<Integer, Integer> loadBlockPaletteMapping() throws IOException {
        Map<Integer, Integer> mapping = new HashMap<>();
        Map<Integer, Integer> sourcePalette = readBlockPalette(LevelChunkConverter.SOURCE_PALETTE_FILE);
        Map<Integer, Integer> targetPalette = readBlockPalette(LevelChunkConverter.TARGET_PALETTE_FILE);

        for (int i = 0; i < sourcePalette.size(); i++) {
            int newId = targetPalette.get(sourcePalette.get(i));
            if (newId != -1) {
                mapping.put(i, newId);
            }
        }
        return mapping;
    }

    private Map<Integer, Integer> readBlockPalette(String filePath) throws IOException {
        try (InputStream stream = new FileInputStream(filePath)) {
            NbtMap root = (NbtMap) NbtUtils.createReaderLE(stream).readTag();
            List<NbtMap> blockList = root.getList("palette", NbtType.COMPOUND);
            Map<Integer, Integer> paletteMap = new HashMap<>();

            for (NbtMap block : blockList) {
                int networkId = block.getInt("network_id");
                int blockId = block.getInt("block_id");
                paletteMap.put(networkId, blockId);
            }
            MainLogger.getLogger().debug(blockList.get(2).getString("name"));
            return paletteMap;
        }
    }

    public ByteBuf convertLevelChunkPayload(ByteBuf oldChunkPacket) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(oldChunkPacket.array()));
             DataOutputStream output = new DataOutputStream(outputStream)) {

            while (input.available() > 0) {
                int oldBlockId = VarInts.readUnsignedInt(input);
                int newBlockId = blockMapping.getOrDefault(oldBlockId, oldBlockId);
                VarInts.writeUnsignedInt(output, newBlockId);
            }
        } catch (IOException e) {
            MainLogger.getLogger().error(e.getMessage());
        }
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(outputStream.toByteArray());
        return byteBuf;
    }

//    static NbtMap remapBlock(NbtMap tag) {
//        final String name= tag.getString("name");
//
//        String replacement;
//
//    }
}
