package net.dirtcraft.discord.discordlink.Events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class PluginMessageHandler implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        CompletableFuture.runAsync(()->{
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            final String type = in.readUTF();
            if (type.equalsIgnoreCase(Settings.PROMOTION_CHANNEL)) handlePromotionPayload(player, in);
            else if (type.equalsIgnoreCase(Settings.ROLES_CHANNEL)) handleRolesPayload(in);
        });
    }

    private void handlePromotionPayload(Player player, ByteArrayDataInput in) {
        final String secret = in.readUTF();
        final UUID source = UUID.fromString(in.readUTF());
        final String target = in.readUTF();
        final String track = in.readUTF();
        final boolean promote = in.readBoolean();

        final PlatformPlayer sourcePlayer = PlatformUtils.getPlayer(player);
        final UUID targetUUID = PlatformUtils.getPlayerOffline(target)
                .map(PlatformUser::getUUID)
                .orElse(null);

        final Optional<PermissionUtils.RankUpdate> success = PermissionUtils.INSTANCE.modifyRank(sourcePlayer, targetUUID, track, promote);
        ByteArrayDataOutput buff = ByteStreams.newDataOutput();
        buff.writeUTF(Settings.PROMOTION_CHANNEL);
        buff.writeUTF(secret);
        buff.writeBoolean(success.isPresent());
        success.ifPresent(result -> {
            buff.writeUTF(result.target.toString());
            buff.writeUTF(result.added == null ? "null" : result.added);
            buff.writeUTF(result.removed == null ? "null" : result.removed);
        });
        player.sendPluginMessage(DiscordLink.getInstance(), Settings.ROOT_CHANNEL, buff.toByteArray());
    }

    private void handleRolesPayload(ByteArrayDataInput in) {
        final UUID target = UUID.fromString(in.readUTF());
        //noinspection ConstantConditions
        Optional.of(target)
                .map(Bukkit.getServer()::getPlayer)
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .map(PlatformUtils::getPlayer)
                .ifPresent(Utility::setRoles);
    }
}
