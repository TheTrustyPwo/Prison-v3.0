package net.evilkingdom.prison.modules.privatemines.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrivateMineCreateCommand implements PluginCommand {
    @Command(names = {"privatemine create", "privatemines create", "pm create", "mine create", "mines create"}, async = true)
    public void create(final @NotNull Player sender) {
        Response.get("privatemine-creating").send(sender);
        PrivateMinesHandler.getInstance().createPrivateMine(sender.getUniqueId(), "default")
                .thenAccept(result -> Response.get("privatemine-created").send(sender));
    }
}
