package net.dirtcraft.discord.discordlink.Configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class PluginConfiguration {
    @Setting(value = "Main")
    private PluginConfiguration.Main main = new PluginConfiguration.Main();
    @Setting(value = "Format")
    private PluginConfiguration.Format format = new PluginConfiguration.Format();

    @ConfigSerializable
    public static class Main {

        @Setting(value = "Channel-ID", comment = "Channel to send in-game messages to")
        public static String channelID = "584645953609400337";

        @Setting(value = "Console-Prefix", comment = "Prefix to use console command")
        public static String consolePrefix = "/";

        @Setting(value = "Bot-Prefix", comment = "Prefix to use bot commands")
        public static String botPrefix = "!";

    }

    @ConfigSerializable
    public static class Format {

        @Setting(value = "Discord-To-Server", comment = "Message format from Discord to server")
        public static String discordToServer = "&9&l» &7[&9Discord&7] &7{username}&8:&r {message}";

        @Setting(value = "Server-To-Discord", comment = "Message format from server to Discord")
        public static String serverToDiscord = "**{prefix} {username}**: {message}";

        @Setting(value = "Server-Start", comment = "Message to Discord when the server has started")
        public static String serverStart = "**{modpack}** has successfully started!";

        @Setting(value = "Server-Stop", comment = "Message to Discord when the server is stoppin!")
        public static String serverStop = "**{modpack}** is now restarting...";

        @Setting(value = "Player-Join", comment = "Message to Discord when a player joins the server")
        public static String playerJoin = "`{prefix} {username} has joined the game`";

        @Setting(value = "New-Player-Join", comment = "Message to Discord when a NEW player joins the server")
        public static String newPlayerJoin = "Welcome **{username}** to DirtCraft!";

        @Setting(value = "Player-Disconnect", comment = "Message to Discord when a player leaves the server")
        public static String playerDisconnect = "`{prefix} {username} has left the game`";

    }

    @ConfigSerializable
    public static class Embed {

        @Setting(value = "Title")
        public static String title = "<:redbulletpoint:539273059631104052>**DirtCraft ChatBot**<:redbulletpoint:539273059631104052>";

        @Setting(value = "Timestamp")
        public static boolean timestamp = true;

    }

}
