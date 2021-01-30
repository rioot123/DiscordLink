package net.dirtcraft.discordlink.api.exceptions;

public class DiscordCommandException extends Exception {
    private String message;
    public DiscordCommandException(){
        this.message = null;
    }
    public DiscordCommandException(String message){
        this.message = message;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
