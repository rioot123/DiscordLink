// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.events;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.event.Listener;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.channels.GameChatChannelImpl;

public class NormalChat
{
    private static final String ADMIN_PERM = "discordlink.chat.notify";
    private GameChatChannelImpl gameChat;
    
    public NormalChat(final DiscordLink link) {
        this.gameChat = link.getChannelManager().getGameChat();
    }
    
    @Listener
    public void onChat(final MessageChannelEvent.Chat event, @Root final Object cause) {
        if (!(cause instanceof Player)) {
            return;
        }
        final Player player = (Player)cause;
        final String prefix = TextSerializers.FORMATTING_CODE.stripCodes((String)player.getOption("prefix").orElse(""));
        final String username = player.getName();
        final String message = TextSerializers.FORMATTING_CODE.stripCodes(Utility.sanitiseMinecraftText(event.getRawMessage().toPlain()));
        this.gameChat.sendPlayerMessage(prefix, username, message);
    }
    
    private void notify(final Text msg) {
        Sponge.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission("discordlink.chat.notify")).forEach(player -> {
            player.sendMessage((Text)Text.of("The following message has been censored:"));
            player.sendMessage(msg);
        });
    }
}
