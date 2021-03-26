package net.dirtcraft.discordlink.common.users.permission.subject;

public interface PermissionResolver {
    boolean hasPermission(String permission);
}
