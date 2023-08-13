// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.channels;

import net.dirtcraft.spongediscordlib.channels.GameChatChannel;
import net.dirtcraft.spongediscordlib.channels.LogChannel;
import net.dirtcraft.spongediscordlib.channels.DiscordChannel;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.JDA;
import net.dirtcraft.spongediscordlib.channels.ChannelManager;

public class ChannelManagerImpl implements ChannelManager
{
    private final JDA jda;
    private LogChannelImpl logChannel;
    private GameChatChannelImpl defaultChannel;
    private long guild;
    
    public ChannelManagerImpl(final JDA jda) {
        this.jda = jda;
        this.loadFromConfig();
    }
    
    public DiscordChannelImpl getChannel(final long channel) {
        return new DiscordChannelImpl(this.jda, channel);
    }
    
    public DiscordChannelImpl getChannel(final long channel, final boolean isPrivate) {
        return new DiscordChannelImpl(this.jda, channel, isPrivate);
    }
    
    public GameChatChannelImpl getGameChat() {
        return this.defaultChannel;
    }
    
    public LogChannelImpl getLogChannel() {
        return this.logChannel;
    }
    
    public TextChannel getDefaultChannel() {
        return this.jda.getTextChannelById(this.defaultChannel.getId());
    }
    
    public boolean isGamechat(final MessageChannel channel) {
        return channel.getIdLong() == this.defaultChannel.getId();
    }
    
    public Guild getGuild() {
        return this.jda.getGuildById(this.guild);
    }
    
    public void loadFromConfig() {
        this.logChannel = new LogChannelImpl(this.jda, Long.parseLong(PluginConfiguration.Main.serverLogChannelID));
        this.defaultChannel = new GameChatChannelImpl(this.jda, PluginConfiguration.Main.defaultChannelID);
        this.guild = ((this.getDefaultChannel() != null) ? this.getDefaultChannel().getGuild().getIdLong() : -1L);
    }
}
