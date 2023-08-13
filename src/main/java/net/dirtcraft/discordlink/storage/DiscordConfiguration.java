// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.storage;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DiscordConfiguration
{
    @Setting("Discord")
    private Discord discord;
    
    public DiscordConfiguration() {
        this.discord = new Discord();
    }
    
    @ConfigSerializable
    public static class Discord
    {
        @Setting("Gamechat Channel ID")
        public static String GAMECHAT_CHANNEL_ID;
        @Setting("Server Name")
        public static String SERVER_NAME;
        @Setting("Discord Bot Token")
        public static String TOKEN;
        
        static {
            Discord.GAMECHAT_CHANNEL_ID = "";
            Discord.SERVER_NAME = "N/A";
            Discord.TOKEN = "";
        }
    }
}
