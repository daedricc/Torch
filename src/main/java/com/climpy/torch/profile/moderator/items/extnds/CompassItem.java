package com.climpy.torch.profile.moderator.items.extnds;

import com.climpy.torch.profile.moderator.items.StaffModeItem;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.Event;

public class CompassItem extends StaffModeItem {

    public CompassItem() {
        super(ItemUtils.createItem(Material.COMPASS, "&6Işınlanma Pusulası", 1), 0);
    }

    @Override
    public void onCustomInteractEvent(Event event) {
        // nothing beacuse nothing sq
    }
}
