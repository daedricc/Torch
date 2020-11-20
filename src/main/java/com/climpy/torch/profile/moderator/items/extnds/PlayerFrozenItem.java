package com.climpy.torch.profile.moderator.items.extnds;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.profile.moderator.items.StaffModeItem;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerFrozenItem extends StaffModeItem {

    public PlayerFrozenItem() {
        super(ItemUtils.createItem(Material.CARPET, "&6Oyuncuyu Dondur", 1), 2);
    }

    @Override
    public void onCustomInteractEvent(Event event) {
        if (event instanceof PlayerInteractEntityEvent) {
            PlayerInteractEntityEvent playerInteractEntityEvent = (PlayerInteractEntityEvent) event;
            Player targetPlayer = (Player) playerInteractEntityEvent.getRightClicked();
            Player player = playerInteractEntityEvent.getPlayer();

            ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());

            Profile targetProfile = profileManager.getProfile(targetPlayer.getUniqueId());
            if (profile == null || targetProfile == null) {
                return;
            }

           /* FrozenManager frozenManager = ModSuitePlugin.getInstance().getFrozenManager();
            frozenManager.setFrozenPlayer(player, targetProfile.getUniqueUUID()); */
        }
    }
}
