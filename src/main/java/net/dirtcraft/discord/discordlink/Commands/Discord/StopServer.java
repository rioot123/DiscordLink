package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StopServer implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        try {
            source.getGamechat().sendEmbed("Discord-Link Reboot", "Attempting to reboot the server.");
            source.getMessage().delete().queue(s->{},e->{});
            Thread.sleep(555);
        } catch (Throwable ignored){

        } finally {
            CompletableFuture.runAsync(()-> Runtime.getRuntime().halt(-1));
        }
    }
}