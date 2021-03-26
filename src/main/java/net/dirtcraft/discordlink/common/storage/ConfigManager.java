package net.dirtcraft.discordlink.common.storage;

import com.google.common.reflect.TypeToken;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class ConfigManager {
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationOptions options;
    private PluginConfiguration config = new PluginConfiguration();
    private CommentedConfigurationNode node = null;

    public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
        options = ConfigurationOptions.defaults().shouldCopyDefaults(true);
        this.load();
    }

    public void load() {
        try {
            node = loader.load(options);
            config = node.get(PluginConfiguration.class, config);
            loader.save(node);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void save(){
        if (node == null) return;
        try {
            loader.save(node.set(PluginConfiguration.class, config));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
