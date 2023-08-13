// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.permission.dummy;

import net.dirtcraft.discordlink.users.permission.subject.PermissionResolver;
import org.spongepowered.api.text.Text;
import javax.annotation.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;

public class DefaultProvider extends PermissionProvider
{
    @Override
    public void printUserGroups(final MessageSource source, final User user) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }
    
    @Override
    public void printUserKits(final MessageSource source, final User player) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }
    
    @Override
    public void setPlayerPrefix(final ConsoleSource source, final User target, final String prefix) {
    }
    
    @Override
    public void clearPlayerPrefix(final ConsoleSource source, final User target) {
    }
    
    @Override
    public void setPlayerPrefix(final MessageSource source, final User target, final String prefix) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }
    
    @Override
    public void clearPlayerPrefix(final MessageSource source, final User target) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }
    
    @Override
    public Optional<String> getPrefix(final UUID uuid) {
        return Optional.empty();
    }
    
    @Override
    public Optional<RankUpdate> modifyRank(@Nullable final Player source, @Nullable final UUID targetUUID, @Nullable final String trackName, final boolean promote) {
        if (source != null) {
            source.sendMessage((Text)Text.of("This version of luckperms is not supported!"));
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<PermissionResolver> getPermission(final UUID uuid) {
        return Optional.empty();
    }
}
