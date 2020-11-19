package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@SuppressWarnings("deprecation")
public abstract class ConsoleSource implements CommandSender, DiscordResponder {

    private CommandSender actualSource = ProxyServer.getInstance().getConsole();

    @Override
    public void sendMessages(@NotNull String... messages) {
        for (String message : messages) sendMessage(message);
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        sendMessage(BaseComponent.toPlainText(message));
    }

    @Override
    public void sendMessage(BaseComponent message) {
        sendMessage(BaseComponent.toPlainText(message));
    }

    @Override
    public String getName() {
        return actualSource.getName();
    }

    @Override
    public Collection<String> getGroups() {
        return actualSource.getGroups();
    }

    @Override
    public void addGroups(String... groups) {
        actualSource.addGroups(groups);
    }

    @Override
    public void removeGroups(String... groups) {
        actualSource.removeGroups(groups);
    }

    @Override
    public boolean hasPermission(String permission) {
        return actualSource.hasPermission(permission);
    }

    @Override
    public void setPermission(String permission, boolean value) {
        actualSource.setPermission(permission, value);
    }

    @Override
    public Collection<String> getPermissions() {
        return actualSource.getPermissions();
    }
}