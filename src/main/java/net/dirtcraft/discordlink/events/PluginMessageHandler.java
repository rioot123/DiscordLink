// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.events;

import java.util.function.Consumer;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import org.spongepowered.api.util.Identifiable;
import java.util.function.Function;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import java.util.UUID;
import org.spongepowered.api.Platform;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;

public class PluginMessageHandler implements RawDataListener
{
    public void handlePayload(final ChannelBuf data, final RemoteConnection connection, final Platform.Type side) {
        final String type = data.readUTF();
        if (type.equalsIgnoreCase("promotion")) {
            this.handlePromotionPayload(data);
        }
        else if (type.equalsIgnoreCase("set_roles")) {
            this.handleRolesPayload(data);
        }
    }
    
    private void handlePromotionPayload(final ChannelBuf in) {
        final String secret = in.readUTF();
        final UUID source = UUID.fromString(in.readUTF());
        final String target = in.readUTF();
        final String track = in.readUTF();
        final boolean promote = in.readBoolean();
        final UUID uuid;
        final Player sourcePlayer;
        final String s;
        UUID targetUUID;
        final String s2;
        final boolean b;
        Optional<PermissionProvider.RankUpdate> success;
        final String s3;
        final Optional optional;
        CompletableFuture.runAsync(() -> {
            sourcePlayer = Sponge.getServer().getPlayer(uuid).orElse(null);
            if (sourcePlayer != null) {
                targetUUID = Sponge.getServiceManager().getRegistration((Class)UserStorageService.class).map(ProviderRegistration::getProvider).flatMap(uss -> uss.get(s)).map((Function<? super Object, ? extends UUID>)Identifiable::getUniqueId).orElse(null);
                success = PermissionProvider.INSTANCE.modifyRank(sourcePlayer, targetUUID, s2, b);
                DiscordLink.get().getChannel().sendTo(sourcePlayer, buff -> {
                    buff.writeUTF("promotion");
                    buff.writeUTF(s3);
                    buff.writeBoolean(optional.isPresent());
                    optional.ifPresent(result -> {
                        buff.writeUTF(result.target.toString());
                        buff.writeUTF((result.added == null) ? "null" : result.added);
                        buff.writeUTF((result.removed == null) ? "null" : result.removed);
                    });
                });
            }
        });
    }
    
    private void handleRolesPayload(final ChannelBuf in) {
        final UUID target = UUID.fromString(in.readUTF());
        CompletableFuture.runAsync(() -> Sponge.getServer().getPlayer(target).map(PlatformProvider::getPlayer).ifPresent(Utility::setRoles));
    }
}
