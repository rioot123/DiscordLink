package net.dirtcraft.discord.discordlink.API;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;

import static net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration.Main.*;

public enum  ActionType {
    CHAT( null,               false),
    DISCORD(     botPrefix,          false),
    ADMIN(       consolePrefix,      false),
    ADMIN_SILENT(silentConsolePrefix,true);

    final public String prefix;
    final public boolean silent;

    ActionType(String prefix, boolean silent){
        this.silent = silent;
        this.prefix = prefix;
    }

    public static ActionType fromMessageRaw(String rawMessage){
        return Arrays.stream(values())
                .filter(cmd->rawMessage.startsWith(cmd.prefix))
                .filter(cmd->cmd != CHAT)
                .findFirst()
                .orElse(CHAT);
    }

    public String getCommand(MessageReceivedEvent event){
        return event.getMessage().getContentRaw().substring(prefix.length());
    }
}
