package dev.vinkyv.leafproxy.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
@ConfigSerializable
public class LeafConfig {
    private String address = "0.0.0.0";
    private int port = 19132;
    private int compressionThreshold = 256;

    private String motd = "§aLeaf§rProxy";
    private String subMotd = "github.com/AtmosphereDev/LeafProxy";
    private String name = "§aLeaf is great!";
    private int maxPlayers = 20;
    private boolean onlineMode = true;

    private String serverAddress = "127.0.0.1";
    private int serverPort = 19142;
    private int seed = -777;
}
