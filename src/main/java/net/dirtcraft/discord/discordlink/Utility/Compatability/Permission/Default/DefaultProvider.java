package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Utility.PermissionUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DefaultProvider extends PermissionUtils {
    @Override
    public void execute(User user) {
        GameChat.sendMessage("This version of luckperms is not supported!");
    }

    public Optional<RankUpdate> modifyRank(@Nullable Player source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote) {
        if (source != null) source.sendMessage(Text.of("This version of luckperms is not supported!"));
        return Optional.empty();
    }
}
