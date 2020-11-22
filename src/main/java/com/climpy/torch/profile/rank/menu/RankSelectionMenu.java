package com.climpy.torch.profile.rank.menu;

import com.climpy.redrock.inventory.ClickableItem;
import com.climpy.redrock.inventory.SmartInventory;
import com.climpy.redrock.inventory.content.InventoryContents;
import com.climpy.redrock.inventory.content.InventoryProvider;
import com.climpy.redrock.inventory.content.Pagination;
import com.climpy.redrock.inventory.content.SlotIterator;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.rank.Rank;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class RankSelectionMenu implements InventoryProvider {

    private Profile profile;

    public RankSelectionMenu(Profile profile) {
        this.profile = profile;
    }

    public static SmartInventory getInventory(Profile profile) {
        return SmartInventory.builder()
                .provider(new RankSelectionMenu(profile))
                .size(5, 9)
                .title("Select a new rank for " + profile.getName())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.set(4, 3, ClickableItem.of(ItemUtils.createSkullItem("§aÖnceki Sayfa", (short) 3, "MHF_ArrowLeft", Arrays.asList("", "")),
                e -> getInventory(profile).open(player, pagination.previous().getPage())));

        contents.set(4, 5, ClickableItem.of(ItemUtils.createSkullItem("§aSonraki Sayfa", (short) 3, "MHF_ArrowRight", Arrays.asList("", "")),
                e -> getInventory(profile).open(player, pagination.next().getPage())));
    }


    @Override
    public void update(Player player, InventoryContents contents) {
        List<Rank> ranks;
        ranks = Collections.unmodifiableList(new ArrayList<>(Rank.getRanks().values()));

        Pagination pagination = contents.pagination();
        ClickableItem[] items = new ClickableItem[ranks.size()];

        for (int i = 0; i < items.length; i++) {
            Rank rank = ranks.get(i);
            StringJoiner perm = new StringJoiner(", ");
            for (String permissions : rank.getAllPermissions()) {
                perm.add(permissions);
            }

            items[i] = ClickableItem.of(ItemUtils.createItem(Material.STAINED_GLASS_PANE,
                    C.BLUE +
                    (rank.isDefaultRank() ?
                            (C.BLUE + "Rütbe Bilgisi " + C.GRAY + "(" + C.WHITE + rank.getColoredName() + C.GRAY + ")") + C.GRAY + " (" + C.WHITE + "Varsayılan Rütbe" + C.GRAY + ")"
                            : (C.BLUE + "Rütbe Bilgisi " + C.GRAY + "(" + C.WHITE + rank.getColoredName() + C.GRAY + ")")), 1,

                    (profile.getRank() == rank ? (short) 5 : (short) 14),
                    (profile.getRank() == rank ? Arrays.asList(
                C.MENU_BAR,
                C.YELLOW + "Ağırlık" + C.GRAY + ": " + C.WHITE + rank.getWeight(),
                C.YELLOW + "Ünvan" + C.GRAY + ": " + C.WHITE + C.WHITE + rank.getPrefix() + profile.getName(),
                C.YELLOW + "Renk" + C.GRAY + ": " + rank.getColor(),
                "",
                C.YELLOW + "Yetkiler: " + C.WHITE + "(" + rank.getAllPermissions().size() + ")",
                C.WHITE + perm.toString() + "",
                C.MENU_BAR,
                C.RED + "Bu oyuncu zaten bu rütbeye sahip.",
                C.RED + "Başka bir rütbe seçerek deneyin.",
                C.MENU_BAR)
            : Arrays.asList(
                 C.MENU_BAR,
                 C.YELLOW + "Ağırlık" + C.GRAY + ": " + C.WHITE + rank.getWeight(),
                 C.YELLOW + "Ünvan" + C.GRAY + ": " + C.WHITE + C.WHITE + rank.getPrefix() + profile.getName(),
                 C.YELLOW + "Renk" + C.GRAY + ": " + rank.getColor(),
                 "",
                 C.YELLOW + "Yetkiler: " + C.WHITE + "(" + rank.getAllPermissions().size() + ")",
                 C.WHITE + perm.toString() + "",
                 C.MENU_BAR)
            )), e -> {
                if(profile.getRank() == rank) {
                    player.sendMessage(C.RED + "Bu oyuncu bu rütbeye zaten sahip.");
                    return;
                }

                profile.setRank(rank);
                profile.saveProfile();

                player.sendMessage(profile.getColoredName() + C.BLUE + " için rütbe " + rank.getColoredName() + C.BLUE +  " olarak değiştirildi.");
                profile.toPlayer().sendMessage(C.BLUE + "Rütbeniz " + rank.getColoredName() + C.BLUE + " olarak değiştirildi.");
            });

            pagination.setItems(items);
            pagination.setItemsPerPage(27);

            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        }
    }
}
