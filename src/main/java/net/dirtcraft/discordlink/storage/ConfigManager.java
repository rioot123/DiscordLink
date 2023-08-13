// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import java.io.IOException;
import ninja.leaping.configurate.ConfigurationNode;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager
{
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationOptions options;
    private PluginConfiguration config;
    private CommentedConfigurationNode node;
    
    public ConfigManager(final ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.config = new PluginConfiguration();
        this.node = null;
        this.loader = loader;
        this.options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
        this.load();
    }
    
    public void load() {
        try {
            this.node = (CommentedConfigurationNode)this.loader.load(this.options);
            this.config = (PluginConfiguration)this.node.getValue(TypeToken.of((Class)PluginConfiguration.class), (Object)this.config);
            this.loader.save((ConfigurationNode)this.node);
        }
        catch (IOException | ObjectMappingException ex2) {
            final Exception ex;
            final Exception exception = ex;
            exception.printStackTrace();
        }
    }
    
    public void save() {
        if (this.node == null) {
            return;
        }
        try {
            this.loader.save(this.node.setValue(TypeToken.of((Class)PluginConfiguration.class), (Object)this.config));
        }
        catch (IOException | ObjectMappingException ex2) {
            final Exception ex;
            final Exception exception = ex;
            exception.printStackTrace();
        }
    }
}
