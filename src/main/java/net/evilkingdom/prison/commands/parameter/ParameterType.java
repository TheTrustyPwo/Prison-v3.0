package net.evilkingdom.prison.commands.parameter;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ParameterType<T> {
    @Nullable
    T transform(@NotNull CommandSender sender, @NotNull String source);

    @NotNull
    List<String> tabComplete(@NotNull CommandSender sender, @NotNull String source);
}
