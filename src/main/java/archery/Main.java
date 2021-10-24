package archery;

import archery.commands.ArcheryCommand;
import archery.commands.ArcheryFunCommand;
import archery.commands.ArcheryTabCompleter;
import archery.events.OnProjectileHit;
import archery.events.OnProjectileLaunched;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static archery.Utils.getKey;

public final class Main extends JavaPlugin {
    private final Map<Player, PlayerSettings> playerSettings = new HashMap<>();

    // Returns archery settings for a given player
    public PlayerSettings getSettings(Player player) {
        if (!playerSettings.containsKey(player)) playerSettings.put(player, new PlayerSettings());
        return playerSettings.get(player);
    }

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(new OnProjectileHit(this), this);
        getServer().getPluginManager().registerEvents(new OnProjectileLaunched(this), this);

        // Register archery command and tab completer
        PluginCommand archeryCmd = getCommand("archery");
        assert archeryCmd != null;
        archeryCmd.setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player p)) return false;
            if (args.length == 0 || !args[0].equals("fun")) new ArcheryCommand(this).process(p);
            else new ArcheryFunCommand(this).process(p, Arrays.copyOfRange(args, 1, args.length));
            return true;
        });
        archeryCmd.setTabCompleter(new ArcheryTabCompleter());

        // Initialize config file
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    // Takes a given player out of practice mode
    public void endPracticeMode(Player player) {
        getSettings(player).setInPracticeMode(false);

        // Clear special archery items
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null) continue;

            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();
            if (lore != null && lore.get(0).startsWith("\u00A75")) player.getInventory().remove(itemStack);
        }

        // Hide boss bar if it exists
        KeyedBossBar bossBar = getServer().getBossBar(getKey(player));
        if (bossBar != null) bossBar.setVisible(false);

        // Remove scoreboard objective if it exists
        Objective obj = player.getScoreboard().getObjective("archery");
        if (obj != null) obj.unregister();
    }
}