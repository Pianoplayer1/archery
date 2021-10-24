package archery.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArcheryTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("archery")) return List.of();

        Map<String, List<String>> funArguments = new HashMap<>();
        funArguments.put("amount", List.of());
        funArguments.put("fire", List.of("true", "false"));
        funArguments.put("projectile", List.of("arrow", "egg", "enderpearl", "fireball", "snowball"));
        funArguments.put("riding", List.of("true", "false"));

        List<String> result = new ArrayList<>();
        if (args.length == 1 && "fun".startsWith(args[0])) result.add("fun");
        else if (args.length > 1 && args[0].equals("fun"))
            for (String key : funArguments.keySet()) {
                if (args.length == 2 && key.startsWith(args[1])) result.add(key);
                else if (args.length > 2 && args[1].equals(key))
                    for (String entry : funArguments.get(key))
                        if (args.length == 3 && entry.startsWith(args[2])) result.add(entry);
            }
        return result;
    }
}