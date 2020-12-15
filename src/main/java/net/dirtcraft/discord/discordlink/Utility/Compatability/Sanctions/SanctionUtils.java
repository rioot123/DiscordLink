package net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions;

import net.dirtcraft.discord.discordlink.API.Channel;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.LiteBans.LiteBans;

import java.util.Timer;
import java.util.TimerTask;

public abstract class SanctionUtils {
    //protected static boolean init = false;
    protected static Timer timer = new Timer();
    public static SanctionUtils INSTANCE = getInstance();
    public static String VERSION;

    public abstract void sanction(MessageSource source, String command, boolean bypass);

    public abstract void updateChannel(long id);

    static SanctionUtils getInstance() {
        Channel CHANNEL = new Channel(PluginConfiguration.Channels.litebansChannel);
        try {
            observeMutes();
            //initDatabase();
        } catch (Exception e){
            e.printStackTrace();
        }
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

               @Override
               public void updateChannel(long id) {

               }
           };
        }
    }

    private static void observeMutes(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!PlatformUtils.isGameReady()) return;
                    DiscordLink.getInstance().getStorage().deactivateExpiredMutes();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, 1000*60*5);
    }

    /*
    private static void initDatabase(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (init || !PlatformUtils.isGameReady()) return;
                    DiscordLink.getInstance().getStorage().buildMuteTable();
                    init = true;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }
     */
}
