package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class BungeeEventHandler implements Listener {
    private final Pattern finalPattern = Pattern.compile("$\\{jndi", Pattern.LITERAL);
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
            //todo update username for vote data.
        });

    }

    @EventHandler
    public void onChat(ChatEvent event){
        if (event.getSender() instanceof ProxiedPlayer) runMessageFiltration(event, (ProxiedPlayer) event.getSender());
        if (!PluginConfiguration.HubChat.enabled) return;
        if (!(event.getReceiver() instanceof Server) || event.isCommand()) return;
        if (!((Server) event.getReceiver()).getInfo().getName().equalsIgnoreCase(PluginConfiguration.HubChat.serverId)) return;
        Channels.sendPlayerMessage("[HUB]", event.getSender().toString(), event.getMessage());
    }

    @EventHandler
    public void onJoin(ServerConnectedEvent event){
        if (!PluginConfiguration.HubChat.leaveJoinMessages) return;
        if (!event.getServer().getInfo().getName().equalsIgnoreCase(PluginConfiguration.HubChat.serverId)) return;
        Channels.getDefaultChat().sendMessage(PluginConfiguration.Format.playerJoin
                .replace("{username}", event.getPlayer().getName())
                .replace("{prefix}", "")
        );
    }

    @EventHandler
    public void onLeave(ServerDisconnectEvent event){
        if (!PluginConfiguration.HubChat.leaveJoinMessages) return;
        if (!event.getTarget().getName().equalsIgnoreCase(PluginConfiguration.HubChat.serverId)) return;
        Channels.getDefaultChat().sendMessage(PluginConfiguration.Format.playerDisconnect
                .replace("{username}", event.getPlayer().getName())
                .replace("{prefix}", "")
        );
    }

    private void runMessageFiltration(ChatEvent e, ProxiedPlayer player){
        if (!PluginConfiguration.HubChat.enableFiltering) return;
        String message = e.getMessage().toLowerCase();
        if(finalPattern.matcher(message).find()){
            player.sendMessage("Not today.");
            String reason = player.getName() + " tried to use the jdni exploit";
            ProxyServer.getInstance().getLogger().info(reason);
            if (PluginConfiguration.HubChat.banSeriousFiltered) Utility.toConsole(String.format("ban %s %s", player.getUniqueId().toString(), reason));
            e.setCancelled(true);
        }
    }
}
