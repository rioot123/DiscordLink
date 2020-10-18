package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.PrivateSender;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.List;

public class IngameCommand implements DiscordCommandExecutor {
    private final String command;

    public IngameCommand(String command){
        this.command = command;
    }
    @Override
    public void execute(GuildMember source, List<String> args, MessageReceivedEvent event) {
        PrivateSender sender = new PrivateSender(source, command);
        Task.builder()
                .execute( () -> Sponge.getCommandManager().process(sender, command))
                .submit(DiscordLink.getInstance());
    }
}
