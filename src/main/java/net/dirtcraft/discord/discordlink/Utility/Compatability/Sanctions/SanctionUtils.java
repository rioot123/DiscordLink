package net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions;

import net.dirtcraft.discord.discordlink.API.Channel;
import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.LiteBans.LiteBans;

public abstract class SanctionUtils {
    public static SanctionUtils INSTANCE = getInstance();
    public static String VERSION;
    public static Channel CHANNEL;

    public abstract void sanction(MessageSource source, String command, boolean bypass);

    static SanctionUtils getInstance() {
        CHANNEL = Channels.getDefaultChat();
        try{
            Class.forName("litebans.api.Events");
            VERSION = "LiteBans";
            return new LiteBans(CHANNEL);
        } catch (ClassNotFoundException e){
            VERSION = "None";
           return new SanctionUtils() {
                @Override
                public void sanction(MessageSource source, String command, boolean bypass) {
                    source.sendCommandResponse("Sanction system not loaded. Could not perform action!");
                }
            };
        }
    }
}