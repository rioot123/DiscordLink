// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import java.util.Optional;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.entity.living.player.User;
import java.util.UUID;
import java.util.regex.Pattern;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.user.UserStorageService;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Discord implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final UserStorageService userStorageService = (UserStorageService)Sponge.getServiceManager().provideUnchecked((Class)UserStorageService.class);
        if (args.size() < 1) {
            throw new DiscordCommandException("Invalid Minecraft name");
        }
        final String minecraftIdentifier = args.get(0);
        final Pattern pattern = Pattern.compile("(\\d{8}-?\\d{4}-?\\d{4}-?\\d{4}-?\\d{12})");
        User optUser;
        if ((minecraftIdentifier.length() == 32 || minecraftIdentifier.length() == 36) && pattern.matcher(minecraftIdentifier).matches()) {
            final UUID uuid = UUID.fromString(minecraftIdentifier);
            optUser = userStorageService.get(uuid).orElseThrow(() -> new DiscordCommandException("Invalid UUID."));
        }
        else {
            optUser = userStorageService.get(minecraftIdentifier).orElseThrow(() -> new DiscordCommandException("Invalid username."));
        }
        final Optional<DiscordMember> optDiscordSource = DiscordLink.get().getUserManager().getMember(optUser.getUniqueId());
        if (!optDiscordSource.isPresent()) {
            throw new DiscordCommandException("User not verified.");
        }
        source.sendCommandResponse("Discord Username", "\\" + optDiscordSource.get().getAsMention());
    }
}
