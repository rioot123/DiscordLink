package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StopServer implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        try {
            if (!source.isPrivateMessage()) source.getMessage().delete().queue(s->{},e->{});
            else source.sendCommandResponse("Forced Reboot Scheduled.", "Attempting to reboot the server.");
            Channels.getDefaultChat().sendMessage(source, "Forced Reboot Scheduled.", "Attempting to reboot the server.");
            Thread.sleep(555);
        } catch (Throwable ignored){

        } finally {
            CompletableFuture.runAsync(()-> Runtime.getRuntime().halt(-1));
        }
    }
}