package net.evilkingdom.prison.modules.privatemines.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.modules.privatemines.menus.PrivateMineMainMenu;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrivateMineCommand implements PluginCommand {
    @Command(names = {"privatemine", "privatemines", "pm", "mine", "mines"}, async = true)
    public void privatemine(final @NotNull Player sender) {
        PrivateMinesHandler.getInstance().getPrivateMineAsync(sender.getUniqueId()).thenAccept(privateMine -> {
            if (privateMine.isEmpty()) {
                Response.get("no-privatemine").send(sender);
                return;
            }
            Bukkit.getScheduler().runTask(Prison.getInstance(), () -> new PrivateMineMainMenu().open(sender));
        });
    }
}
