package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unlink implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, List<String> args, MessageReceivedEvent event) throws DiscordCommandException {
        Guild guild = GameChat.getGuild();
        Storage storage = DiscordLink.getInstance().getStorage();
        MessageSource author = new MessageSource(event);
        if (args.isEmpty()){
            DiscordLink.getInstance().getStorage().deleteRecord(source.getUser().getId());
            Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
            Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);
            if (author.getRoles().contains(verifiedRole)) {
                guild.getController().removeSingleRoleFromMember(author, verifiedRole).queue();
            }
            if (author.getRoles().contains(donorRole)) {
                guild.getController().removeSingleRoleFromMember(author, donorRole).queue();
            }
            Utility.sendResponse(event, "Successfully unlinked " + author.getSpongeUser().map(User::getName).orElse("your account") + ".");
            return;
        }
        if (!author.hasRole(Roles.ADMIN)) throw new DiscordCommandException("You do not have permission to use this command on other users.");
        final String discordID = args.get(0);
        Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        Matcher matcher = pattern.matcher(discordID);
        if (!matcher.matches() || GameChat.getGuild().getMemberById(matcher.group(1)) == null) throw new DiscordCommandException("Invalid Discord ID");

        final GuildMember player = new GuildMember(GameChat.getGuild().getMemberById(matcher.group(1)));
        final Optional<User> user = player.getSpongeUser();
        String response;
        if (user.isPresent()) {
            response = user.get().getName();
        } else {
            response = storage.getLastKnownUsername(matcher.group(1));
            if (response == null) response = storage.getUUIDfromDiscordID(matcher.group(1));
            if (response == null) throw new DiscordCommandException("The user was not verified!");
        }
        storage.deleteRecord(matcher.group(1));
        Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);

        if (player.getRoles().contains(verifiedRole)) {
            guild.getController().removeSingleRoleFromMember(player, verifiedRole).queue();
        }
        if (player.getRoles().contains(donorRole)) {
            guild.getController().removeSingleRoleFromMember(player, donorRole).queue();
        }
        GameChat.sendEmbed(null, "Successfully unlinked " + response + ".");
    }
}
