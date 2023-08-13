// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.channels;

import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.commands.sources.DiscordResponder;
import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.Comparator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;

public enum MessageIntent
{
    CHAT("\n", Sender.NONE, Type.CHAT_MESSAGE), 
    PUBLIC_BUNGEE(PluginConfiguration.Main.bungeePublic, Sender.GAMECHAT, Type.BUNGEE_COMMAND), 
    PRIVATE_BUNGEE(PluginConfiguration.Main.bungeePrivate, Sender.PRIVATE, Type.BUNGEE_COMMAND), 
    PUBLIC_COMMAND(PluginConfiguration.Main.consolePublic, Sender.GAMECHAT, Type.CONSOLE_COMMAND), 
    PRIVATE_COMMAND(PluginConfiguration.Main.consolePrivate, Sender.PRIVATE, Type.CONSOLE_COMMAND), 
    DISCORD_COMMAND(PluginConfiguration.Main.discordCommand, Sender.NONE, Type.DISCORD_COMMAND);
    
    public final String prefix;
    public final Sender sender;
    public final Type type;
    
    private MessageIntent(final String prefix, final Sender sender, final Type type) {
        this.sender = sender;
        this.prefix = prefix;
        this.type = type;
    }
    
    public static String filterConsolePrefixes(final String command) {
        final String prefixes = Arrays.stream(values()).filter(MessageIntent::isConsole).map((Function<? super MessageIntent, ?>)MessageIntent::getPrefix).collect((Collector<? super Object, ?, String>)Collectors.joining("|"));
        return command.replaceAll("^(" + prefixes + ")", "");
    }
    
    public static MessageIntent fromMessageRaw(final String rawMessage) {
        return Arrays.stream(values()).filter(cmd -> rawMessage.startsWith(cmd.prefix)).filter(cmd -> cmd != MessageIntent.CHAT).max(Comparator.comparingInt(MessageIntent::length)).orElse(MessageIntent.CHAT);
    }
    
    public String getCommand(final MessageReceivedEvent event) {
        return event.getMessage().getContentRaw().substring(this.prefix.length());
    }
    
    public boolean isChat() {
        return this.type == Type.CHAT_MESSAGE;
    }
    
    public boolean isConsole() {
        return this.type == Type.CONSOLE_COMMAND;
    }
    
    public boolean isBotCommand() {
        return this.type == Type.DISCORD_COMMAND;
    }
    
    public boolean isBungee() {
        return this.type == Type.BUNGEE_COMMAND;
    }
    
    public boolean isPrivate() {
        return this.sender == Sender.PRIVATE;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public ConsoleSource getCommandSource(final MessageSourceImpl sender, final String command) {
        if (this.sender == Sender.PRIVATE) {
            return DiscordResponder.getSender(sender);
        }
        return DiscordResponder.getSender(sender.getChannel().getCommandResponder(sender, command));
    }
    
    private int length() {
        return this.prefix.length();
    }
    
    public enum Sender
    {
        PRIVATE, 
        GAMECHAT, 
        NONE;
    }
    
    public enum Type
    {
        BUNGEE_COMMAND, 
        CONSOLE_COMMAND, 
        DISCORD_COMMAND, 
        CHAT_MESSAGE;
    }
}
