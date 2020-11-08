package net.dirtcraft.discord.discordlink.Events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.PermissionUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PluginMessageHandler implements Listener {
    final Map<UUID, Callback> callbacks = new ConcurrentHashMap<>();

    public PluginMessageHandler(DiscordLink link){
        ProxyServer.getInstance().getScheduler().schedule(link, this::trimCallbacks, 5, TimeUnit.MINUTES);
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void on(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase(Settings.ROOT_CHANNEL)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        final String type = in.readUTF();
        if (type.equalsIgnoreCase(Settings.PROMOTION_CHANNEL)) handlePromoteEvent(in);
    }

    private void handlePromoteEvent(ByteArrayDataInput in){
        UUID query = UUID.fromString(in.readUTF());
        boolean response = in.readBoolean();
        PermissionUtils.RankUpdate rankUpdate;
        if (response){
            UUID target = UUID.fromString(in.readUTF());
            String added = in.readUTF();
            String removed = in.readUTF();
            rankUpdate = new PermissionUtils.RankUpdate(target, added, removed);
        } else {
            rankUpdate = null;
        }
        handleCallback(query, rankUpdate);
    }

    private void handleCallback(UUID secret, PermissionUtils.RankUpdate rankUpdate){
        final Callback callback = callbacks.remove(secret);
        if (callback != null) callback.handler.accept(rankUpdate);
    }

    public void registerCallback(UUID uuid, Consumer<PermissionUtils.RankUpdate> handler){
        callbacks.put(uuid, new Callback(handler));
    }

    public void trimCallbacks(){
        callbacks.entrySet().stream()
                .filter(p->p.getValue().canBeExpunged())
                .forEach(p->callbacks.remove(p.getKey()));
    }

    public static class Callback{
        final Consumer<PermissionUtils.RankUpdate> handler;
        final long submitted;
        private Callback(Consumer<PermissionUtils.RankUpdate> handler){
            this.handler = handler;
            this.submitted = System.currentTimeMillis();
        }

        private boolean canBeExpunged(){
            long DURATION = 1000 * 60 * 5;
            return System.currentTimeMillis() - submitted > DURATION;
        }
    }
}
