package net.evilkingdom.prison.modules.users.commands.parameters;

import net.evilkingdom.commons.commands.parameter.ParameterType;
import net.evilkingdom.commons.utilities.text.Text;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.UsersHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserParameterType implements ParameterType<User> {
    @Override
    public @Nullable User transform(@NotNull CommandSender sender, @NotNull String source) {
        if (source.equals("self") && sender instanceof Player player) {
            return UsersHandler.getInstance().getUser(player.getUniqueId());
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(source);
        } catch (IllegalArgumentException ignored) {
            Player player = Bukkit.getPlayer(source);
            if (player != null) uuid = player.getUniqueId();
        }

        if (uuid == null) {
            sender.sendMessage(Text.colorize("&c&l(!) &cInvalid Player"));
            return null;
        }

        if (UsersHandler.getInstance().isUserLoaded(uuid)) {
            return UsersHandler.getInstance().getUser(uuid);
        }

        return UsersHandler.getInstance().getOrLoadAndCacheUser(uuid);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String source) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(string -> string.toLowerCase().startsWith(source.toLowerCase()))
                .collect(Collectors.toList());
    }
}
