package net.dirtcraft.discordlink.common.users.discord;

import com.google.common.collect.ImmutableList;
import net.dirtcraft.discordlink.api.util.LongSupplier;
import net.dirtcraft.discordlink.common.utility.Pair;
import net.dirtcraft.discordlink.api.users.roles.DiscordRole;
import net.dirtcraft.discordlink.api.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.api.users.roles.RoleManager;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static net.dirtcraft.discordlink.common.storage.PluginConfiguration.Roles.*;

public class RoleManagerImpl extends RoleManager {
    private final Map<String, DiscordRole> nameMap = new HashMap<>();
    private final List<DiscordRole> roles = new ArrayList<>();
    private final JDA jda;

    public RoleManagerImpl(JDA jda) {
        this.jda = jda;
        Stream.of(
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.OWNER,     () -> ownerRoleID       ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.DIRTY,     () -> dirtyRoleID       ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.ADMIN,     () -> adminRoleID       ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.MOD,       () -> moderatorRoleID   ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.HELPER,    () -> helperRoleID      ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.STAFF,     () -> staffRoleID       ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.NITRO,     () -> nitroRoleID       ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.DONOR,     () -> donatorRoleID     ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.VERIFIED,  () -> verifiedRoleID    ),
                new Pair<DiscordRole, LongSupplier>(DiscordRoles.MUTED,     () -> mutedRoleID       )
        ).forEach(role->registerRole(role.getKey(), role.getValue()));
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

    private void registerRole(DiscordRole role, LongSupplier roleSupplier) {
        if (nameMap.containsKey(role.getName())) return;
        nameMap.put(role.getName(), role);
        roles.add(role);
        setFields(role, jda, roleSupplier);
        role.reload();
    }
}
