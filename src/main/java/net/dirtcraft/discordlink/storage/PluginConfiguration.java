// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class PluginConfiguration
{
    @Setting("Main")
    private Main main;
    @Setting("Format")
    private Format format;
    @Setting("Embed")
    private Embed embed;
    @Setting("Roles")
    private Roles roles;
    @Setting("Commands")
    private Command command;
    @Setting("Notifier")
    private Notifier notifier;
    @Setting("Misc")
    private Misc misc;
    
    public PluginConfiguration() {
        this.main = new Main();
        this.format = new Format();
        this.embed = new Embed();
        this.roles = new Roles();
        this.command = new Command();
        this.notifier = new Notifier();
        this.misc = new Misc();
    }
    
    @ConfigSerializable
    public static class Main
    {
        @Setting(value = "Silent-Console-Prefix", comment = "Prefix to use private console command")
        public static String consolePrivate;
        @Setting(value = "Console-Prefix", comment = "Prefix to use console command")
        public static String consolePublic;
        @Setting(value = "Silent-Manager-Console-Prefix", comment = "Prefix to use private console command (Full Access, Set different from non-proxy Discord-Link)")
        public static String bungeePrivate;
        @Setting(value = "Console-Manager-Prefix", comment = "Prefix to use bungee command (Full Access, Set different from non-proxy Discord-Link)")
        public static String bungeePublic;
        @Setting(value = "Bot-Prefix", comment = "Prefix to use bot commands")
        public static String discordCommand;
        @Setting("defaultChannelId")
        public static Long defaultChannelID;
        @Setting("Discord-Server-ID")
        public static String serverLogChannelID;
        
        static {
            Main.consolePrivate = "#/";
            Main.consolePublic = "/";
            Main.bungeePrivate = "#$";
            Main.bungeePublic = "$";
            Main.discordCommand = "!";
            Main.defaultChannelID = Long.parseLong(SpongeDiscordLib.getGamechatChannelID());
            Main.serverLogChannelID = "566095634008899585";
        }
    }
    
    @ConfigSerializable
    public static class Format
    {
        @Setting(value = "Discord-To-Server", comment = "Message format from Discord to server")
        public static String discordToServer;
        @Setting(value = "Server-To-Discord", comment = "Message format from server to Discord")
        public static String serverToDiscord;
        @Setting(value = "Server-Start", comment = "Message to Discord when the server has started")
        public static String serverStart;
        @Setting(value = "Server-Stop", comment = "Message to Discord when the server is stopping")
        public static String serverStop;
        @Setting(value = "Player-Join", comment = "Message to Discord when a player joins the server")
        public static String playerJoin;
        @Setting(value = "New-Player-Join", comment = "Message to Discord when a NEW player joins the server")
        public static String newPlayerJoin;
        @Setting(value = "Player-Disconnect", comment = "Message to Discord when a player leaves the server")
        public static String playerDisconnect;
        
        static {
            Format.discordToServer = "&9&l» &7[&9Discord&7] &7{username}&8:&r {message}";
            Format.serverToDiscord = "**{prefix} {username}**: {message}";
            Format.serverStart = "**{modpack}** has successfully started!";
            Format.serverStop = "**{modpack}** is now restarting...";
            Format.playerJoin = "`{prefix} {username} has joined the game`";
            Format.newPlayerJoin = "Welcome **{username}** to DirtCraft!";
            Format.playerDisconnect = "`{prefix} {username} has left the game`";
        }
    }
    
    @ConfigSerializable
    public static class Embed
    {
        @Setting("Title")
        public static String title;
        @Setting("Timestamp")
        public static boolean timestamp;
        
        static {
            Embed.title = "<:redbulletpoint:539273059631104052>**DirtCraft ChatBot**<:redbulletpoint:539273059631104052>";
            Embed.timestamp = true;
        }
    }
    
    @ConfigSerializable
    public static class Roles
    {
        @Setting("Owner-Role-ID")
        public static String ownerRoleID;
        @Setting("Dirty-Role-ID")
        public static String dirtyRoleID;
        @Setting("Admin-Role-ID")
        public static String adminRoleID;
        @Setting("Moderator-Role-ID")
        public static String moderatorRoleID;
        @Setting("Helper-Role-ID")
        public static String helperRoleID;
        @Setting("Staff-Role-ID")
        public static String staffRoleID;
        @Setting("Verified-Role-ID")
        public static String verifiedRoleID;
        @Setting("Donator-Role-ID")
        public static String donatorRoleID;
        @Setting("Nitro-Role-ID")
        public static String nitroRoleID;
        @Setting("Muted-Role-ID")
        public static String mutedRoleID;
        
        static {
            Roles.ownerRoleID = "307551061156298762";
            Roles.dirtyRoleID = "591732856443895808";
            Roles.adminRoleID = "531631265443479562";
            Roles.moderatorRoleID = "332701183477284867";
            Roles.helperRoleID = "563538434333999108";
            Roles.staffRoleID = "549039481450397699";
            Roles.verifiedRoleID = "578447006662524940";
            Roles.donatorRoleID = "591145069810155530";
            Roles.nitroRoleID = "581195961813172225";
            Roles.mutedRoleID = "589777192024670228";
        }
    }
    
    @ConfigSerializable
    public static class Command
    {
        @Setting(value = "Proxy-Commands", comment = "A list of commands that will be processed by the proxy-link module (This list should match the list in said module)")
        public static List<String> ignored;
        @Setting(value = "Admin-Command-Blacklist", comment = "A list of commands admins are not allowed to use.")
        public static List<String> blacklist;
        
        static {
            Command.ignored = Arrays.asList("find", "ban", "tempban", "ipban", "unban", "mute", "tempmute", "ipmute", "unmute", "warn", "unwarn", "kick", "history", "staffhistory", "banlist", "warnings", "dupeip", "geoip", "checkban", "checkmute", "lastuuid");
            Command.blacklist = Arrays.asList("luckperms", "perm", "permissions", "perm", "lp", "execute", "nameban", "nameunban", "whitelist", "minecraft:", "sponge");
        }
    }
    
    @ConfigSerializable
    public static class Notifier
    {
        @Setting(value = "Boot-Notifier-Minutes", comment = "Max boot stage time (minutes) before sending DMs")
        public static long maxStageMinutes;
        @Setting(value = "Notify-Users", comment = "Users to DM (Discord ID's)")
        public static List<Long> notify;
        
        static {
            Notifier.maxStageMinutes = 12L;
            Notifier.notify = new ArrayList<Long>(Arrays.asList(248056002274918400L, 204412960427212800L));
        }
    }
    
    @ConfigSerializable
    public static class Misc
    {
        @Setting(value = "Omit-Chat-Arrow", comment = "Don't apply arrows to prefixes.")
        public static boolean omitChatArrow;
        
        static {
            Misc.omitChatArrow = SpongeDiscordLib.getServerName().toLowerCase().contains("pixel");
        }
    }
}
