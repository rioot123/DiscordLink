package net.dirtcraft.discord.discordlink.Configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class PluginConfiguration {
    @Setting private PluginConfiguration.Main main = new PluginConfiguration.Main();
    @Setting private PluginConfiguration.Format format = new PluginConfiguration.Format();
    @Setting private PluginConfiguration.Embed embed = new PluginConfiguration.Embed();
    @Setting private PluginConfiguration.Roles roles = new PluginConfiguration.Roles();
    @Setting private PluginConfiguration.Command command = new PluginConfiguration.Command();
    @Setting private PluginConfiguration.Database database = new PluginConfiguration.Database();
    @Setting private PluginConfiguration.CrashDetector crashDetector= new PluginConfiguration.CrashDetector();

    @ConfigSerializable
    public static class CrashDetector {
        @Setting(value = "Server-log-ID")
        public static String serverLogID = "566095634008899585";
    }

    @ConfigSerializable
    public static class Main {

        @Setting(value = "Silent-Console-Prefix", comment = "Prefix to use private console command")
        public static String consolePrivate = "#/";

        @Setting(value = "Console-Prefix", comment = "Prefix to use console command")
        public static String consolePublic = "/";

        @Setting(value = "Bot-Prefix", comment = "Prefix to use bot commands")
        public static String discordCommand = "!";

        @Setting(value = "Discord Bot Token")
        public static String botToken = "";

        @Setting(value = "Gamechat Channel ID")
        public static String GAMECHAT_CHANNEL_ID = "";

        @Setting(value = "Server Name")
        public static String SERVER_NAME = "";

        @Setting(value = "Discord Server Invite")
        public static String DISCORD_INVITE = "https://discord.com/invite/mqQX9f";
    }

    @ConfigSerializable
    public static class Format {

        @Setting(value = "Discord-To-Server", comment = "Message format from Discord to server")
        public static String discordToServer = "&9&l» &7[&9Discord&7] &7{username}&8:&r {message}";

        @Setting(value = "Server-To-Discord", comment = "Message format from server to Discord")
        public static String serverToDiscord = "**{prefix} {username}**: {message}";

        @Setting(value = "Server-Start", comment = "Message to Discord when the server has started")
        public static String serverStart = "**{modpack}** has successfully started!";

        @Setting(value = "Server-Stop", comment = "Message to Discord when the server is stopping")
        public static String serverStop = "**{modpack}** is now restarting...";

        @Setting(value = "Player-Join", comment = "Message to Discord when a player joins the server")
        public static String playerJoin = "`{prefix} {username} has joined the game`";

        @Setting(value = "New-Player-Join", comment = "Message to Discord when a NEW player joins the server")
        public static String newPlayerJoin = "Welcome **{username}** to DirtCraft!";

        @Setting(value = "Player-Disconnect", comment = "Message to Discord when a player leaves the server")
        public static String playerDisconnect = "`{prefix} {username} has left the game`";

        @Setting(value = "Invite-Discord", comment = "Message send to invite player.")
        public static String discordInvite = "&6Join us on discord! &9&l{url}&6.";
    }

    @ConfigSerializable
    public static class Embed {

        @Setting(value = "Title")
        public static String title = "<:redbulletpoint:539273059631104052>**DirtCraft ChatBot**<:redbulletpoint:539273059631104052>";

        @Setting(value = "Timestamp")
        public static boolean timestamp = true;
    }

    @ConfigSerializable
    public static class Roles {

        @Setting(value = "Owner-Role-ID")
        public static String ownerRoleID = "307551061156298762";

        @Setting(value = "Dirty-Role-ID")
        public static String dirtyRoleID = "591732856443895808";

        @Setting(value = "Admin-Role-ID")
        public static String adminRoleID = "531631265443479562";

        @Setting(value = "Moderator-Role-ID")
        public static String modRoleID = "332701183477284867";

        @Setting(value = "Staff-Role-ID")
        public static String staffRoleID = "549039481450397699";

        @Setting(value = "Verified-Role-ID")
        public static String verifiedRoleID = "578447006662524940";

        @Setting(value = "Donator-Role-ID")
        public static String donatorRoleID = "591145069810155530";

        @Setting(value = "Nitro-Role-ID")
        public static String nitroRoleID = "581195961813172225";
    }

    @ConfigSerializable
    public static class Command {
        @Setting(value = "Proxy-Commands", comment = "A list of commands that will be processed by the proxy-link module (This list should match the list in said module)")
        public static List<String> ignored = Arrays.asList(
                "find",
                "ban",
                "tempban",
                "ipban",
                "unban",
                "mute",
                "tempmute",
                "ipmute",
                "unmute",
                "warn",
                "unwarn",
                "kick",
                "history",
                "staffhistory",
                "banlist",
                "warnings",
                "dupeip",
                "geoip",
                "checkban",
                "checkmute",
                "lastuuid"
        );

        @Setting(value = "Admin-Command-Blacklist", comment = "A list of commands admins are not allowed to use.")
        public static List<String> blacklist = Arrays.asList(
                "pex",
                "perm",
                "permissions",
                "execute",
                "whitelist",
                "minecraft:",
                "spigot",
                "bukkit",
                "plugins"
        );
    }

    @ConfigSerializable
    public static class Database {
        @Setting public static String USER = "";
        @Setting public static String PASS = "";
        @Setting public static String IP = "127.0.0.1";
        @Setting public static int PORT = 3306;
    }
}
