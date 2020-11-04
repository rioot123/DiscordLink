package net.dirtcraft.discord.discordlink.Storage;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class PluginConfiguration {
    @Setting private PluginConfiguration.Main main = new PluginConfiguration.Main();
    @Setting private PluginConfiguration.Embed embed = new PluginConfiguration.Embed();
    @Setting private PluginConfiguration.Roles roles = new PluginConfiguration.Roles();
    @Setting private PluginConfiguration.Command command = new PluginConfiguration.Command();
    @Setting private PluginConfiguration.Database database = new PluginConfiguration.Database();
    @Setting private PluginConfiguration.Format format = new PluginConfiguration.Format();

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

        @Setting(value = "Bot-Prefix", comment = "Prefix to use bot commands (Managers+, Set different from non-proxy Discord-Link)")
        public static String discordCommand = "~$";

        @Setting(value = "Discord Bot Token")
        public static String botToken = "";

        @Setting(value = "Gamechat Category ID")
        public static long GAMECHAT_CATEGORY_ID = 516473998478016512L;

        @Setting(value = "Sanction Log Channel ID", comment = "Channel for sanction messages from litebans (OPTIONAL)")
        public static long SANCTION_CHANNEL_ID = 768343841106559026L;

        @Setting(value = "Discord Server Invite")
        public static String DISCORD_INVITE = "https://discord.com/invite/mqQX9f";
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
    public static class Format {
        @Setting(value = "Invite-Discord", comment = "Message send to invite player.")
        public static String discordInvite = "&6Join us on discord! &9&l{url}&6.";
    }

    @ConfigSerializable
    public static class Database {
        @Setting public static String USER = "";
        @Setting public static String PASS = "";
        @Setting public static String IP = "127.0.0.1";
        @Setting public static int PORT = 3306;
    }
}
