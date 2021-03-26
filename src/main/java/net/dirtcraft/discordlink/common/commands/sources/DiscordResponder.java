package net.dirtcraft.discordlink.common.commands.sources;


import java.util.function.Consumer;

public interface DiscordResponder {
    void sendDiscordResponse(String message);

    int getCharLimit();

    boolean sanitise();

    static DiscordResponder getSender(Consumer<String> output, int limit, boolean sanitise) {
        return new DiscordResponder() {
            @Override
            public void sendDiscordResponse(String message) {
                output.accept(message);
            }

            @Override
            public int getCharLimit(){
                return limit;
            }

            @Override
            public boolean sanitise(){
                return sanitise;
            }
        };
    }

    static ConsoleSender getSender(DiscordResponder console){
        return console instanceof ConsoleSender? (ConsoleSender) console : new ConsoleSender(console);
    }

    class ConsoleSender implements DiscordResponder, ConsoleSource {
        final DiscordResponder scheduledSender;
        protected ConsoleSender(DiscordResponder scheduledSender){
            this.scheduledSender = scheduledSender;
        }

        @Override
        public void sendMessage(String message) {
            ResponseScheduler.submit(this, message);
        }

        @Override
        public void sendDiscordResponse(String message) {
            scheduledSender.sendDiscordResponse(message);
        }

        @Override
        public int getCharLimit() {
            return scheduledSender.getCharLimit();
        }

        @Override
        public boolean sanitise() {
            return scheduledSender.sanitise();
        }
    }
}
