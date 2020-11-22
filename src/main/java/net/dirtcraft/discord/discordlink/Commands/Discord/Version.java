package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.SanctionUtils;

import java.util.List;

public class Version implements DiscordCommandExecutor {

    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        String version = "Discord-Link: " + Settings.VERSION;
        version += "\nPermission System: " + PermissionUtils.VERSION;
        version += "\nSanction System: " + SanctionUtils.VERSION;
        version += "\nPlatform System: " + PlatformUtils.VERSION;
        source.sendCommandResponse("Version Data", version);
    }
}