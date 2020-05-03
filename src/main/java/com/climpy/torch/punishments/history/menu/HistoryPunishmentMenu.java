package com.climpy.torch.punishments.history.menu;

import com.climpy.redrock.inventory.ClickableItem;
import com.climpy.redrock.inventory.SmartInventory;
import com.climpy.redrock.inventory.content.InventoryContents;
import com.climpy.redrock.inventory.content.InventoryProvider;
import com.climpy.redrock.inventory.content.Pagination;
import com.climpy.redrock.inventory.content.SlotIterator;
import com.climpy.redrock.utils.TimeUtils;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.punishments.Punishment;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.PunishmentType;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HistoryPunishmentMenu implements InventoryProvider {

    private PunishmentType punishmentType;
    private Profile profile;

    public HistoryPunishmentMenu(PunishmentType punishmentType, Profile profile) {
        this.punishmentType = punishmentType;
        this.profile = profile;
    }

    public static SmartInventory getInventory(PunishmentType punishmentType, Profile profile) {
        return SmartInventory.builder()
                .provider(new HistoryPunishmentMenu(punishmentType, profile))
                .size(3, 9)
                .title("&8" + punishmentType.getDisplayName() + " türündeki cezalar")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.set(2, 3, ClickableItem.of(ItemUtils.createSkullItem("§aÖnceki Sayfa", (short) 3, "MHF_ArrowLeft", Arrays.asList("", "")),
                e -> getInventory(punishmentType, profile).open(player, pagination.previous().getPage())));

        contents.set(2, 5, ClickableItem.of(ItemUtils.createSkullItem("§aSonraki Sayfa", (short) 3, "MHF_ArrowRight", Arrays.asList("", "")),
                e -> getInventory(punishmentType, profile).open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        List<Punishment> punishments;

        if (punishmentType == PunishmentType.BAN) {
            punishments = Collections.unmodifiableList(profile.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN || punishment.getPunishmentType() == PunishmentType.IP_BAN).collect(Collectors.toList()));
        } else if (punishmentType == PunishmentType.MUTE) {
            punishments = Collections.unmodifiableList(profile.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE || punishment.getPunishmentType() == PunishmentType.IP_MUTE).collect(Collectors.toList()));
        } else {
            punishments = Collections.unmodifiableList(profile.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == punishmentType).collect(Collectors.toList()));
        }

        Pagination pagination = contents.pagination();
        ClickableItem[] items = new ClickableItem[punishments.size()];

        PunishmentManager punishmentManager = TorchPlugin.getInstance().getPunishmentManager();

        for (int i = 0; i < items.length; i++) {
            Punishment punishment = punishments.get(i);

            if (punishmentType == PunishmentType.KICK || punishmentType == PunishmentType.WARN) {
                items[i] = ClickableItem.empty(ItemUtils.createItem(Material.WOOL, C.GRAY + "- ID: " + punishment.getPunishmentID() + " " + (isSharingPunishment(profile, punishment.getPunishmentID()) ? "(Paylaşılan)" : ""), 1, (short) (punishment.isActive() ? 13 : 14), Arrays.asList(
                        C.MENU_BAR,
                        C.YELLOW + "Kullanıcı: " + C.RED + profile.getName(),
                        C.YELLOW + "Ekleyen: " + C.RED + punishment.getAddedBy(),
                        C.YELLOW + "Nedeni: " + C.RED + punishment.getAddedReason(),
                        C.MENU_BAR
                )));
            }

            if (punishmentType == PunishmentType.BLACKLIST) {
                items[i] = ClickableItem.of(ItemUtils.createItem(Material.WOOL, C.GRAY + "- ID: " + punishment.getPunishmentID() + " " + (isSharingPunishment(profile, punishment.getPunishmentID()) ? "(Paylaşılan)" : ""), 1, (short) (punishment.isActive() ? 13 : 14), (punishment.isActive() ? Arrays.asList(
                        C.MENU_BAR,
                        C.YELLOW + "Kullanıcı: " + C.RED + profile.getName(),
                        C.YELLOW + "Ekleyen: " + C.RED + new ProfileManager().getProfile(punishment.getAddedBy()).getName(),
                        C.YELLOW + "Nedeni: " + C.RED + punishment.getAddedReason(),
                        C.YELLOW + "Durum: " + C.RED + (punishment.isActive() ? C.GREEN + "Açık" : C.DARK_RED + "Kapalı"),
                        C.MENU_BAR,
                        "",
                        C.GREEN + "Çözmek için sağ-tıkla!"
                ) : Arrays.asList(
                        C.MENU_BAR,
                        C.YELLOW + "Kullanıcı: " + C.RED + profile.getName(),
                        C.YELLOW + "Kaldıran: " + C.RED + (punishment.getRemovedBy() == null ? "MoosyAI" : new ProfileManager().getProfile(punishment.getRemovedBy()).getName()),
                        C.YELLOW + "Kaldırılma Nedeni: " + C.RED + punishment.getRemovedReason(),
                        C.YELLOW + "Durum: " + C.RED + (punishment.isActive() ? C.GREEN + "Açık" : C.DARK_RED + "Kapalı"),
                        C.MENU_BAR,
                        "",
                        C.RED + "Bu ceza çözülmüş."
                ))), e -> {
                    if (e.isRightClick()) {
                        if (punishment.isActive()) {
                            punishment.setRemovedBy(player.getUniqueId());
                            punishment.setRemovedReason("Ceza çözüldü.");
                            punishment.setActive(false);

                            punishmentManager.applyPunishment(punishment, false, true);
                        }
                    } else {
                        player.playSound(player.getLocation(), Sound.GLASS, 10, 10);
                        player.sendMessage(C.RED + "Bu ceza zaten çözülmüş.");
                    }
                });
            }

            if (punishmentType == PunishmentType.BAN || punishmentType == PunishmentType.MUTE) {
                items[i] = ClickableItem.of(ItemUtils.createItem(Material.WOOL, C.GRAY + "- ID: " + punishment.getPunishmentID() + " " + (isSharingPunishment(profile, punishment.getPunishmentID()) ? "(Paylaşılan)" : ""), 1, (short) (punishment.isActive() ? 13 : 14), (punishment.isActive() ? Arrays.asList(
                        C.MENU_BAR,
                        C.YELLOW + "Kullanıcı: " + C.RED + profile.getName(),
                        C.YELLOW + "Ekleyen: " + C.RED + new ProfileManager().getProfile(punishment.getAddedBy()).getName(),
                        C.YELLOW + "Nedeni: " + C.RED + punishment.getAddedReason(),
                        C.YELLOW + "Sona erme zamanı: " + C.RED + (punishment.isPermanent() ? "Sonsuza dek!" : TimeUtils.getDurationWords(punishment.getDuration() - System.currentTimeMillis(), true, true)),
                        C.YELLOW + "Durum: " + C.RED + (punishment.isActive() ? C.GREEN + "Açık" : C.DARK_RED + "Kapalı"),
                        C.MENU_BAR,
                        "",
                        C.GREEN + "Çözmek için sağ-tıkla!"
                ) : Arrays.asList(
                        C.MENU_BAR,
                        C.YELLOW + "Kullanıcı: " + C.RED + profile.getName(),
                        C.YELLOW + "Kaldıran: " + C.RED + (punishment.getRemovedBy() == null ? "MoosyAI" : new ProfileManager().getProfile(punishment.getRemovedBy()).getName()),
                        C.YELLOW + "Kaldırılma Nedeni: " + C.RED + punishment.getRemovedReason(),
                        C.YELLOW + "Durum: " + C.RED + (punishment.isActive() ? C.GREEN + "Açık" : C.DARK_RED + "Kapalı"),
                        C.MENU_BAR,
                        "",
                        C.RED + "Bu ceza çözülmüş."
                ))), e -> {
                    if (e.isRightClick()) {
                        if (punishment.isActive()) {
                            punishment.setRemovedBy(player.getUniqueId());
                            punishment.setRemovedReason("Ceza çözüldü.");
                            punishment.setActive(false);

                            punishmentManager.applyPunishment(punishment, false, true);
                        } else {
                            player.playSound(player.getLocation(), Sound.GLASS, 10, 10);
                            player.sendMessage(C.RED + "Bu ceza zaten çözülmüş.");
                        }
                    }
                });
            }

            pagination.setItems(items);
            pagination.setItemsPerPage(7);

            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));

        }
    }

    private boolean isSharingPunishment(Profile profile, String punishmentID) {
        for (UUID alt : profile.getAlts()) {
            Profile profile1 = TorchPlugin.getInstance().getProfileManager().getProfile(alt);
            for (Punishment punishment : profile1.getPunishments()) {
                if (punishment.getPunishmentID().equals(punishmentID)) {
                    return true;
                }
            }
        }

        return false;
    }
}