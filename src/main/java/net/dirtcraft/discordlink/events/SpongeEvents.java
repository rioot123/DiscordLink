// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.events;

import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.Listener;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.data.key.Keys;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.channels.GameChatChannelImpl;

public class SpongeEvents
{
    private final GameChatChannelImpl gameChatChannel;
    
    public SpongeEvents(final DiscordLink main, final Database storage) {
        this.gameChatChannel = main.getChannelManager().getGameChat();
    }
    
    @Listener
    public void onPlayerJoin(final ClientConnectionEvent.Join event, @Root final Player player) {
        Utility.setRoles(PlatformProvider.getPlayer(player));
        event.setMessageCancelled((boolean)player.get(Keys.VANISH).orElse(false));
        if (player.get(Keys.VANISH).orElse(false)) {
            return;
        }
        if (player.hasPlayedBefore()) {
            final String prefix = TextSerializers.FORMATTING_CODE.stripCodes((String)player.getOption("prefix").orElse(PlatformProvider.getPlayer(player).getPrefix().orElse("")));
            this.gameChatChannel.sendMessage(PluginConfiguration.Format.playerJoin.replace("{username}", player.getName()).replace("{prefix}", prefix));
        }
        else {
            final MessageEmbed embed = Utility.embedBuilder().setDescription((CharSequence)PluginConfiguration.Format.newPlayerJoin.replace("{username}", player.getName())).build();
            this.gameChatChannel.sendMessage(embed);
        }
    }
    
    @Listener(order = Order.POST)
    public void onPlayerDeath(final DestructEntityEvent.Death event) {
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getTargetEntity();
        if (player.get(Keys.VANISH).orElse(false)) {
            return;
        }
        final String reason = event.getMessage().toPlain();
        this.gameChatChannel.sendMessage(reason);
    }
    
    @Listener
    public void onPlayerDisconnect(final ClientConnectionEvent.Disconnect event, @Root final Player player) {
        if (player.get(Keys.VANISH).orElse(false)) {
            return;
        }
        final String prefix = TextSerializers.FORMATTING_CODE.stripCodes((String)player.getOption("prefix").orElse(""));
        this.gameChatChannel.sendMessage(PluginConfiguration.Format.playerDisconnect.replace("{username}", player.getName()).replace("{prefix}", prefix));
    }
}
