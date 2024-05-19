package dev.vinkyv.leafproxy.config;

import java.io.IOException;
import java.nio.file.Paths;

import dev.vinkyv.leafproxy.logger.MainLogger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import dev.vinkyv.leafproxy.Leaf;

public class LeafConfiguration {
  public String address = "0.0.0.0";
  public int port = 19132;
  public String motd = "§aLeaf§rProxy";
  public String subMotd = "github.com/VinkyV/LeafProxy";
  public String name = "§aLeaf is great!";
  public int maxPlayers = 20;
  public boolean onlineMode = true;

  public int seed = 0;

  public void load() {
    String filePath = "leaf.yml";

    final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
      .path(Paths.get(filePath))
      .nodeStyle(NodeStyle.BLOCK)
      .build();
    
    try {
      CommentedConfigurationNode config = loader.load();
      
      if (config.empty()) {
        config.node("address").set(address);
        config.node("port").set(port);
        config.node("motd").set(motd);
        config.node("sub_motd").set(subMotd);
        config.node("name").set(name);
        config.node("max_players").set(maxPlayers);
        config.node("online_mode").set(onlineMode);
        config.node("seed").set(seed);
        
        loader.save(config);
      } else {
        this.address = config.node("address").getString();
        this.port = config.node("port").getInt();
        this.motd = config.node("motd").getString();
        this.subMotd = config.node("sub_motd").getString();
        this.name = config.node("name").getString();
        this.maxPlayers = config.node("max_players").getInt();
        this.onlineMode = config.node("online_mode").getBoolean();
        this.seed = config.node("seed").getInt();
      }
    } catch(IOException error) {
      MainLogger.getLogger().error(error.getMessage());
    }
  }
}
