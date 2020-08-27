package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class StopServer implements DiscordCommandExecutor {
    private final boolean gracefulExit;

    public StopServer(boolean graceful){
        this.gracefulExit = graceful;
    }

    @Override
    public void execute(GuildMember source, String[] args, MessageReceivedEvent event) {
        Utility.sendResponse(event, "Attempting to reboot the server.", 15);
        try{
            Thread.sleep(50);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        FMLCommonHandler.instance().exitJava(-1, !gracefulExit);
    }
}
