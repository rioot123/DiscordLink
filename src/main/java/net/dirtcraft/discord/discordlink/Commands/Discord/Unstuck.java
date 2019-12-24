package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Unstuck implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordSource source, String[] args, MessageReceivedEvent event) {
        Role verifiedRole = event.getGuild().getRoleById(PluginConfiguration.Roles.verifiedRoleID);
        List<Role> roles = event.getMember().getRoles();
        if (!roles.contains(verifiedRole)) {
            Utility.sendPermissionErrorMessage(event);
            return;
        }
        CompletableFuture.runAsync(() -> {

            final Optional<UserStorageService> userStorage = Sponge.getGame().getServiceManager().provide(UserStorageService.class);
            if (!userStorage.isPresent()) {
                Utility.sendResponse(event, "Could not execute the command. Please try again later or contact support for further assistance. (Err.1)");
                return;
            }

            final Optional<WorldProperties> optionalWorld = Sponge.getServer().getDefaultWorld();
            if (!optionalWorld.isPresent()) {
                Utility.sendResponse(event, "Could not execute the command. Please try again later or contact support for further assistance. (Err.2)");
                return;
            }

            final String uuid = DiscordLink.getInstance().getStorage().getUUIDfromDiscordID(event.getAuthor().getId());
            if (uuid == null) {
                Utility.sendResponse(event, "Could not execute the command as we could not find your UUID. Please try again later or contact support for further assistance.");
                return;
            }

            final Optional<User> optionalUser = userStorage.get().get(UUID.fromString(uuid));
            if (!optionalUser.isPresent()) {
                Utility.sendResponse(event, "Could not execute the command as we could not find your minecraft account. Please try again later or contact support for further assistance.");
                return;
            }

            final User user = optionalUser.get();
            final WorldProperties spawn = optionalWorld.get();

            if (user.getPlayer().isPresent()) {
                Task.builder().execute(()->{
                    user.getPlayer().get().transferToWorld(spawn.getUniqueId(), spawn.getSpawnPosition().toDouble());
                }).submit(DiscordLink.getInstance());
            } else {
                Task.builder().execute(()->{
                    user.setLocation(spawn.getSpawnPosition().toDouble(), spawn.getUniqueId());
                }).submit(DiscordLink.getInstance());
            }
            Utility.sendResponse(event, "Successfully moved " + user.getName() +  " to spawn.", 15);
        });
    }
}
