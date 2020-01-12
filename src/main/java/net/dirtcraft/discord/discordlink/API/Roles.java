package net.dirtcraft.discord.discordlink.API;

import net.dv8tion.jda.core.entities.Role;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;

import static net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration.Roles.*;

public enum Roles {
    OWNER   (ownerRoleID,   true, 'c',"Owner"           ),
    DIRTY   (dirtyRoleID,   true, 'f',"Manager"         ),
    ADMIN   (adminRoleID,   true, '4',"Admin"           ),
    MOD     (modRoleID  ,   true, 'b',"Moderator"       ),
    STAFF   (staffRoleID,   true, '5',"Helper"          ),
    NITRO   (nitroRoleID,   false,'a',"Nitro Booster"   ),
    DONOR   (donatorRoleID, false,'6',"Donor"           ),
    VERIFIED(verifiedRoleID,false,'7',"Verified"        ),
    NONE    (null,      false,'7',"None"            );

    private final Role id;
    private final String name;
    private final char color;
    private final boolean isStaff;

    Roles(String id, boolean isStaff, char color, @NonNull String name){
        this.id = id == null ? null : GameChat.getGuild().getRoleById(id);
        this.name = name;
        this.color = color;
        this.isStaff = isStaff;
    }

    @Nullable public Role getRole(){
        return id;
    }

    @NonNull public String getName(){
        return name;
    }

    public boolean isStaff(){
        return isStaff;
    }

    @NonNull public String getStyle(){
        return "§"+color + (this.isStaff? "§l" : "");
    }

    @NonNull public String getChevron(){
        return (color == '7' ? "§9" : "§" + color) + "§l»";
    }

}
