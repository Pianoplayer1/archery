package archery.commands;

import archery.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import static archery.Utils.format;

public record ArcheryFunCommand(Main plugin) {
    public void process(Player player, String @NotNull [] args) {
        // If no further arguments are specified, either put the player in fun mode or take them out of it
        // If there are arguments, call the respective setting function of the first argument or give an error message
        if (args.length == 0) {
            // Exit archery practice mode if it is active
            if (plugin.getSettings(player).isInPracticeMode()) plugin.endPracticeMode(player);
            // Exit archery fun mode if it is active
            else if (plugin.getSettings(player).isInFunMode()) {
                String msg = plugin.getConfig().getString("funExitMessage");
                assert msg != null;
                player.sendMessage(format(msg));
                plugin.getSettings(player).setInFunMode(false);
                return;
            }
            plugin.getSettings(player).setInFunMode(true);

            // Send messages
            String msg1 = plugin.getConfig().getString("funBeginMessage");
            assert msg1 != null;
            player.sendMessage(format(msg1));
            String msg2 = plugin.getConfig().getString("funBeginMessageSub");
            assert msg2 != null;
            TextComponent msgCommand = new TextComponent("/archery fun <property> <value>");
            msgCommand.setColor(ChatColor.GRAY);
            msgCommand.setUnderlined(true);
            msgCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click me!")));
            msgCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/archery fun "));
            String[] msg2Parts = msg2.split("%s");
            if (msg2Parts.length == 1) msg2Parts = new String[]{msg2Parts[0], ""};
            if (msg2Parts.length == 2) {
                TextComponent part1 = new TextComponent(format(msg2Parts[0]));
                TextComponent part2 = new TextComponent(format(msg2Parts[1]));
                player.spigot().sendMessage(part1, msgCommand, part2);
            }
            TextComponent practiceModeMsg1 = new TextComponent("Click here");
            practiceModeMsg1.setColor(ChatColor.GRAY);
            practiceModeMsg1.setUnderlined(true);
            practiceModeMsg1.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, new Text("Click for practice mode!")));
            practiceModeMsg1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/archery"));
            TextComponent practiceModeMsg2 = new TextComponent(" to go to archery practice mode instead.");
            practiceModeMsg2.setColor(ChatColor.GRAY);
            player.spigot().sendMessage(practiceModeMsg1, practiceModeMsg2);

            // Give items if needed and play sound
            PlayerInventory inventory = player.getInventory();
            if (!inventory.contains(Material.BOW)) inventory.addItem(new ItemStack(Material.BOW));
            if (!inventory.contains(Material.ARROW)) inventory.addItem(new ItemStack(Material.ARROW));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,
                    SoundCategory.PLAYERS, 100, 1);
        } else switch (args[0]) {
            case "amount" -> amountSetting(player, args);
            case "fire" -> fireSetting(player, args);
            case "projectile" -> projectileSetting(player, args);
            case "riding" -> ridingSetting(player, args);
            default -> player.sendMessage(format("&cIncorrect argument for archery fun command"));
        }
    }

    // Processes the command if the first argument of the command is "amount"
    private void amountSetting(Player player, String @NotNull [] args) {
        // Get the amount value from the next argument. If it is not specified or not a number, leave it at 0
        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
        }

        // Set the amount or give an error message, depending on the player's input
        if (amount >= 1 && amount <= 100) {
            plugin.getSettings(player).setAmount(amount);
            player.sendMessage(format("&6Set projectile amount to &a%d", amount));
        } else player.sendMessage(format("&cPlease specify the projectile amount in range 1 to 100"));
    }

    // Gets called if the first argument of the command is "fire"
    private void fireSetting(Player player, String @NotNull [] args) {
        // "Convert" the next argument to a boolean. If it is not specified or not a boolean value, give an error
        if (args.length < 2) args = new String[]{"", ""};
        boolean fireOn;
        switch (args[1].toLowerCase()) {
            case "true" -> fireOn = true;
            case "false" -> fireOn = false;
            default -> {
                player.sendMessage(format("&cPlease specify if fire should be on with \"true\" or \"false\""));
                return;
            }
        }

        // Set the fireOn value to the parsed argument and give a success message
        plugin.getSettings(player).setFireOn(fireOn);
        player.sendMessage(format("&6Set projectile fire to &a%b", fireOn));
    }

    // Gets called if the first argument of the command is "projectile"
    private void projectileSetting(Player player, String @NotNull [] args) {
        // "Convert" the next argument to a projectile sub-interface. If it is not "convertible", give an error
        if (args.length < 2) args = new String[]{"", ""};
        Class<? extends Projectile> projectile;
        switch (args[1].toLowerCase()) {
            case "arrow" -> projectile = Arrow.class;
            case "egg" -> projectile = Egg.class;
            case "enderpearl" -> projectile = EnderPearl.class;
            case "fireball" -> projectile = Fireball.class;
            case "snowball" -> projectile = Snowball.class;
            default -> {
                player.sendMessage(format("&cPlease specify a valid projectile!"));
                return;
            }
        }

        // Set the projectile value to the parsed argument and give a success message
        plugin.getSettings(player).setProjectile(projectile);
        player.sendMessage(format("&6Set projectile type to &a%s", args[1].toLowerCase()));
    }

    // Gets called if the first argument of the command is "riding"
    private void ridingSetting(Player player, String @NotNull [] args) {
        // "Convert" the next argument to a boolean. If it is not specified or not a boolean value, give an error
        if (args.length < 2) args = new String[]{"", ""};
        boolean ridingOn;
        switch (args[1].toLowerCase()) {
            case "true" -> ridingOn = true;
            case "false" -> ridingOn = false;
            default -> {
                player.sendMessage(format("&cPlease specify if riding should be on with \"true\" or \"false\""));
                return;
            }
        }

        // Set the ridingOn value to the parsed argument and give a success message
        plugin.getSettings(player).setRidingOn(ridingOn);
        player.sendMessage(format("&6Set riding mode to &a%b", ridingOn));
    }
}