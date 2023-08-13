// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sponge.prefix;

import java.util.Optional;
import io.github.nucleuspowered.nucleus.api.service.NucleusNicknameService;
import org.spongepowered.api.text.serializer.TextSerializers;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.entity.living.player.User;
import javax.annotation.Nonnull;
import net.dirtcraft.discordlink.commands.sources.ConsoleSource;

public class Test extends Set
{
    @Override
    protected void setPrefix(@Nonnull final ConsoleSource src, final User target, final String prefix) {
        final Text name = NucleusAPI.getNicknameService().flatMap(u -> u.getNickname(target)).orElse((Text)Text.of(target.getName()));
        src.sendMessage(Text.of(new Object[] { TextSerializers.FORMATTING_CODE.deserialize(prefix), Text.of(" "), name }));
    }
}
