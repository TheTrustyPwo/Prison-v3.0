package net.evilkingdom.prison.modules.privatemines.commands.admin;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.commands.parameter.Param;
import net.evilkingdom.commons.utilities.numbers.NumberFormatType;
import net.evilkingdom.commons.utilities.numbers.Numbers;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;

public class PrivateMineSetSizeCommand implements PluginCommand {
    @Command(names = {"privatemine setsize", "privatemines setsize", "pm setsize", "mine setsize", "mines setsize"}, async = true)
    public void setSize(final @NotNull CommandSender sender, final @Param Player player, final @Param @Nonnegative int size) {
        PrivateMinesHandler.getInstance().getPrivateMineAsync(player.getUniqueId()).thenAccept(privateMine -> {
            if (privateMine.isEmpty()) {
                Response.get("target-no-privatemine").replace("%player%", player.getName()).send(sender);
                return;
            }

            if (size < 3 || size > privateMine.get().getTheme().getMaxMineSize() || size % 2 == 0) {
                Response.get("privatemine-invalid-size").replace("%max%",
                                Numbers.format(privateMine.get().getTheme().getMaxMineSize(), NumberFormatType.LETTERS))
                        .send(sender);
                return;
            }

            privateMine.get().updateSize(size).whenComplete((updateSuccessful, throwable) -> {
                if (!updateSuccessful) return;
                Response.get("privatemine-setsize").replace("%player%", player.getName())
                        .replace("%size%", Numbers.format(size, NumberFormatType.LETTERS))
                        .send(sender);
            });
        });
    }
}
