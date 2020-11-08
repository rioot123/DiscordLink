package net.dirtcraft.discord.discordlink.Storage;

import net.md_5.bungee.api.CommandSender;

import java.util.Arrays;
import java.util.Objects;

public class Permission {
    public static final String PROMOTE_PERMISSION_GROUP_PREFIX = "discordlink.promote.";
    public static final String PROMOTE_PERMISSION = "discordlink.promote.use";

    public static boolean canModify(CommandSender sender, String... group){
        return Arrays.stream(group)
                .filter(Objects::nonNull)
                .allMatch(sender::hasPermission);
    }
}
