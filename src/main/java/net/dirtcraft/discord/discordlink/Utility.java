package net.dirtcraft.discord.discordlink;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.spongediscordlib.DiscordUtil;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.storage.WorldProperties;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Utility {

    public static EmbedBuilder embedBuilder() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(PluginConfiguration.Embed.title);
        if (PluginConfiguration.Embed.timestamp) {
            embed.setTimestamp(Instant.now());
        }
        return embed;
    }

    public static void chatToDiscord(String prefix, String playerName, String message) {
        DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(
                        PluginConfiguration.Format.serverToDiscord
                                .replace("{prefix}", prefix)
                                .replace("{username}", playerName)
                                .replace("{message}", message))
                .queue();
    }

    public static void messageToChannel(String type, String message, MessageEmbed embed) {
        switch (type.toLowerCase()) {
            default:
            case "message":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(message)
                        .queue();
            break;
            case "embed":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(embed)
                        .queue();
                break;
        }
    }

    public static void autoRemove(int delaySeconds, String type, String message, MessageEmbed embed) {
        switch (type.toLowerCase()) {
            default:
            case "message":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(message)
                        .queue(msg -> {
                            msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS);
                        });
                break;
            case "embed":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(embed)
                        .queue(msg -> {
                            msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS);
                        });
                break;
        }
    }

    public static void setTopic() {
        TextChannel channel = DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID());
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            String name = SpongeDiscordLib.getServerName().split(" ")[1];
            String code = SpongeDiscordLib.getServerName().toLowerCase().split(" ")[1];
            switch (code) {
                case "redstone":
                    code = "red";
                    break;
                case "glowstone":
                    code = "glow";
                    break;
                default:
                case "lapiz":
                    break;
            }
            channel.getManager()
                    .setTopic("**Pixelmon " + name + "** — IP: " + code + ".pixelmon.gg")
                    .queue();
            return;
        }
        String code = channel.getName().split("-")[1];

        channel.getManager()
                .setTopic("ModPack: **" + SpongeDiscordLib.getServerName() + "** — IP: " + code + ".dirtcraft.gg")
                .queue();
    }

    public static void setStatus() {
        DiscordUtil.setStatus(Game.GameType.STREAMING, SpongeDiscordLib.getServerName(), "https://www.twitch.tv/dirtcraft/");
    }

    public static void listCommand(MessageReceivedEvent event) {
        Member member = event.getMember();

        Collection<Player> players = Sponge.getServer().getOnlinePlayers();

        ArrayList<String> playerNames = new ArrayList<>();
        players.forEach(online -> {
            if (NucleusAPI.getAFKService().isPresent()) {
                if (NucleusAPI.getAFKService().get().isAFK(online)) {
                    playerNames.add(online.getName() + " " + "—" + " " + "**AFK**");
                } else {
                    playerNames.add(online.getName());
                }
            } else {
                playerNames.add(online.getName());
            }
        });

        playerNames.sort(String::compareToIgnoreCase);

        EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() > 1) {
            embed.addField("__**" + players.size() + "** players online__", String.join("\n", playerNames), false);
        } else if (players.size() == 1) {
            embed.addField("__**" + players.size() + "** player online__", String.join("\n", playerNames), false);
        } else {
            embed.setDescription("There are no players playing **" + SpongeDiscordLib.getServerName() + "**!");
        }
                embed.setFooter("Requested By: " + member.getUser().getAsTag(), event.getAuthor().getAvatarUrl());

        DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(embed.build())
                .queue();
    }

    public static void toConsole(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw()
                .replace(PluginConfiguration.Main.consolePrefix, "")
                .split(" ");

        if (!consoleCheck(event)) {
            sendPermissionErrorMessage(event);
            return;
        }

        String command = String.join(" ", args);
        Task.builder()
                .execute(() ->
                        Sponge.getCommandManager().process(new ConsoleManager(Sponge.getServer().getConsole(), event.getMember(), command), command))
                .submit(DiscordLink.getInstance());
    }

    private static boolean consoleCheck(MessageReceivedEvent event) {
        Role adminRole = event.getGuild().getRoleById(PluginConfiguration.Roles.adminRoleID);
        Role ownerRole = event.getGuild().getRoleById(PluginConfiguration.Roles.ownerRoleID);

        if (event.getMember().getRoles().contains(ownerRole)) {
            return true;
        } else if (event.getMember().getRoles().contains(adminRole)) {
            if (event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "luckperms") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "perm") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "permissions") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "perm") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "lp") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "execute") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "ban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "ipban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "tempban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "nameban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "nameunban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "tempmute") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "mute") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "kick") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "whitelist")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static void emergencyStop(MessageReceivedEvent event, boolean hardExit){
        Role ownerRole = event.getGuild().getRoleById(PluginConfiguration.Roles.ownerRoleID);
        Role dirtyRole = event.getGuild().getRoleById(PluginConfiguration.Roles.dirtyRoleID);
        List<Role> roles = event.getMember().getRoles();
        if (roles.contains(ownerRole) || roles.contains(dirtyRole)) {
            FMLCommonHandler.instance().exitJava(-1, hardExit);
            sendResponse(event, "Emergency shutdown has been executed. Please wait.", 15);
        } else {
            sendPermissionErrorMessage(event);
        }
    }

    public static void unstuck(MessageReceivedEvent event, Storage dbHelper) {
        Role verifiedRole = event.getGuild().getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        List<Role> roles = event.getMember().getRoles();
        if (!roles.contains(verifiedRole)) {
            sendPermissionErrorMessage(event);
            return;
        }
        CompletableFuture.runAsync(() -> {

            final Optional<UserStorageService> userStorage = Sponge.getGame().getServiceManager().provide(UserStorageService.class);
            if (!userStorage.isPresent()) {
                sendResponse(event, "Could not execute the command. Please try again later or contact support for further assistance. (Err.1)");
                return;
            }

            final Optional<WorldProperties> optionalWorld = Sponge.getServer().getDefaultWorld();
            if (!optionalWorld.isPresent()) {
                sendResponse(event, "Could not execute the command. Please try again later or contact support for further assistance. (Err.2)");
                return;
            }

            final String uuid = dbHelper.getUUIDfromDiscordID(event.getAuthor().getId());
            if (uuid == null) {
                sendResponse(event, "Could not execute the command as we could not find your UUID. Please try again later or contact support for further assistance.");
                return;
            }

            final Optional<User> optionalUser = userStorage.get().get(UUID.fromString(uuid));
            if (!optionalUser.isPresent()) {
                sendResponse(event, "Could not execute the command as we could not find your minecraft account. Please try again later or contact support for further assistance.");
                return;
            }

            final User user = optionalUser.get();
            final WorldProperties spawn = optionalWorld.get();

            if (user.getPlayer().isPresent()) {
                Task.builder().execute(()->{
                    user.getPlayer().get().transferToWorld(spawn.getUniqueId(), spawn.getSpawnPosition().toDouble());
                }).submit(DiscordLink.getInstance());
            } else {
                Task.builder().execute(()->{
                    user.setLocation(spawn.getSpawnPosition().toDouble(), spawn.getUniqueId());
                }).submit(DiscordLink.getInstance());
            }
            sendResponse(event, "Successfully moved " + user.getName() +  " to spawn.", 15);
        });
    }

    private static void sendResponse(MessageReceivedEvent event, String error){
        sendResponse(event, error, 30);
    }

    private static void sendResponse(MessageReceivedEvent event, String error, int delay){
        event.getMessage().delete().queue();
        Utility.autoRemove(delay, "message", "<@" + event.getAuthor().getId() + ">, " + error, null);
    }

    private static void sendPermissionErrorMessage(MessageReceivedEvent event){
        event.getMessage().delete().queue();
        Utility.autoRemove(5, "message", "<@" + event.getAuthor().getId() + ">, you do **not** have permission to use this command!", null);
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                        .build())
                .queue();
    }

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }
}
