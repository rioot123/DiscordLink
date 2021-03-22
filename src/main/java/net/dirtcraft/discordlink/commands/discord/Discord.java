package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class Discord implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        final UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        if (args.size() < 1) throw new DiscordCommandException("Invalid Minecraft name");
        final String minecraftIdentifier = args.get(0);
        User optUser;
        Pattern pattern = Pattern.compile("(\\d{8}-?\\d{4}-?\\d{4}-?\\d{4}-?\\d{12})");
        if ((minecraftIdentifier.length() == 32 || minecraftIdentifier.length() == 36) && pattern.matcher(minecraftIdentifier).matches()){
            UUID uuid = UUID.fromString(minecraftIdentifier);
            optUser = userStorageService.get(uuid).orElseThrow(()->new DiscordCommandException("Invalid UUID."));
        } else {
            optUser = userStorageService.get(minecraftIdentifier).orElseThrow(()->new DiscordCommandException("Invalid username."));
        }
        Optional<DiscordMember> optDiscordSource = DiscordLink.get().getUserManager().getMember(optUser.getUniqueId());
        if (!optDiscordSource.isPresent()) throw new DiscordCommandException("User not verified.");
        source.sendCommandResponse("Discord Username", "\\" + optDiscordSource.get().getAsMention());
    }
}
