package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.CompletableFuture;

public class BungeeEventHandler implements Listener {
    {
        ProxyServer.getInstance().getPluginManager().registerListener(DiscordLink.getInstance(), this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        CompletableFuture.runAsync(()->{
            if (!event.getPlayer().hasPermission(Permission.ROLES_STAFF)) return;
            PlatformPlayer platformPlayer = PlatformUtils.getPlayer(event.getPlayer());
            GuildMember member = GuildMember.fromPlayerId(platformPlayer.getUUID()).orElse(null);
            if (member == null) return;
            Utility.setRoles(platformPlayer, member);
        });

        //todo update username for vote data.

    }

    @EventHandler
    public void onChat(ChatEvent event){
        if (!(event.getReceiver() instanceof Server) || event.isCommand()) return;
        if (!((Server) event.getReceiver()).getInfo().getName().equalsIgnoreCase("lobby")) return;
        Channels.sendPlayerMessage("[HUB]", event.getSender().toString(), event.getMessage());
    }
}
