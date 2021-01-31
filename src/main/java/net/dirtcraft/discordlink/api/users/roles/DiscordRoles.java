package net.dirtcraft.discordlink.api.users.roles;

import static net.dirtcraft.discordlink.storage.PluginConfiguration.Roles.*;

public interface DiscordRoles {
    DiscordRole OWNER =     new DiscordRole(()->ownerRoleID,    6,'c',"Owner"      );
    DiscordRole DIRTY =     new DiscordRole(()->dirtyRoleID,    5,'e',"Manager"    );
    DiscordRole ADMIN =     new DiscordRole(()->adminRoleID,    4,'4',"Admin"      );
    DiscordRole MOD =       new DiscordRole(()->moderatorRoleID,3,'b',"Moderator"  );
    DiscordRole HELPER =    new DiscordRole(()->helperRoleID,   2,'5',"Helper"     );
    DiscordRole STAFF =     new DiscordRole(()->staffRoleID,    1,'d',"Staff"      );
    DiscordRole NITRO =     new DiscordRole(()->nitroRoleID,    0,'a',"Nitro"      );
    DiscordRole DONOR =     new DiscordRole(()->donatorRoleID,  0,'6',"Donor"      );
    DiscordRole VERIFIED =  new DiscordRole(()->verifiedRoleID, 0,'7',"Verified"   );
    DiscordRole MUTED =     new DiscordRole(()->mutedRoleID,    0,'0',"Muted"      );
    DiscordRole NONE =      new DiscordRole(()->null,           0,'0',"Unverified" );
}
