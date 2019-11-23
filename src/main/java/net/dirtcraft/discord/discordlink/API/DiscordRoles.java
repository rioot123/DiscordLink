package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;

public enum DiscordRoles {
    OWNER,
    DIRTY,
    ADMIN,
    STAFF,
    DONOR,
    NITRO,
    VERIFIED;

    public String getID(){
        switch (this) {
            case OWNER: return PluginConfiguration.Roles.ownerRoleID;
            case DIRTY: return PluginConfiguration.Roles.dirtyRoleID;
            case ADMIN: return PluginConfiguration.Roles.adminRoleID;
            case STAFF: return PluginConfiguration.Roles.staffRoleID;
            case NITRO: return PluginConfiguration.Roles.nitroRoleID;
            case DONOR: return PluginConfiguration.Roles.donatorRoleID;
            case VERIFIED: return PluginConfiguration.Roles.verifiedRoleID;
            default: return "";
        }
    }
}
