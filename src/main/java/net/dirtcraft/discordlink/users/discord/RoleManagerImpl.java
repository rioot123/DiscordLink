// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.discord;

import net.dirtcraft.discordlink.storage.PluginConfiguration;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import java.util.stream.Stream;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.utility.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.api.JDA;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import java.util.Map;
import net.dirtcraft.spongediscordlib.users.roles.RoleManager;

public class RoleManagerImpl extends RoleManager
{
    private final Map<String, DiscordRole> nameMap;
    private final List<DiscordRole> roles;
    private final JDA jda;
    
    public RoleManagerImpl(final JDA jda) {
        this.nameMap = new HashMap<String, DiscordRole>();
        this.roles = new ArrayList<DiscordRole>();
        this.jda = jda;
        Stream.of((Pair[])new Pair[] { new Pair((T)DiscordRoles.OWNER, () -> PluginConfiguration.Roles.ownerRoleID), new Pair((T)DiscordRoles.DIRTY, () -> PluginConfiguration.Roles.dirtyRoleID), new Pair((T)DiscordRoles.ADMIN, () -> PluginConfiguration.Roles.adminRoleID), new Pair((T)DiscordRoles.MOD, () -> PluginConfiguration.Roles.moderatorRoleID), new Pair((T)DiscordRoles.HELPER, () -> PluginConfiguration.Roles.helperRoleID), new Pair((T)DiscordRoles.STAFF, () -> PluginConfiguration.Roles.staffRoleID), new Pair((T)DiscordRoles.NITRO, () -> PluginConfiguration.Roles.nitroRoleID), new Pair((T)DiscordRoles.DONOR, () -> PluginConfiguration.Roles.donatorRoleID), new Pair((T)DiscordRoles.VERIFIED, () -> PluginConfiguration.Roles.verifiedRoleID), new Pair((T)DiscordRoles.MUTED, () -> PluginConfiguration.Roles.mutedRoleID) }).forEach(role -> this.registerRole(role.getKey(), (DiscordRole.RoleSupplier)role.getValue()));
    }
    
    public DiscordRole getRole(final String name) {
        return this.nameMap.getOrDefault(name, DiscordRoles.NONE);
    }
    
    public DiscordRole getRole(final int ordinal) {
        return (ordinal >= 0 && this.roles.size() < ordinal) ? DiscordRoles.NONE : this.roles.get(ordinal);
    }
    
    public int getOrdinal(final DiscordRole role) {
        return this.roles.indexOf(role);
    }
    
    public List<DiscordRole> getRoles() {
        return (List<DiscordRole>)ImmutableList.copyOf((Collection)this.roles);
    }
    
    public void registerRole(final DiscordRole role) {
        if (this.nameMap.containsKey(role.getName())) {
            return;
        }
        this.nameMap.put(role.getName(), role);
        this.roles.add(role);
        this.setFields(role, this.jda);
    }
    
    private void registerRole(final DiscordRole role, final DiscordRole.RoleSupplier roleSupplier) {
        if (this.nameMap.containsKey(role.getName())) {
            return;
        }
        this.nameMap.put(role.getName(), role);
        this.roles.add(role);
        this.setFields(role, this.jda, roleSupplier);
        role.reload();
    }
}
