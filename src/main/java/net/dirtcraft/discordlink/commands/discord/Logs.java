// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import java.util.ArrayList;
import net.dirtcraft.spongediscordlib.exceptions.DiscordPermissionException;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import java.util.Arrays;
import java.io.File;
import java.nio.file.Paths;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Logs implements DiscordCommandExecutor
{
    private static final int MAX_ENTRIES = 20;
    
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) {
            this.sendLatest(source);
        }
        else if (args.get(0).matches("(?i)crash-?(log|report)?s?")) {
            this.getCrashLogs(source, (args.size() > 1) ? args.get(1) : null);
        }
        else if (args.get(0).matches("(?i)(debug|special|secret)")) {
            this.getDebug(source, (args.size() > 1) ? args.get(1) : null);
        }
        else {
            this.getLogs(source, (args.size() > 1) ? args.get(1) : null);
        }
    }
    
    private void sendLatest(final MessageSource source) {
        final File logs = Paths.get("logs", "latest.log").toFile();
        source.sendPrivateFile(logs);
    }
    
    private void getCrashLogs(final MessageSource source, final String log) {
        if (log == null) {
            final String[] logs = Paths.get("crash-reports", new String[0]).toFile().list();
            String[] newOnly = new String[20];
            if (logs == null) {
                return;
            }
            Arrays.sort(logs);
            if (logs.length < 20) {
                newOnly = logs;
            }
            else {
                System.arraycopy(logs, logs.length - 20, newOnly, 0, 20);
            }
            source.sendPrivateMessage(String.join("\n", (CharSequence[])newOnly));
        }
        else if (log.contains("\\") || log.contains("/")) {
            source.sendPrivateMessage("Filename contains illegal characters!");
        }
        else {
            final File logs2 = Paths.get("crash-reports", log).toFile();
            source.sendPrivateFile(logs2);
        }
    }
    
    private void getLogs(final MessageSource source, final String log) {
        if (log == null) {
            final String[] logs = Paths.get("logs", new String[0]).toFile().list();
            if (logs == null) {
                return;
            }
            Arrays.sort(logs);
            source.sendPrivateMessage(String.join("\n", this.getLogs(logs, false)));
        }
        else if (log.contains("\\") || log.contains("/") || !this.isLog(log)) {
            source.sendPrivateMessage("Filename contains illegal characters or is a debug log!");
        }
        else {
            final File logs2 = Paths.get("logs", log).toFile();
            source.sendPrivateFile(logs2);
        }
    }
    
    private void getDebug(final MessageSource source, final String log) throws DiscordCommandException {
        if (!source.hasRole(DiscordRoles.DIRTY)) {
            throw new DiscordPermissionException();
        }
        if (log == null) {
            final String[] logs = Paths.get("logs", new String[0]).toFile().list();
            if (logs == null) {
                return;
            }
            Arrays.sort(logs);
            source.sendPrivateMessage(String.join("\n", this.getLogs(logs, true)));
        }
        else if (log.contains("\\") || log.contains("/") || this.isLog(log)) {
            source.sendPrivateMessage("Filename contains illegal characters or is not a debug log!");
        }
        else {
            final File logs2 = Paths.get("logs", log).toFile();
            source.sendPrivateFile(logs2);
        }
    }
    
    private boolean isLog(final String file) {
        return file.equalsIgnoreCase("latest.log") || file.matches("\\d+-\\d+-\\d+(-\\d+)?\\.log\\.gz");
    }
    
    private List<String> getLogs(final String[] files, final boolean debug) {
        final List<String> logs = new ArrayList<String>();
        for (int i = files.length - 1; i >= 0 && logs.size() < 20; --i) {
            final String file = files[i];
            if (debug ^ this.isLog(file)) {
                logs.add(0, file);
            }
        }
        return logs;
    }
}
