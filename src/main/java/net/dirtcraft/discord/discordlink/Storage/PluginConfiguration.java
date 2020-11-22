package net.dirtcraft.discord.discordlink.Storage;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigSerializable
public class PluginConfiguration {
    @Setting private Main main = new Main();
    @Setting private Embed embed = new Embed();
    @Setting private Roles roles = new Roles();
    @Setting private Command command = new Command();
    @Setting private Database database = new Database();
    @Setting private Format format = new Format();
    @Setting private Promotion promotion = new Promotion();

    @ConfigSerializable
    public static class Main {
        @Setting(value = "Silent-Whitelisted-Console-Prefix", comment = "Prefix to use private console command (Whitelisted, Same as non-proxy Discord-Link)")
        public static String consolePrivate = "#/";

        @Setting(value = "Whitelisted-Console-Prefix", comment = "Prefix to use bungee command (Whitelisted, Same as non-proxy Discord-Link)")
        public static String consolePublic = "/";

        @Setting(value = "Silent-Manager-Console-Prefix", comment = "Prefix to use private console command (Full Access, Set different from non-proxy Discord-Link)")
        public static String bungeePrivate = "#$";

        @Setting(value = "Console-Manager-Prefix", comment = "Prefix to use bungee command (Full Access, Set different from non-proxy Discord-Link)")
        public static String bungeePublic = "$";

        @Setting(value = "Bot-Prefix", comment = "Prefix to use bot commands")
        public static String discordCommand = "!";

        @Setting(value = "Gamechat Category ID")
        public static long GAMECHAT_CATEGORY_ID = 516473998478016512L;

        @Setting(value = "Sanction Log Channel ID", comment = "Channel for sanction messages from litebans (OPTIONAL)")
        public static long defaultChannelID = 768343841106559026L;

        @Setting(value = "Discord-Server-ID")
        public static long serverLogChannelID = 566095634008899585L;

        @Setting(value = "Discord Server Invite")
        public static String DISCORD_INVITE = "https://discord.com/invite/mqQX9f";

        @Setting(value = "Discord Bot Token")
        public static String botToken = "";
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
        public static String moderatorRoleID = "332701183477284867";

        @Setting(value = "Helper-Role-ID")
        public static String helperRoleID = "563538434333999108";

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
        @Setting(value = "Admin-Command-Whitelist", comment = "A list of commands admins are allowed to use. Make sure these are in the non-bungee versions ignore list")
        public static List<String> whiteList = Arrays.asList(
                "find",
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
        @Setting(value = "LiteBan-Sanctions", comment = "A list of LiteBan sanctions admins are allowed to use. These should not be in the command list, and will have a sender argument specified.")
        public static List<String> sanctions = Arrays.asList(
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
                "kick"
        );
    }

    @ConfigSerializable
    public static class Database {
        @Setting public static String USER = "";
        @Setting public static String PASS = "";
        @Setting public static String IP = "127.0.0.1";
        @Setting public static int PORT = 3306;
    }

    @ConfigSerializable
    public static class Promotion{
        @Setting(value = "Discord-Staff-Tag", comment = "The role that denotes someone is staff.")
        public static Long staffTag = 549039481450397699L;
        @Setting(value = "Role-Map", comment = "For certain groups that are represented by a role, map them here for them to be assigned.")
        public static Map<String, Long> roles = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>("manager", 591732856443895808L),
                new AbstractMap.SimpleImmutableEntry<>("admin", 531631265443479562L),
                new AbstractMap.SimpleImmutableEntry<>("moderator", 332701183477284867L),
                new AbstractMap.SimpleImmutableEntry<>("helper", 563538434333999108L))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
