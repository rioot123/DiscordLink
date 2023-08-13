// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.channels;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.JDA;
import net.dirtcraft.spongediscordlib.channels.LogChannel;

public class LogChannelImpl extends DiscordChannelImpl implements LogChannel
{
    LogChannelImpl(final JDA jda, final long channel) {
        super(jda, channel);
    }
    
    LogChannelImpl(final JDA jda, final long channel, final boolean isPrivate) {
        super(jda, channel, isPrivate);
    }
    
    public void sendLog(String header, final String message) {
        header = ((header == null) ? "" : header);
        final MessageEmbed embed = Utility.embedBuilder().addField(header, message, false).setFooter("modpack: " + SpongeDiscordLib.getServerName()).build();
        this.sendMessage(embed);
    }
}
