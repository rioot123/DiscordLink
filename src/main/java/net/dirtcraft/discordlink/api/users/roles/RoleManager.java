package net.dirtcraft.discordlink.api.users.roles;

import net.dv8tion.jda.api.JDA;

import java.util.List;

public abstract class RoleManager {
    public abstract DiscordRole getRole(String name);

    public abstract DiscordRole getRole(int ordinal);

    public abstract int getOrdinal(DiscordRole role);

    public abstract List<DiscordRole> getRoles();

    public abstract void registerRole(DiscordRole role);

    protected final void setFields(DiscordRole role, JDA jda){
        role.roleManager = this;
        role.ordinal = getOrdinal(role);
        role.jda = jda;
    }
}
