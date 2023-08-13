// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.events;

import org.spongepowered.api.event.Listener;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.filter.cause.Root;
import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.channels.GameChatChannelImpl;

public class UltimateChat
{
    private GameChatChannelImpl gameChatChannel;
    
    public UltimateChat(final DiscordLink discordLink) {
        this.gameChatChannel = discordLink.getChannelManager().getGameChat();
    }
    
    @Listener
    public void onSendChannelMessage(final SendChannelMessageEvent event, @Root final Object cause) {
        if (event.getChannel() == null) {
            return;
        }
        if (!event.getChannel().getName().equalsIgnoreCase("global")) {
            return;
        }
        if (!(cause instanceof Player)) {
            return;
        }
        final Player player = (Player)cause;
        final String prefix = TextSerializers.FORMATTING_CODE.stripCodes((String)player.getOption("prefix").orElse(""));
        final String username = player.getName();
        final String message = TextSerializers.FORMATTING_CODE.stripCodes(Utility.sanitiseMinecraftText(event.getMessage().toPlain()));
        this.gameChatChannel.sendPlayerMessage(prefix, username, message);
    }
}
