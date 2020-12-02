package net.dirtcraft.discord.discordlink.Commands.Sources;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class ConsoleSource implements ConsoleCommandSender {

    private ConsoleCommandSender actualSource = Bukkit.getConsoleSender();

    ConsoleSource(){
    }

    @Override
    public void sendMessage(@NotNull String[] messages) {
        for (String message : messages) sendMessage(message);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        sendMessage(message);
    }

    @Override
    public @NotNull Server getServer() {
        return actualSource.getServer();
    }

    @Override
    public @NotNull String getName() {
        return actualSource.getName();
    }

    @Override
    public boolean isConversing() {
        return actualSource.isConversing();
    }

    @Override
    public void acceptConversationInput(@NotNull String input) {
        actualSource.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return actualSource.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        actualSource.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
        actualSource.abandonConversation(conversation, details);
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return actualSource.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return actualSource.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return actualSource.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return actualSource.hasPermission(perm);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return actualSource.addAttachment(plugin, name, value);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return actualSource.addAttachment(plugin);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return actualSource.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return actualSource.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        actualSource.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        actualSource.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return actualSource.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return actualSource.isOp();
    }

    @Override
    public void setOp(boolean value) {
        actualSource.setOp(value);
    }
}