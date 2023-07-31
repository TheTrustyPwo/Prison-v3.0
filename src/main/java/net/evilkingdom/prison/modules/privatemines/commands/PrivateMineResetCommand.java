package net.evilkingdom.prison.modules.privatemines.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrivateMineResetCommand implements PluginCommand {
    @Command(names = {"privatemine reset", "privatemines reset", "pm reset", "mine reset", "mines reset"}, async = true)
    public void reset(final @NotNull Player sender) {
        PrivateMinesHandler.getInstance().getPrivateMineAsync(sender.getUniqueId()).thenAccept(privateMine -> {
            if (privateMine.isEmpty()) {
                Response.get("no-privatemine").send(sender);
                return;
            }
            privateMine.get().reset().whenComplete((resetSuccessful, throwable) -> Response.get("privatemine-reset").send(sender));
        });
    }
}
