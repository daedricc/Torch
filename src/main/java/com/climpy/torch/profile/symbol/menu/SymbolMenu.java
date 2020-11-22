package com.climpy.torch.profile.symbol.menu;

import com.climpy.redrock.inventory.ClickableItem;
import com.climpy.redrock.inventory.SmartInventory;
import com.climpy.redrock.inventory.content.InventoryContents;
import com.climpy.redrock.inventory.content.InventoryProvider;
import com.climpy.redrock.inventory.content.Pagination;
import com.climpy.redrock.inventory.content.SlotIterator;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.cosmetic.menu.CosmeticMenu;
import com.climpy.torch.profile.symbol.SymbolType;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class SymbolMenu  implements InventoryProvider {

    private Profile profile;

    public SymbolMenu(Profile profile) {
        this.profile = profile;
    }

    public static SmartInventory getInventory(Profile profile) {
        return SmartInventory.builder()
                .provider(new SymbolMenu(profile))
                .size(5, 9)
                .title("Select the symbol")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
       /* Pagination pagination = contents.pagination();

        contents.set(4, 3, ClickableItem.of(ItemUtils.createSkullItem("§aÖnceki Sayfa", (short) 3, "MHF_ArrowLeft", Arrays.asList("", "")),
                e -> getInventory(profile).open(player, pagination.previous().getPage())));

        contents.set(4, 5, ClickableItem.of(ItemUtils.createSkullItem("§aSonraki Sayfa", (short) 3, "MHF_ArrowRight", Arrays.asList("", "")),
                e -> getInventory(profile).open(player, pagination.next().getPage()))); */

        contents.set(4, 4, ClickableItem.of(ItemUtils.createItem(Material.BARRIER, "§aAna Sayfaya Dön", Arrays.asList("")),
                e -> CosmeticMenu.getInventory(profile).open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        List<SymbolType> symbolTypes;
        symbolTypes = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(SymbolType.values())));

        ClickableItem[] items = new ClickableItem[symbolTypes.size()];

        Pagination pagination = contents.pagination();

        for (int i = 0; i < items.length; i++) {
            SymbolType symbolType = symbolTypes.get(i);
            StringJoiner perm = new StringJoiner(", ");

            items[i] = ClickableItem.of(ItemUtils.createItem(Material.STAINED_GLASS_PANE, C.GREEN + symbolType.getName() + C.WHITE + ": " + symbolType.getDisplayName(), (profile.getSymbolType() == symbolType ? (short) 5 : (short) 14), (Arrays.asList(
                    "",
                    "",
                    (profile.getSymbolType() == symbolType ? C.RED + "This symbol is already selected." : C.GRAY + "Click to select the symbol."))
            )), e -> {
               if(profile.getSymbolType() == symbolType) {
                    player.sendMessage(C.RED + "This symbol is already selected.");
                    return;
               }

                profile.setSymbolType(symbolType);
                profile.saveProfile();

                profile.toPlayer().sendMessage(C.BLUE + "Your new selected symbol: " + profile.getSymbolType().getDisplayName());
            });

            pagination.setItems(items);
            pagination.setItemsPerPage(27);

            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        }


     /*   List<SymbolType> symbolTypes;

        Pagination pagination = contents.pagination();
        symbolTypes = Collections.unmodifiableList(SymbolType.values().length); */
    }

}
