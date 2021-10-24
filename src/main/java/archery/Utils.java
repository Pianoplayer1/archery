package archery;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Utils {
    // Shortcut for chat color translation with additional arguments
    public static String format(String message, Object... args) {
        return String.format(ChatColor.translateAlternateColorCodes('&', message), args);
    }

    // Shortcut for Strings of a repeated sequence
    public static @NotNull String repeatedString(String sequence, int repeats) {
        return new String(new char[repeats]).replace("\0", sequence);
    }

    // Shortcut name-spaced key generation based on a player's name
    public static NamespacedKey getKey(@NotNull Player player) {
        return NamespacedKey.fromString(player.getName().toLowerCase());
    }
}