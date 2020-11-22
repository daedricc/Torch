package com.climpy.torch.profile.cosmetic.menu;

import com.climpy.redrock.inventory.ClickableItem;
import com.climpy.redrock.inventory.SmartInventory;
import com.climpy.redrock.inventory.content.InventoryContents;
import com.climpy.redrock.inventory.content.InventoryProvider;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.color.menu.ColorMenu;
import com.climpy.torch.profile.rank.menu.RankSelectionMenu;
import com.climpy.torch.profile.symbol.menu.SymbolMenu;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CosmeticMenu implements InventoryProvider {

    private Profile profile;

    public CosmeticMenu(Profile profile) {
        this.profile = profile;
    }

    public static SmartInventory getInventory(Profile profile) {
        return SmartInventory.builder()
                .provider(new CosmeticMenu(profile))
                .size(3, 9)
                .title(C.translate("&8&l» &8Cosmetics"))
                .build();
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {
        contents.set(1, 3, ClickableItem.of(ItemUtils.createItem(Material.NAME_TAG, "&eColors",
                Arrays.asList(
                        "",
                        C.DARK_PURPLE + "Click here to open the colors menu."
        )), e -> {
            ColorMenu.getInventory(profile).open(profile.toPlayer());
        }));


        contents.set(1, 5, ClickableItem.of(ItemUtils.createItem(Material.PAPER, "&eSymbols",
                Arrays.asList(
                        "",
                        C.DARK_PURPLE + "Click here to open the symbol menu."
                )), e -> {
            SymbolMenu.getInventory(profile).open(profile.toPlayer());
        }));
    }
}
