// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage;

import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.stream.Stream;
import net.dirtcraft.discordlink.utility.Pair;
import java.util.Map;

public class Settings
{
    public static final String ROOT_CHANNEL = "Discord-Link";
    public static final String ROLES_CHANNEL = "set_roles";
    public static final String PROMOTION_CHANNEL = "promotion";
    public static final String VERSION = "2.0.1";
    public static final Map<String, String> STAFF_PREFIXES;
    
    static {
        STAFF_PREFIXES = Stream.of((Pair[])new Pair[] { new Pair((T)"discordlink.roles.manager", (S)"&f\u272f"), new Pair((T)"discordlink.roles.admin", (S)"&4&lA"), new Pair((T)"discordlink.roles.moderator", (S)"&9&lM"), new Pair((T)"discordlink.roles.helper", (S)"&5&lH"), new Pair((T)"discordlink.roles.builder", (S)"&6&lB") }).collect((Collector<? super Pair, ?, Map<String, String>>)Collectors.toMap((Function<? super Pair, ?>)Pair::getKey, (Function<? super Pair, ?>)Pair::getValue, (a, b) -> a, (Supplier<R>)LinkedHashMap::new));
    }
}
