package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class Discord implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
        final UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        if (args.length < 2) throw new DiscordCommandException("Invalid Minecraft name");
        final String minecraftIdentifier = args[1];
        User optUser;
        Pattern pattern = Pattern.compile("(\\d{8}-?\\d{4}-?\\d{4}-?\\d{4}-?\\d{12})");
        if ((minecraftIdentifier.length() == 32 || minecraftIdentifier.length() == 36) && pattern.matcher(minecraftIdentifier).matches()){
            UUID uuid = UUID.fromString(minecraftIdentifier);
            optUser = userStorageService.get(uuid).orElseThrow(()->new DiscordCommandException("Invalid UUID."));
        } else {
            optUser = userStorageService.get(minecraftIdentifier).orElseThrow(()->new DiscordCommandException("Invalid username."));
        }
        Optional<GuildMember> optDiscordSource = GuildMember.fromPlayerId(optUser.getUniqueId());
        if (!optDiscordSource.isPresent()) throw new DiscordCommandException("User not verified.");
        GameChat.sendEmbed(null, "\\" + optDiscordSource.get().getAsMention());
    }
}
