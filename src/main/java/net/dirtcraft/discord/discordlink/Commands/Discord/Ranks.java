package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.RankProvider;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;

public class Ranks implements DiscordCommandExecutor {
    private RankProvider provider;

    @Override
    public void execute(GuildMember source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
        if (Sponge.getGame().getState() != GameState.SERVER_STARTED){
            GameChat.sendMessage("Sorry, The server has not started yet.");
            return;
        } else if (provider == null) {
            provider = RankProvider.INSTANCE;
        }
        provider.execute(source, args, event);
    }
}
