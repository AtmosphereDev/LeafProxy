package dev.vinkyv.leafproxy.player;

import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;

public class PlayerSession {
  private final LeafServer server;
  private final BedrockServerSession session;
  private final Player player;

  public PlayerSession(LeafServer server, BedrockServerSession session) {
    this.server = server;
    this.player = new Player(server, session, LeafServer.compressionAlgorithm);
    this.session = session;
    this.session.setPacketHandler(new BedrockPacketHandler() {
      //try {
      //  server.g
      //}
    });
  }
}
