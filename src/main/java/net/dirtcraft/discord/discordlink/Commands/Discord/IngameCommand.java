package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.List;

public class IngameCommand implements DiscordCommandExecutor {
    private final String command;

    public IngameCommand(String command){
        this.command = command;
    }
    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        String runCommand = this.command + " " + String.join(" ", args);
        ConsoleSource sender = source.getCommandSource(runCommand);
        Task.builder()
                .execute( () -> Sponge.getCommandManager().process(sender, runCommand))
                .submit(DiscordLink.getInstance());
    }
}
