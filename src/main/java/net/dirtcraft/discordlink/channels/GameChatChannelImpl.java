// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.channels;

import net.dirtcraft.discordlink.commands.sources.ResponseScheduler;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dirtcraft.spongediscordlib.channels.GameChatChannel;

public class GameChatChannelImpl extends DiscordChannelImpl implements GameChatChannel
{
    GameChatChannelImpl(final JDA jda, final long channel) {
        super(jda, channel);
    }
    
    GameChatChannelImpl(final JDA jda, final long channel, final boolean isPrivate) {
        super(jda, channel, isPrivate);
    }
    
    public void sendPlayerMessage(final String prefix, final String playerName, final String message) {
        final String output = PluginConfiguration.Format.serverToDiscord.replace("{prefix}", prefix).replace("{username}", playerName).replace("{message}", message);
        ResponseScheduler.submit(this.getChatResponder(), output);
    }
}
