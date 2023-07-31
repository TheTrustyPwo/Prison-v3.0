package net.evilkingdom.prison.modules.privatemines.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrivateMineToggleCommand implements PluginCommand {
    @Command(names = {"privatemine toggle", "privatemines toggle", "pm toggle", "mine toggle", "mines toggle"}, async = true)
    public void toggle(final @NotNull Player sender) {
        PrivateMinesHandler.getInstance().getPrivateMineAsync(sender.getUniqueId()).thenAccept(privateMine -> {
            if (privateMine.isEmpty()) {
                Response.get("no-privatemine").send(sender);
                return;
            }

            if (privateMine.get().isPublic()) {
                privateMine.get().setPublic(false);
                Response.get("privatemine-toggle-private").send(sender);
            } else {
                privateMine.get().setPublic(true);
                Response.get("privatemine-toggle-public").send(sender);
            }
        });
    }
}
