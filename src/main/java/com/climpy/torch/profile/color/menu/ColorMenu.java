package com.climpy.torch.profile.color.menu;

import com.climpy.redrock.inventory.ClickableItem;
import com.climpy.redrock.inventory.SmartInventory;
import com.climpy.redrock.inventory.content.InventoryContents;
import com.climpy.redrock.inventory.content.InventoryProvider;
import com.climpy.redrock.inventory.content.Pagination;
import com.climpy.redrock.inventory.content.SlotIterator;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.color.ColorType;
import com.climpy.torch.profile.cosmetic.menu.CosmeticMenu;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class ColorMenu implements InventoryProvider {

    private Profile profile;

    public ColorMenu(Profile profile) {
        this.profile = profile;
    }

    public static SmartInventory getInventory(Profile profile) {
        return SmartInventory.builder()
                .provider(new ColorMenu(profile))
                .size(5, 9)
                .title("Select the color")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
     /*   Pagination pagination = contents.pagination();

        contents.set(4, 3, ClickableItem.of(ItemUtils.createSkullItem("§aÖnceki Sayfa", (short) 3, "MHF_ArrowLeft", Arrays.asList("", "")),
                e -> getInventory(profile).open(player, pagination.previous().getPage())));

        contents.set(4, 5, ClickableItem.of(ItemUtils.createSkullItem("§aSonraki Sayfa", (short) 3, "MHF_ArrowRight", Arrays.asList("", "")),
                e -> getInventory(profile).open(player, pagination.next().getPage()))); */

        contents.set(4, 4, ClickableItem.of(ItemUtils.createItem(Material.BARRIER, "§aAna Sayfaya Dön", Arrays.asList("")),
                e -> CosmeticMenu.getInventory(profile).open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        List<ColorType> colorTypes;
        colorTypes = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(ColorType.values())));

        ClickableItem[] items = new ClickableItem[colorTypes.size()];

        for (int i = 0; i < items.length; i++) {
            ColorType colorType = colorTypes.get(i);

            items[i] = ClickableItem.of(ItemUtils.createItem(Material.WOOL, colorType.getChatColor() + colorType.getName(), (short) colorType.getVariant(), (Arrays.asList(
                    "",
                    "",
                    (profile.getColorType() == colorType ? C.RED + "This color is already selected." : C.GRAY + "Click to select the color."))
            )), e -> {
               if(profile.getColorType() == colorType) {
                    player.sendMessage(C.RED + "This color is already selected.");
                    return;
               }

                profile.setColorType(colorType);
                profile.saveProfile();

                profile.toPlayer().sendMessage(C.BLUE + "Your new name color is " + colorType.getChatColor() + colorType.getName());
            });

            Pagination pagination = contents.pagination();
            pagination.setItems(items);
            pagination.setItemsPerPage(27);

            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        }
    }

}
