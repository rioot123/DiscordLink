package net.dirtcraft.discordlink.common.storage;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class PluginConfiguration {
    @Setting(value = "Main")
    private PluginConfiguration.Main main = new PluginConfiguration.Main();
    @Setting(value = "Format")
    private PluginConfiguration.Format format = new PluginConfiguration.Format();
    @Setting(value = "Embed")
    private PluginConfiguration.Embed embed = new PluginConfiguration.Embed();
    @Setting(value = "Roles")
    private PluginConfiguration.Roles roles = new PluginConfiguration.Roles();
    @Setting(value = "Commands")
    private PluginConfiguration.Command command = new PluginConfiguration.Command();
    @Setting(value = "Notifier")
    private PluginConfiguration.Notifier notifier = new PluginConfiguration.Notifier();
    @Setting
    private PluginConfiguration.Database database = new PluginConfiguration.Database();

    @ConfigSerializable
    public static class Database {
        @Setting public static String USER = "";
        @Setting public static String PASS = "";
        @Setting public static String IP = "127.0.0.1";
        @Setting public static int PORT = 3306;
    }


    @ConfigSerializable
    public static class Main {

        @Setting("Server Name")
        public static String SERVER_NAME = "N/A";

        @Setting("Discord Bot Token")
        public static String TOKEN = "";

        @Comment("Prefix to use private console command")
        @Setting(value = "Silent-Console-Prefix")
        public static String consolePrivate = "#/";

        @Comment("Prefix to use console command")
        @Setting(value = "Console-Prefix")
        public static String consolePublic = "/";

        @Comment("Prefix to use private console command (Full Access, Set different from non-proxy Discord-Link)")
        @Setting("Silent-Manager-Console-Prefix")
        public static String bungeePrivate = "#$";

        @Comment("Prefix to use bungee command (Full Access, Set different from non-proxy Discord-Link)")
        @Setting(value = "Console-Manager-Prefix")
        public static String bungeePublic = "$";

        @Comment("Prefix to use bot commands")
        @Setting("Bot-Prefix")
        public static String discordCommand = "!";

        @Setting("defaultChannelId")
        public static long defaultChannelID;

        @Setting("Discord-Server-ID")
        public static long serverLogChannelID;

    }

    @ConfigSerializable
    public static class Format {

        @Comment("Message format from Discord to server")
        @Setting("Discord-To-Server")
        public static String discordToServer = "&9&l» &7[&9Discord&7] &7{username}&8:&r {message}";

        @Comment("Message format from server to Discord")
        @Setting("Server-To-Discord")
        public static String serverToDiscord = "**{prefix} {username}**: {message}";

        @Comment("Message to Discord when the server has started")
        @Setting("Server-Start")
        public static String serverStart = "**{modpack}** has successfully started!";

        @Comment("Message to Discord when the server is stopping")
        @Setting("Server-Stop")
        public static String serverStop = "**{modpack}** is now restarting...";

        @Comment("Message to Discord when a player joins the server")
        @Setting("Player-Join")
        public static String playerJoin = "`{prefix} {username} has joined the game`";

        @Comment("Message to Discord when a NEW player joins the server")
        @Setting("New-Player-Join")
        public static String newPlayerJoin = "Welcome **{username}** to DirtCraft!";

        @Comment("Message to Discord when a player leaves the server")
        @Setting("Player-Disconnect")
        public static String playerDisconnect = "`{prefix} {username} has left the game`";

    }

    @ConfigSerializable
    public static class Embed {

        @Setting("Title")
        public static String title = "<:redbulletpoint:539273059631104052>**DirtCraft ChatBot**<:redbulletpoint:539273059631104052>";

        @Setting("Timestamp")
        public static boolean timestamp = true;

    }

    @ConfigSerializable
    public static class Roles {

        @Setting("Owner-Role-ID")
        public static long ownerRoleID = 307551061156298762L;

        @Setting("Dirty-Role-ID")
        public static long dirtyRoleID = 591732856443895808L;

        @Setting("Admin-Role-ID")
        public static long adminRoleID = 531631265443479562L;

        @Setting("Moderator-Role-ID")
        public static long moderatorRoleID = 332701183477284867L;

        @Setting("Helper-Role-ID")
        public static long helperRoleID = 563538434333999108L;

        @Setting("Staff-Role-ID")
        public static long staffRoleID = 549039481450397699L;

        @Setting("Verified-Role-ID")
        public static long verifiedRoleID = 578447006662524940L;

        @Setting("Donator-Role-ID")
        public static long donatorRoleID = 591145069810155530L;

        @Setting("Nitro-Role-ID")
        public static long nitroRoleID = 581195961813172225L;

        @Setting("Muted-Role-ID")
        public static long mutedRoleID = 589777192024670228L;
    }

    @ConfigSerializable
    public static class Command {
        @Comment("A list of commands that will be processed by the proxy-link module (This list should match the list in said module)")
        @Setting("Proxy-Commands")
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

        @Comment("A list of commands admins are not allowed to use.")
        @Setting(value = "Admin-Command-Blacklist")
        public static List<String> blacklist = Arrays.asList(
                "luckperms",
                "perm",
                "permissions",
                "perm",
                "lp",
                "execute",
                "nameban",
                "nameunban",
                "whitelist",
                "minecraft:",
                "sponge"
        );
    }

    @ConfigSerializable
    public static class Notifier{
        @Comment("Max boot stage time (minutes) before sending DMs")
        @Setting("Boot-Notifier-Minutes")
        public static long maxStageMinutes = 12;

        @Comment("Users to DM (Discord ID's)")
        @Setting("Notify-Users")
        public static List<Long> notify = new ArrayList<>(Arrays.asList(248056002274918400L, 261928443179040768L));
    }

}
