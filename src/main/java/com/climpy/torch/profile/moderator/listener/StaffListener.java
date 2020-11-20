package com.climpy.torch.profile.moderator.listener;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.profile.moderator.StaffManager;
import com.climpy.torch.profile.moderator.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StaffListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        VanishManager vanishManager = TorchPlugin.getInstance().getVanishManager();
        StaffManager staffManager = TorchPlugin.getInstance().getStaffManager();
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Player player = event.getPlayer();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (profile.isFrozenStatus()) {
            if (profile.isStaffMode() || profile.isStaffChat() || profile.isVanishStatus()) {
                profile.setStaffChat(false);
                profile.setVanishStatus(false);
                profile.setStaffMode(false);
                profile.saveProfile();

                staffManager.setStaffMode(profile.getUniqueUUID(), false);
                vanishManager.setVanishStatus(profile.getUniqueUUID(), false);
            }

        } else if (profile.toPlayer().hasPermission("torch.rank.mod") && (profile.isVanishStatus() || profile.isStaffMode())) {
            Bukkit.getScheduler().runTaskLater(TorchPlugin.getInstance(), ()-> {
                if (!staffManager.getStaffs().contains(profile.getUniqueUUID())) {
                    staffManager.setStaffMode(profile.getUniqueUUID(), true);
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        StaffManager staffManager = TorchPlugin.getInstance().getStaffManager();
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Player player = event.getPlayer();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (staffManager.getStaffs().contains(profile.getUniqueUUID())) {
            staffManager.setStaffMode(profile.getUniqueUUID(), false);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        StaffManager staffManager = TorchPlugin.getInstance().getStaffManager();
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Player player = (Player) event.getEntity();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (staffManager.getStaffs().contains(profile.getUniqueUUID())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        StaffManager staffManager = TorchPlugin.getInstance().getStaffManager();
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Player player = (Player) event.getWhoClicked();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (staffManager.getStaffs().contains(profile.getUniqueUUID())) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null || itemMeta.getDisplayName() == null) {
                return;
            }

            if (staffManager.hasStaffModeItem(itemStack)) {
                event.setCancelled(true);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        StaffManager staffManager = TorchPlugin.getInstance().getStaffManager();
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Player player = event.getPlayer();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (staffManager.getStaffs().contains(profile.getUniqueUUID())) {
            ItemStack itemStack = event.getItemDrop().getItemStack();
            if (itemStack == null) return;

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null || itemMeta.getDisplayName() == null) {
                return;
            }

            if (staffManager.hasStaffModeItem(itemStack)) {
                event.setCancelled(true);
            }
        }
    }
}
