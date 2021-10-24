package archery.events;

import archery.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import static archery.Utils.*;

public record OnProjectileHit(Main plugin) implements Listener {

    @EventHandler
    public void onProjectileHit(@NotNull ProjectileHitEvent e) {
        // Only proceed if hit block matches desired target and player is in practice mode
        Material validTarget = Material.JACK_O_LANTERN;
        Block b = e.getHitBlock();
        if (b == null || b.getType() != validTarget || !(e.getEntity().getShooter() instanceof Player player)
                || !plugin.getSettings(player).isInPracticeMode()) return;

        // Update player's score
        int score = plugin.getSettings(player).getScore() + 1;
        int time = Math.round((System.currentTimeMillis() - plugin.getSettings(player).getStartTime()) / 1000f);
        plugin.getSettings(player).setScore(score);

        // Show score in action bar if configured so
        if (plugin.getConfig().getBoolean("actionBar")) {
            String msg = plugin.getConfig().getString("actionBarMessage");
            assert msg != null;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(format(msg, score, 10)));
        }

        // Update score in boss bar if it exists
        KeyedBossBar bossBar = plugin.getServer().getBossBar(getKey(player));
        if (bossBar != null) bossBar.setProgress(score / 10f);

        // Add time to scoreboard objective if it exists
        Scoreboard sb = player.getScoreboard();
        Objective obj = sb.getObjective("archery");
        if (obj != null) {
            obj.getScore(format("Target %d: %d second%s", score, time, time == 1 ? "" : "s"))
                    .setScore(10 - score);
            sb.resetScores(format("Targets : %s%s", repeatedString("⬛", score - 1),
                    repeatedString("⬜", 11 - score)));
            obj.getScore(format("Targets : %s%s", repeatedString("⬛", score),
                    repeatedString("⬜", 10 - score))).setScore(10);
        }

        // Print results and return if player is done
        if (score >= 10) {
            if (time < plugin.getSettings(player).getHighscore()) {
                plugin.getSettings(player).setHighscore(time);
                player.sendTitle(format("&2&lNew personal Highscore"), format("&6Good job, you were " +
                        "faster than... well... your former self!"), 10, 80, 10);
            }
            String msg1 = plugin.getConfig().getString("practiceDoneMessage");
            assert msg1 != null;
            player.sendMessage(format(msg1, time));
            String msg2 = plugin.getConfig().getString("practiceDoneMessageSub");
            assert msg2 != null;
            player.sendMessage(format(msg2, plugin.getSettings(player).getHighscore()));
            plugin.endPracticeMode(player);

            // Spawn firework at the player's location
            FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(Color.GREEN)
                    .withFade(Color.LIME).with(FireworkEffect.Type.BURST).trail(true).build();

            for (int i = 0; i < 5; i++) {
                Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                meta.setPower(i);
                firework.setFireworkMeta(meta);
            }
        }
    }
}