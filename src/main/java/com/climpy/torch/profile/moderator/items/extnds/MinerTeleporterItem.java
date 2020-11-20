package com.climpy.torch.profile.moderator.items.extnds;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.profile.moderator.StaffManager;
import com.climpy.torch.profile.moderator.items.StaffModeItem;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinerTeleporterItem extends StaffModeItem {

    public MinerTeleporterItem() {
        super(ItemUtils.createItem(Material.DIAMOND, C.GOLD + "Madenci Teleportasyonu", 1), 3);
    }

    @Override
    public void onCustomInteractEvent(Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
            StaffManager staffManager = TorchPlugin.getInstance().getStaffManager();
            ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
            Player player = playerInteractEvent.getPlayer();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            if (profile == null) {
                return;
            }

            if (Bukkit.getOnlinePlayers().size() <= 1) {
                player.sendMessage(ChatColor.RED + "Işınlanılacak açık oyuncu bulunamadı.");
                return;
            }

            Random random = new Random();
            List<Player> players = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId().equals(onlinePlayer.getUniqueId())) {
                    continue;
                }

                if(player.getLocation().getBlockY() < 40) {
                    players.add(onlinePlayer);
                }
            }

            if (players.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Işınlanılacak açık oyuncu bulunamadı.");
                return;
            }

            Player randomPlayer = players.get(random.nextInt(players.size()));
            Profile randomPlayerProfile = profileManager.getProfile(randomPlayer.getUniqueId());
            player.teleport(randomPlayer);
            player.sendMessage(randomPlayerProfile.getColoredName() + C.GREEN + " adlı oyuncuya ışınlandınız.");
        }
    }
}