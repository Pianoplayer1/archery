package archery.commands;

import archery.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import static archery.Items.getArcheryItems;
import static archery.Utils.format;
import static archery.Utils.getKey;

public record ArcheryCommand(Main plugin) {
    public void process(Player player) {
        // Exit archery fun mode if it is active
        if (plugin.getSettings(player).isInFunMode()) plugin.getSettings(player).setInFunMode(false);
        // Exit archery practice mode if it is active
        else if (plugin.getSettings(player).isInPracticeMode()) {
            String msg = plugin.getConfig().getString("practiceExitMessage");
            assert msg != null;
            player.sendMessage(format(msg));
            plugin.endPracticeMode(player);
            return;
        }

        // Send messages
        String msg1 = plugin.getConfig().getString("practiceBeginMessage");
        String msg2 = plugin.getConfig().getString("practiceBeginMessageSub");
        assert msg1 != null;
        assert msg2 != null;
        TextComponent funModeMsg1 = new TextComponent("Click here");
        funModeMsg1.setColor(ChatColor.GRAY);
        funModeMsg1.setUnderlined(true);
        funModeMsg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click for fun mode!")));
        funModeMsg1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/archery fun"));
        TextComponent funModeMsg2 = new TextComponent(" to go to archery fun mode instead.");
        funModeMsg2.setColor(ChatColor.GRAY);
        player.sendMessage(format(msg1));
        player.sendMessage(format(msg2));
        player.spigot().sendMessage(funModeMsg1, funModeMsg2);

        // Give items and play sound
        player.getInventory().addItem(getArcheryItems());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 100, 1);

        // (Re-)set scores
        plugin.getSettings(player).setScore(0);
        plugin.getSettings(player).setStartTime(System.currentTimeMillis());
        plugin.getSettings(player).setInPracticeMode(true);

        // Prepare boss bar
        if (plugin.getConfig().getBoolean("bossBar")) {
            KeyedBossBar bossBar = plugin.getServer().getBossBar(getKey(player));
            if (bossBar == null) bossBar = plugin.getServer().createBossBar(getKey(player),
                    "Archery score", BarColor.GREEN, BarStyle.SEGMENTED_10);
            bossBar.setProgress(0);
            bossBar.addPlayer(player);
            bossBar.setVisible(true);
        }

        // Prepare scoreboard and archery objective
        if (plugin.getConfig().getBoolean("scoreboard")) {
            ScoreboardManager manager = plugin.getServer().getScoreboardManager();
            assert manager != null;
            Scoreboard scoreboard = player.getScoreboard();
            scoreboard = scoreboard.equals(manager.getMainScoreboard()) ? manager.getNewScoreboard() : scoreboard;
            player.setScoreboard(scoreboard);

            Objective obj = scoreboard.getObjective("archery");
            if (obj != null) obj.unregister();
            obj = scoreboard.registerNewObjective("archery", "dummy", format("&6&lArchery Times"));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore("Targets : ⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜").setScore(10);
        }
    }
}