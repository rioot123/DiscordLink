package net.dirtcraft.discordlink.users.discord;

import com.google.common.collect.ImmutableList;
import net.dirtcraft.discordlink.api.users.roles.DiscordRole;
import net.dirtcraft.discordlink.api.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.api.users.roles.RoleManager;
import net.dv8tion.jda.api.JDA;

import java.util.*;

public class RoleManagerImpl extends RoleManager {
    private final Map<String, DiscordRole> nameMap = new HashMap<>();
    private final List<DiscordRole> roles = new ArrayList<>();
    private final JDA jda;

    public RoleManagerImpl(JDA jda){
        this.jda = jda;
        List<DiscordRole> predefined = Arrays.asList(
                DiscordRoles.OWNER,
                DiscordRoles.DIRTY,
                DiscordRoles.ADMIN,
                DiscordRoles.MOD,
                DiscordRoles.HELPER,
                DiscordRoles.STAFF,
                DiscordRoles.NITRO,
                DiscordRoles.DONOR,
                DiscordRoles.VERIFIED,
                DiscordRoles.MUTED
        );
        predefined.forEach(this::registerRole);
    }

    @Override
    public DiscordRole getRole(String name) {
        return nameMap.getOrDefault(name, DiscordRoles.NONE);
    }

    @Override
    public DiscordRole getRole(int ordinal) {
        return ordinal >= 0 && roles.size() < ordinal? DiscordRoles.NONE: roles.get(ordinal);
    }

    @Override
    public int getOrdinal(DiscordRole role) {
        return roles.indexOf(role);
    }

    @Override
    public List<DiscordRole> getRoles() {
        return ImmutableList.copyOf(roles);
    }

    @Override
    public void registerRole(DiscordRole role) {
        if (nameMap.containsKey(role.getName())) return;
        nameMap.put(role.getName(), role);
        roles.add(role);
        setFields(role, jda);
    }
}
