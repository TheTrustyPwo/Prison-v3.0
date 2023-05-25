package net.evilkingdom.prison.utils;

import net.evilkingdom.prison.Prison;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public record Response(List<String> messages, String actionBar, SoundResponse soundResponse,
                       TitleResponse titleResponse) {
    private static final ConcurrentHashMap<String, Response> RESPONSES;
    private static final YamlConfiguration RESPONSES_CONFIG;

    static {
        RESPONSES = new ConcurrentHashMap<>();
        final File file = new File(Prison.getInstance().getDataFolder(), "responses.yml");
        if (!file.exists()) Prison.getInstance().saveResource("responses.yml", false);
        RESPONSES_CONFIG = YamlConfiguration.loadConfiguration(file);
        final ConfigurationSection section = RESPONSES_CONFIG.getConfigurationSection("");
        assert section != null;
        for (String identifier : section.getKeys(false)) {
            final ConfigurationSection response = section.getConfigurationSection(identifier);
            if (response != null) RESPONSES.put(identifier, Response.from(response));
        }
    }

    @NotNull
    public static Response get(@NotNull String identifier) {
        if (RESPONSES.containsKey(identifier)) return RESPONSES.get(identifier);
        else return new Response(null, null, null, null);
    }

    @NotNull
    public static Response from(@NotNull ConfigurationSection section) {
        List<String> messages = null;
        String actionBar = null;
        SoundResponse soundResponse = null;
        TitleResponse titleResponse = null;

        if (section.contains("messages"))
            messages = Text.colorize(section.getStringList("messages"));

        if (section.contains("action-bar-message"))
            actionBar = Text.colorize(section.getString("action-bar-message"));

        if (section.contains("sound"))
            soundResponse = new SoundResponse(Sound.valueOf(section.getString("sound.sound")),
                    (float) section.getDouble("sound.volume"), (float) section.getDouble("sound.pitch"));

        if (section.contains("title"))
            titleResponse = new TitleResponse(Text.colorize(section.getString("title.title")),
                    Text.colorize(section.getString("title.subtitle")), section.getInt("title.fade-in"),
                    section.getInt("title.stay"), section.getInt("title.fade-out"));

        return new Response(messages, actionBar, soundResponse, titleResponse);
    }

    @NotNull
    public Response replace(@NotNull String target, @NotNull String replacement) {
        replacement = Text.colorize(replacement);
        final List<String> newMessages = messages == null ? null : new ArrayList<>();
        if (messages != null)
            for (final String string : messages)
                newMessages.add(string.replace(target, replacement));
        final String newActionBar = actionBar == null ? null : actionBar.replace(target, replacement);
        final TitleResponse newTitleResponse = titleResponse == null ? null : titleResponse.replace(target, replacement);
        return new Response(newMessages, newActionBar, soundResponse, newTitleResponse);
    }

    public void send(@NotNull CommandSender sender) {
        if (messages != null && !messages.isEmpty())
            sender.sendMessage(String.join("\n", messages));
        if (sender instanceof Player player) {
            if (actionBar != null)
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
            if (soundResponse != null)
                soundResponse.play(player);
            if (titleResponse != null)
                titleResponse.send(player);
        }
    }

    private record SoundResponse(Sound sound, float volume, float pitch) {
        public void play(@NotNull Player player) {
            player.playSound(player, sound, volume, pitch);
        }
    }

    private record TitleResponse(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        public void send(@NotNull Player player) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }

        @NotNull
        public TitleResponse replace(@NotNull CharSequence target, @NotNull CharSequence replacement) {
            final String newTitle = title.replace(target, replacement);
            final String newSubtitle = subtitle.replace(target, replacement);
            return new TitleResponse(newTitle, newSubtitle, fadeIn, stay, fadeOut);
        }
    }
}
