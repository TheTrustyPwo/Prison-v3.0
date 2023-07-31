package net.evilkingdom.prison.modules.privatemines.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.commands.parameter.Param;
import net.evilkingdom.commons.utilities.numbers.NumberFormatType;
import net.evilkingdom.commons.utilities.numbers.Numbers;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesModule;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrivateMineWhitelistCommand implements PluginCommand {
    @Command(names = {"privatemine whitelist", "privatemines whitelist", "pm whitelist", "mine whitelist", "mines whitelist"}, async = true)
    public void whitelist(final @NotNull Player sender, final @Param Player target) {
        PrivateMinesHandler.getInstance().getPrivateMineAsync(sender.getUniqueId()).thenAccept(privateMine -> {
            if (privateMine.isEmpty()) {
                Response.get("no-privatemine").send(sender);
                return;
            }

            final int maxWhitelist = PrivateMinesModule.getInstance().getConfig().getInt("max-whitelist");
            if (privateMine.get().isWhitelisted(target.getUniqueId())) {
                Response.get("privatemine-already-whitelisted").replace("%player%", target.getName()).send(sender);
            } else if (privateMine.get().getWhitelisted().size() >= maxWhitelist) {
                Response.get("privatemine-max-whitelisted").replace("%amount%", String.valueOf(maxWhitelist)).send(sender);
            } else {
                privateMine.get().whitelistPlayer(target.getUniqueId());
                Response.get("privatemine-whitelist").replace("%player%", target.getName()).send(sender);
            }
        });
    }
}
