package dev.vinkyv.leafproxy.config;

import lombok.Getter;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class LeafConfigLoader {
    public LeafConfig load() {
        try {
            Path configPath = Paths.get("leaf.yaml");
            final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(configPath)
                    .nodeStyle(NodeStyle.BLOCK)
                    .build();

            LeafConfig config = new LeafConfig();

            if (Files.exists(configPath)) {
                ConfigurationNode node = loader.load();
                config = node.get(LeafConfig.class);
            } else {
                ConfigurationNode node = loader.createNode();
                node.set(LeafConfig.class, config);
                loader.save(node);
            }

            return config;
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }
}
