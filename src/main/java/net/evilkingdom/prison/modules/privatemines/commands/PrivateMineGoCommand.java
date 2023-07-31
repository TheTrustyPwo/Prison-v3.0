package net.evilkingdom.prison.modules.privatemines.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrivateMineGoCommand implements PluginCommand {
    @Command(names = {"privatemine go", "privatemines go", "pm go", "mine go", "mines go"}, async = true)
    public void go(final @NotNull Player sender) {
        PrivateMinesHandler.getInstance().getPrivateMineAsync(sender.getUniqueId()).thenAccept(privateMine -> {
            if (privateMine.isEmpty()) {
                Response.get("no-privatemine").send(sender);
                return;
            }
            Bukkit.getScheduler().runTask(Prison.getInstance(), () -> sender.teleport(privateMine.get().getSpawn()));
            Response.get("privatemine-go").send(sender);
        });
    }
}
