package com.climpy.torch.profile.moderator.items.extnds;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.profile.moderator.VanishManager;
import com.climpy.torch.profile.moderator.items.StaffModeItem;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

@Getter
public class VanishChangeItem extends StaffModeItem {
    public VanishChangeItem() {
        super(null, 7);
        this.changeVanishItem(true);
    }

    @Override
    public void onCustomInteractEvent(Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
            Player player = playerInteractEvent.getPlayer();
            VanishManager vanishManager = TorchPlugin.getInstance().getVanishManager();
            ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            if(profile == null) {
                return;
            }

            boolean newVanishStatus = !profile.isVanishStatus();
            profile.setVanishStatus(newVanishStatus);
            profile.saveProfile();

            this.changeVanishItem(newVanishStatus).applyToPlayer(player);
            vanishManager.setVanishStatus(profile.getUniqueUUID(), newVanishStatus);
        }
    }

    public VanishChangeItem changeVanishItem(boolean newVanishStatus) {
        this.itemStack = ItemUtils.createItem(Material.INK_SACK, C.AQUA + (newVanishStatus ? "Görünür Ol" : "Görünmez Ol"), (newVanishStatus ? (short) 8 : (short) 10), Enchantment.DURABILITY);
        return this;
    }
}