package net.evilkingdom.prison.commands;

import com.google.common.base.Preconditions;
import net.evilkingdom.prison.commands.bukkit.ExtendedCommand;
import net.evilkingdom.prison.commands.parameter.Param;
import net.evilkingdom.prison.commands.parameter.ParameterData;
import net.evilkingdom.prison.commands.parameter.ParameterType;
import net.evilkingdom.prison.commands.parameter.impl.*;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.currency.Currency;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Commands {

    private static final Field COMMAND_MAP_FIELD;
    private static final Field KNOWN_COMMANDS_FIELD;
    private static final Map<String, ExtendedCommand> COMMANDS;
    private static final CommandNode ROOT_NODE;
    private static final Map<Class<?>, ParameterType<?>> PARAMETER_TYPES;

    static {
        try {
            COMMAND_MAP_FIELD = SimplePluginManager.class.getDeclaredField("commandMap");
            COMMAND_MAP_FIELD.setAccessible(true);
            KNOWN_COMMANDS_FIELD = SimpleCommandMap.class.getDeclaredField("knownCommands");
            KNOWN_COMMANDS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        COMMANDS = new ConcurrentHashMap<>();
        ROOT_NODE = new CommandNode("root", "");
        PARAMETER_TYPES = new HashMap<>();
        PARAMETER_TYPES.put(boolean.class, new BooleanParameterType());
        PARAMETER_TYPES.put(int.class, new IntegerParameterType());
        PARAMETER_TYPES.put(double.class, new DoubleParameterType());
        PARAMETER_TYPES.put(long.class, new LongParameterType());
        PARAMETER_TYPES.put(Boolean.class, new BooleanParameterType());
        PARAMETER_TYPES.put(Integer.class, new IntegerParameterType());
        PARAMETER_TYPES.put(Double.class, new DoubleParameterType());
        PARAMETER_TYPES.put(Long.class, new LongParameterType());
        PARAMETER_TYPES.put(String.class, new StringParameterType());
        PARAMETER_TYPES.put(Player.class, new PlayerParameterType());
        PARAMETER_TYPES.put(Material.class, new MaterialParameterType());
        PARAMETER_TYPES.put(Enchantment.class, new EnchantmentParameterType());
        PARAMETER_TYPES.put(User.class, new UserParameterType());
        PARAMETER_TYPES.put(Currency.class, new CurrencyParameterType());
    }

    @Nullable
    public static ParameterType<?> getParameterType(@NotNull final Class<?> clazz) {
        Preconditions.checkArgument(clazz != null, "Clazz cannot be null!");
        return PARAMETER_TYPES.get(clazz);
    }

    public static void unregister(@NotNull final ExtendedCommand command) {
        Preconditions.checkArgument(command != null, "Command cannot be nulL!");
        command.unregister(getCommandMap());
    }

    public static void register(@NotNull final PluginCommand command, @NotNull final Plugin plugin) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null!");
        Preconditions.checkArgument(command != null, "Command cannot be null!");

        Set<Method> methods;
        try {
            final Method[] publicMethods = command.getClass().getMethods();
            final Method[] privateMethods = command.getClass().getDeclaredMethods();
            methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);
            Collections.addAll(methods, publicMethods);
            Collections.addAll(methods, privateMethods);
        } catch (NoClassDefFoundError e) {
            plugin.getLogger().severe(String.format("Failed to register commands for %s because %s does not exist", command.getClass(), e.getMessage()));
            return;
        }

        for (final Method method : methods) {
            final Command cmd = method.getAnnotation(Command.class);
            if (cmd == null || method.isBridge() || method.isSynthetic()) continue;
            if (method.getParameterCount() < 1 || !CommandSender.class.isAssignableFrom(method.getParameterTypes()[0])) {
                plugin.getLogger().severe(String.format("Attempted to register an invalid command method signature for %s in %s", method.toGenericString(), command.getClass()));
                continue;
            }
            method.setAccessible(true);

            final List<ParameterData> parameterData = new ArrayList<>();
            for (int index = 1; index < method.getParameterCount(); index++) {
                final Parameter parameter = method.getParameters()[index];
                final Class<?> paramType;
                if (!PARAMETER_TYPES.containsKey(paramType = parameter.getType())) {
                    plugin.getLogger().severe(String.format("Attempted to register unknown command parameter type %s for %s in %s", paramType, method.toGenericString(), command.getClass()));
                    break;
                }
                final Param param = parameter.getAnnotation(Param.class);
                if (param == null) {
                    plugin.getLogger().severe(String.format("Attempted to register unannotated command parameter %s for %s in %s", parameter.getName(), method.toGenericString(), command.getClass()));
                    break;
                }
                final ParameterData paramData = new ParameterData(index, param.defaultValue(), paramType, Arrays.asList(param.suggestions()));
                parameterData.add(paramData);
            }

            for (String alias : cmd.names()) {
                final String[] childNames = alias.strip().toLowerCase().split(" ");
                if (childNames.length == 0) continue;
                CommandNode parentNode = ROOT_NODE;
                for (final String childName : childNames) {
                    CommandNode childNode = parentNode.hasCommand(childName) ? parentNode.getCommand(childName) : new CommandNode(childName, "");
                    parentNode.registerCommand(childNode);
                    parentNode = childNode;
                }
                parentNode.setPermission(cmd.permission()).setUsage(cmd.usage()).setAsync(cmd.async())
                        .setMethod(method).setParameters(parameterData).setOwningInstance(command);

                final CommandNode mainNode = ROOT_NODE.getCommand(childNames[0]);
                final ExtendedCommand extendedCommand = new ExtendedCommand(mainNode, plugin);
                getCommandMap().register(plugin.getDescription().getName(), extendedCommand);
                getKnownCommandMap().put(plugin.getDescription().getName().toLowerCase() + ":" + mainNode.getName().toLowerCase(), extendedCommand);
                getKnownCommandMap().put(mainNode.getName().toLowerCase(), extendedCommand);
                extendedCommand.setLabel(mainNode.getName());

                COMMANDS.put(mainNode.getName().toLowerCase(), extendedCommand);
            }
        }
    }

    @NotNull
    public static ExtendedCommand get(String name) {
        return COMMANDS.get(name.toLowerCase());
    }

    @NotNull
    private static CommandMap getCommandMap() {
        try {
            return (CommandMap) COMMAND_MAP_FIELD.get(Bukkit.getServer().getPluginManager());
        } catch (Exception e) {
            throw new RuntimeException("Could not get CommandMap", e);
        }
    }

    @NotNull
    private static Map<String, org.bukkit.command.Command> getKnownCommandMap() {
        try {
            //noinspection unchecked
            return (Map<String, org.bukkit.command.Command>) KNOWN_COMMANDS_FIELD.get(getCommandMap());
        } catch (Exception e) {
            throw new RuntimeException("Could not get known commands map", e);
        }
    }
}
