package net.dirtcraft.discord.discordlink.Storage;

import net.dirtcraft.discord.discordlink.Utility.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Settings {
    public static final String ROOT_CHANNEL = "dirtcraft:discord_link";
    public static final String ROLES_CHANNEL = "dirtcraft:set_roles";
    public static final String PROMOTION_CHANNEL = "dirtcraft:promotion";
    public static final String VERSION = "@VERSION@";
    public static final Map<String, String> STAFF_PREFIXES = Stream.of(
            new Pair<>(Permission.ROLES_MANAGER,   "&f✯"),
            new Pair<>(Permission.ROLES_ADMIN,     "&4&lA"),
            new Pair<>(Permission.ROLES_MODERATOR, "&9&lM"),
            new Pair<>(Permission.ROLES_HELPER,    "&5&lH"),
            new Pair<>(Permission.ROLES_BUILDER,   "&6&lB")
    ).collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b)->a, LinkedHashMap::new));
}
