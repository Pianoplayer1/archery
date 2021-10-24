package archery;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static archery.Utils.format;

public class Items {
    private final static ItemStack archeryBow, arrows, multiShotArrows, hotHighArrows, enderArrows;

    // Creates custom archery itemStacks
    static {
        archeryBow = new ItemStack(Material.BOW);
        ItemMeta archeryBowMeta = archeryBow.getItemMeta();
        assert archeryBowMeta != null;
        archeryBowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        archeryBowMeta.setDisplayName(format("&aArchery Bow"));
        archeryBowMeta.setUnbreakable(true);
        List<String> archeryBowLore = new ArrayList<>();
        archeryBowLore.add(format("&r&5Special bow for archery purposes"));
        archeryBowMeta.setLore(archeryBowLore);
        archeryBow.setItemMeta(archeryBowMeta);

        arrows = new ItemStack(Material.ARROW);
        arrows.setAmount(64);
        ItemMeta arrowsMeta = arrows.getItemMeta();
        assert arrowsMeta != null;
        List<String> arrowsLore = new ArrayList<>();
        arrowsLore.add(format("&r&5Just a normal arrow"));
        arrowsMeta.setLore(arrowsLore);
        arrows.setItemMeta(arrowsMeta);

        multiShotArrows = prepareArrowStack("&bMulti-shot arrow", Color.AQUA,
                "&r&5This arrow is actually more than one... Try it!");
        hotHighArrows = prepareArrowStack("&9Hot n' High", Color.BLUE,
                "&r&5Despite looking blue, this arrow comes out blazing hot!",
                "&r&5In addition to that, it flies higher than every other arrow.");
        enderArrows = prepareArrowStack("&1Enderarrow", Color.NAVY,
                "&r&5I wonder what the name of this arrow could mean...");
    }

    // Returns itemStack for a special arrow
    private static @NotNull ItemStack prepareArrowStack(String name, Color color, String @NotNull ... lore) {
        ItemStack itemStack = new ItemStack(Material.TIPPED_ARROW);
        itemStack.setAmount(3);
        PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
        assert meta != null;
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setColor(color);
        meta.setDisplayName(format(name));
        List<String> formattedLore = new ArrayList<>();
        for (String line : lore) formattedLore.add(format(line));
        meta.setLore(formattedLore);
        meta.setBasePotionData(new PotionData(PotionType.AWKWARD));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    // Returns archery bow
    public static ItemStack getArcheryBow() {
        return archeryBow;
    }

    // Returns all archery items
    @Contract(value = " -> new", pure = true)
    public static ItemStack @NotNull [] getArcheryItems() {
        return new ItemStack[]{archeryBow, arrows, multiShotArrows, hotHighArrows, enderArrows};
    }
}