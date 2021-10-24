package archery.events;

import archery.Main;
import archery.PlayerSettings;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static archery.Items.getArcheryBow;
import static archery.Utils.format;

public record OnProjectileLaunched(Main plugin) implements Listener {

    @EventHandler
    public void onProjectileLaunched(@NotNull ProjectileLaunchEvent e) {
        // Only proceed if shot projectile is an arrow
        if (!(e.getEntity() instanceof Arrow arrow) || !(e.getEntity().getShooter() instanceof Player player)) return;
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        // "Modify" projectile if player is in fun mode, else proceed
        if (plugin.getSettings(player).isInFunMode()) {
            PlayerSettings s = plugin.getSettings(player);
            Projectile p = arrow;
            Random r = new Random();
            for (int i = 1; i < s.getAmount() + 1; i++) {
                Vector v = arrow.getVelocity();
                if (s.getAmount() > 1)
                    v.setX(v.getX() * (1 + r.nextDouble())).setY(v.getY() * (0.5 + r.nextDouble()))
                            .setZ(v.getZ() * (1 + r.nextDouble()));
                if (s.getProjectile().equals(Arrow.class))
                    p = arrow.getWorld().spawnArrow(arrow.getLocation(), v, 3, 10);
                else
                    p = player.launchProjectile((Class<? extends Projectile>) s.getProjectile(), v);
                if (s.isFireOn()) p.setFireTicks(100);
                p.setShooter(player);
                if (p instanceof Arrow a) a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
            if (s.isRidingOn()) p.addPassenger(player);
            arrow.remove();
            return;
        }

        // Only proceed if shot arrow is a custom special arrow
        Color color;
        PotionType potion;
        World world;
        try {
            color = arrow.getColor();
            potion = arrow.getBasePotionData().getType();
            world = arrow.getWorld();
        } catch (IllegalArgumentException exc) {
            return;
        }
        if (!potion.equals(PotionType.AWKWARD)) return;

        // "Modify" special arrow if it was shot in practice mode from the archery bow, else give an error message
        if (plugin.getSettings(player).isInPracticeMode()
                && player.getInventory().getItemInMainHand().equals(getArcheryBow())) {
            // Remove shot arrow from inventory, will not happen automatically because original arrow gets deleted
            // Actually removes the first tipped arrow of the shot arrow's color with "awkward" effect in the
            //   inventory as there is no way to get the original item stack of the shot arrow
            for (ItemStack s : player.getInventory().getContents())
                if (s != null && s.getType().equals(Material.TIPPED_ARROW)) {
                    PotionMeta meta = (PotionMeta) s.getItemMeta();
                    assert meta != null;
                    Color itemColor = meta.getColor();
                    assert itemColor != null;
                    if (meta.getBasePotionData().getType().equals(PotionType.AWKWARD)
                            && itemColor.equals(color)) s.setAmount(s.getAmount() - 1);
                }

            // Replace shot arrow with custom projectile(s) depending on shot arrow's color
            if (color.equals(Color.AQUA)) for (int i = 1; i < 4; i++) {
                Arrow a = world.spawnArrow(arrow.getLocation(), arrow.getVelocity(), 3f, 2 * i);
                a.setShooter(player);
                a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
            else if (color.equals(Color.BLUE)) {
                Vector v = arrow.getVelocity();
                v.setX(v.getX() * 3).setZ(v.getZ() * 3);
                Arrow a = player.launchProjectile(Arrow.class, v);
                a.setFireTicks(100);
                a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            } else if (color.equals(Color.NAVY))
                player.launchProjectile(EnderPearl.class, arrow.getVelocity()).setShooter(player);
        } else {
            String msg = plugin.getConfig().getString("specialArrowErrorMessage");
            assert msg != null;
            player.sendMessage(format(msg));
        }
        arrow.remove();
    }
}