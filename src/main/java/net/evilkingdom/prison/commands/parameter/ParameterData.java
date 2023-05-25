package net.evilkingdom.prison.commands.parameter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ParameterData(
        int index,

        @NotNull String defaultValue,

        @NotNull Class<?> type,

        @NotNull List<String> suggestions
) {
}
