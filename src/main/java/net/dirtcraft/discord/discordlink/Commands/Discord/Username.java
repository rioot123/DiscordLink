package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Username implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordSource member, String[] command, MessageReceivedEvent event) throws DiscordCommandException {
        if (command.length < 2) throw new DiscordCommandException("Invalid Discord ID");
        final String discordID = command[1];
        Pattern pattern = Pattern.compile("<@(\\d+)>");
        Matcher matcher = pattern.matcher(discordID);
        if (!matcher.matches() || GameChat.getGuild().getMemberById(matcher.group(1)) == null) throw new DiscordCommandException("Invalid Discord ID");

        final DiscordSource player = new DiscordSource(GameChat.getGuild().getMemberById(matcher.group(1)));
        final Optional<User> user = player.getSpongeUser();
        if (!user.isPresent()) throw new DiscordCommandException("The user was not verified!");
        GameChat.sendEmbed(null, user.get().getName());
    }
}
