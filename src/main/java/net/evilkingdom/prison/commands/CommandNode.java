package net.evilkingdom.prison.commands;

import com.google.common.base.Preconditions;
import net.evilkingdom.prison.commands.constants.Messages;
import net.evilkingdom.prison.commands.parameter.ParameterData;
import net.evilkingdom.prison.commands.parameter.ParameterType;
import net.evilkingdom.prison.utils.Response;
import net.evilkingdom.prison.utils.numbers.Numbers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnegative;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandNode {
    private final @NotNull String name;
    private final @NotNull Map<String, CommandNode> children;
    private @NotNull String permission;
    private @Nullable CommandNode parent;
    private @NotNull List<ParameterData> parameters;
    private @Nullable Object owningInstance;
    private @Nullable Method method;
    private @Nullable String usage;
    private boolean async;

    public CommandNode(@NotNull String name, @NotNull String permission) {
        this.name = name;
        this.permission = permission;
        this.children = new TreeMap<>();
        this.parameters = new ArrayList<>();
    }

    public void registerCommand(@NotNull CommandNode commandNode) {
        Preconditions.checkNotNull(commandNode, "CommandNode cannot be null!");
        commandNode.parent = this;
        this.children.put(commandNode.name, commandNode);
    }

    public boolean hasCommand(@NotNull final String name) {
        return this.children.containsKey(name.toLowerCase());
    }

    public CommandNode getCommand(@NotNull final String name) {
        return this.children.get(name.toLowerCase());
    }

    public boolean hasCommands() {
        return !this.children.isEmpty();
    }

    public String getRealUsage() {
        String usage = this.usage;
        if (this.usage == null || this.usage.length() == 0) {
            StringBuilder stringBuilder = new StringBuilder();

            CommandNode parent = this;
            while (parent != null && !parent.name.equals("root")) {
                stringBuilder.insert(0, parent.name).insert(0, " ");
                parent = parent.getParent();
            }
            stringBuilder.deleteCharAt(0);

            for (ParameterData parameterData : this.parameters) {
                stringBuilder.append(" ");
                if (parameterData.defaultValue().length() == 0)
                    stringBuilder.append("<")
                            .append(parameterData.type().getSimpleName().toLowerCase())
                            .append(">");
                else
                    stringBuilder.append("[")
                            .append(parameterData.type().getSimpleName().toLowerCase())
                            .append("=")
                            .append(parameterData.defaultValue())
                            .append("]");
            }

            usage = stringBuilder.toString();
        }

        return Messages.INVALID_USAGE.replace("%usage%", usage);
    }

    public boolean invoke(@NotNull CommandSender sender, @NotNull String[] arguments) {
        if (this.method == null) return false;

        if (this.permission.length() > 0 && !sender.hasPermission(this.permission)) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        int methodParamCount = this.method.getParameterCount();
        List<Object> objects = new ArrayList<>(methodParamCount);

        if (Player.class.isAssignableFrom(this.method.getParameterTypes()[0])) {
            if (sender instanceof Player player) {
                objects.add(player);
            } else {
                Response.get("only-players-command").send(sender);
                return false;
            }
        } else {
            objects.add(sender);
        }

        for (int i = 0; i < this.parameters.size(); i++) {
            ParameterData parameterData = this.parameters.get(i);
            String defaultValue = parameterData.defaultValue();

            String argument;
            if (i < arguments.length) argument = arguments[i];
            else {
                if (defaultValue.length() == 0) {
                    sender.sendMessage(getRealUsage());
                    return false;
                } else argument = defaultValue;
            }

            ParameterType<?> type = Commands.getParameterType(parameterData.type());
            assert type != null;
            Object result = type.transform(sender, argument);

            if (result == null) return false;
            if (result instanceof Number number && this.method.getParameters()[i + 1].isAnnotationPresent(Nonnegative.class)) {
                final BigDecimal bigDecimal = BigDecimal.valueOf(number.doubleValue());
                if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                    Response.get("negative-amount").replace("%amount%", Numbers.formatLetters(number.doubleValue()));
                    return false;
                }
            }

            objects.add(parameterData.index(), result);
        }

        try {
            this.method.invoke(this.owningInstance, objects.toArray());
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            sender.sendMessage(Messages.ERROR);
            return false;
        }

        return true;
    }

    public List<String> getSubCommands(@NotNull CommandSender sender) {
        ArrayList<String> commands = new ArrayList<>();
        this.children.values()
                .stream()
                .filter(commandNode -> sender.hasPermission(commandNode.permission))
                .forEach(commandNode -> commands.add(commandNode.getName()));
        return commands;
    }

    public boolean canUse(@NotNull final CommandSender sender) {
        if (this.permission.length() == 0) return true;
        return sender.hasPermission(this.permission);
    }

    public @NotNull Map<String, CommandNode> getChildren() {
        return children;
    }

    public @NotNull List<ParameterData> getParameters() {
        return parameters;
    }

    public CommandNode setParameters(@NotNull List<ParameterData> parameters) {
        this.parameters = parameters;
        return this;
    }

    public @Nullable CommandNode getParent() {
        return parent;
    }

    public CommandNode setParent(@Nullable CommandNode parent) {
        this.parent = parent;
        return this;
    }

    public @Nullable Object getOwningInstance() {
        return owningInstance;
    }

    public CommandNode setOwningInstance(@Nullable Object owningInstance) {
        this.owningInstance = owningInstance;
        return this;
    }

    public @Nullable Method getMethod() {
        return method;
    }

    public CommandNode setMethod(@Nullable Method method) {
        this.method = method;
        return this;
    }

    public @Nullable String getUsage() {
        return usage;
    }

    public CommandNode setUsage(@Nullable String usage) {
        this.usage = usage;
        return this;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getPermission() {
        return permission;
    }

    public CommandNode setPermission(@NotNull String permission) {
        this.permission = permission;
        return this;
    }

    public boolean isAsync() {
        return async;
    }

    public CommandNode setAsync(boolean async) {
        this.async = async;
        return this;
    }
}
