package net.evilkingdom.prison.modules.users.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.commands.parameter.Param;
import net.evilkingdom.prison.modules.users.menu.ProfileMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProfileCommand implements PluginCommand {
    @Command(names = "profile")
    public void execute(@NotNull final CommandSender sender, @Param(defaultValue = "self") final Player player) {
        sender.sendMessage("TEST");
        new ProfileMenu().open((Player) sender);
    }
}
