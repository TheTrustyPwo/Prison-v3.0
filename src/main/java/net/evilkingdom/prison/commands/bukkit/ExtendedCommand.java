package net.evilkingdom.prison.commands.bukkit;

import net.evilkingdom.prison.commands.CommandNode;
import net.evilkingdom.prison.commands.Commands;
import net.evilkingdom.prison.commands.parameter.ParameterData;
import net.evilkingdom.prison.commands.parameter.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtendedCommand extends Command implements PluginIdentifiableCommand {
    private final Plugin plugin;
    private final CommandNode node;

    public ExtendedCommand(@NotNull CommandNode node, @NotNull Plugin plugin) {
        super(node.getName());
        this.plugin = plugin;
        this.node = node;
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        CommandNode executionNode = this.node;
        int subCommandDepth = 0;
        for (String argument : args) {
            if (!executionNode.hasCommand(argument)) break;
            executionNode = executionNode.getCommand(argument);
            subCommandDepth++;
        }
        final String[] effectiveArguments = Arrays.copyOfRange(args, subCommandDepth, args.length);
        if (executionNode.isAsync()) {
            CommandNode finalExecutionNode = executionNode;
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> finalExecutionNode.invoke(sender, effectiveArguments));
        } else {
            executionNode.invoke(sender, effectiveArguments);
        }
        return true;
    }

    @NotNull
    @Override
    public final List<String> tabComplete(@NotNull final CommandSender sender, @NotNull String cmdLine, @NotNull String[] args) {
        CommandNode realNode = this.node;
        int subCommandDepth = 0;
        for (String argument : args) {
            if (!realNode.hasCommand(argument)) break;
            realNode = realNode.getCommand(argument);
            subCommandDepth++;
        }
        final String[] effectiveArguments = Arrays.copyOfRange(args, subCommandDepth, args.length);
        int lastArgumentIndex = Math.max(effectiveArguments.length - 1, 0);
        List<String> tabComplete = new ArrayList<>();
        for (CommandNode subNode : realNode.getChildren().values()) {
            if (subNode.canUse(sender)) tabComplete.add(subNode.getName());
        }
        if (effectiveArguments.length <= realNode.getParameters().size()) {
            ParameterData param = realNode.getParameters().get(lastArgumentIndex);
            List<String> suggestions = param.suggestions();
            if (suggestions.size() == 0 && effectiveArguments.length > 0) {
                ParameterType<?> type = Commands.getParameterType(param.type());
                tabComplete.addAll(type.tabComplete(sender, effectiveArguments[lastArgumentIndex]));
            } else {
                tabComplete.addAll(suggestions);
            }
        }
        return tabComplete;
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @NotNull
    public CommandNode getNode() {
        return node;
    }
}
