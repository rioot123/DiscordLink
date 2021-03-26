package net.dirtcraft.discordlink.common.channels;

import net.dirtcraft.discordlink.common.storage.PluginConfiguration;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dirtcraft.discordlink.api.channels.LogChannel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LogChannelImpl extends DiscordChannelImpl implements LogChannel {
    LogChannelImpl(JDA jda, long channel) {
        super(jda, channel);
    }

    LogChannelImpl(JDA jda, long channel, boolean isPrivate) {
        super(jda, channel, isPrivate);
    }

    public void sendLog(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("modpack: " + PluginConfiguration.Main.SERVER_NAME)
                .build();
        sendMessage(embed);
    }
}
