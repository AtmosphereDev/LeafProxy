package dev.vinkyv.leafproxy.player;

import dev.vinkyv.leafproxy.LeafServer;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
  private final LeafServer server;

  private final Map<UUID, Player> players = new ConcurrentHashMap<>();

  public PlayerManager(LeafServer server) {
    this.server = server;
  }

  public boolean registerPlayer(Player player) {
    if (player == null) {
      return false;
    }

    this.players.put(new UUID(0, 0), player);
    return true;
  }

  public Map<UUID, Player> getPlayers() {
    return Collections.unmodifiableMap(this.players);
  }
}
