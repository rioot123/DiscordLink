package net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.LiteBans.LiteBans;

public abstract class SanctionUtils {
    public static SanctionUtils INSTANCE = getInstance();

    public abstract void sanction(MessageSource source, String command, boolean bypass);

    static SanctionUtils getInstance() {
        try{
            Class.forName("litebans.api.Events");
            return new LiteBans();
        } catch (ClassNotFoundException e){
           return new SanctionUtils() {
                @Override
                public void sanction(MessageSource source, String command, boolean bypass) {
                    source.sendCommandResponse("Sanction system not loaded. Could not perform action!");
                }
            };
        }
    }
}
