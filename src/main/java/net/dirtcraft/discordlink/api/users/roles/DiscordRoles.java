package net.dirtcraft.discordlink.api.users.roles;

public interface DiscordRoles {
    DiscordRole OWNER =     new DiscordRole(()->-1,6,'c',"Owner"      );
    DiscordRole DIRTY =     new DiscordRole(()->-1,5,'e',"Manager"    );
    DiscordRole ADMIN =     new DiscordRole(()->-1,4,'4',"Admin"      );
    DiscordRole MOD =       new DiscordRole(()->-1,3,'b',"Moderator"  );
    DiscordRole HELPER =    new DiscordRole(()->-1,2,'5',"Helper"     );
    DiscordRole STAFF =     new DiscordRole(()->-1,1,'d',"Staff"      );
    DiscordRole NITRO =     new DiscordRole(()->-1,0,'a',"Nitro"      );
    DiscordRole DONOR =     new DiscordRole(()->-1,0,'6',"Donor"      );
    DiscordRole VERIFIED =  new DiscordRole(()->-1,0,'7',"Verified"   );
    DiscordRole MUTED =     new DiscordRole(()->-1,0,'0',"Muted"      );
    DiscordRole NONE =      new DiscordRole(()->-1,0,'0',"Unverified" );
}
