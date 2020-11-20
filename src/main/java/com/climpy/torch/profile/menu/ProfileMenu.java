package com.climpy.torch.profile.menu;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.inventory.ClickableItem;
import com.climpy.redrock.inventory.SmartInventory;
import com.climpy.redrock.inventory.content.InventoryContents;
import com.climpy.redrock.inventory.content.InventoryProvider;
import com.climpy.redrock.redis.player.RedisPlayerAction;
import com.climpy.redrock.redis.player.RedisPlayerCache;
import com.climpy.redrock.redis.player.RedisPlayerData;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.punishments.history.menu.HistoryCategoryMenu;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ItemUtils;
import com.climpy.torch.utils.TimeUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class ProfileMenu implements InventoryProvider {
    private Profile profile;

    public ProfileMenu(Profile profile) {
        this.profile = profile;
    }

    public static SmartInventory getInventory(Profile profile) {
        return SmartInventory.builder()
                .provider(new ProfileMenu(profile))
                .size(4, 9)
                .title("&8Profil: " + profile.getColoredName())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
       contents.fillBorders(ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData())));
       contents.set(0, 4, ClickableItem.empty(ItemUtils.createSkullItem(profile.getColoredName(), 1, (short) 3, profile.getName(), Collections.singletonList(C.GRAY + "Kullanıcı bilgileri"))));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        RedisPlayerCache redisPlayerCache = RedrockPlugin.getInstance().getRedisPlayerCache();
        RedisPlayerData redisPlayerData = redisPlayerCache.getRedisPlayerData(profile.getUniqueUUID());

        contents.set(1, 1, ClickableItem.empty(ItemUtils.createItem(Material.PAPER, "&eUUID", Arrays.asList(C.WHITE + profile.getUniqueUUID()))));
        contents.set(1, 2, ClickableItem.empty(ItemUtils.createItem(Material.WEB, "&eŞimdiki IP Adresi", Arrays.asList(C.WHITE + profile.getCurrentAddress()))));
        contents.set(1, 3, ClickableItem.empty(ItemUtils.createItem(Material.BOOK_AND_QUILL, "&eDaha önceki IP Adresleri", Arrays.asList(C.WHITE + profile.getIpAddresses()))));
        contents.set(1, 4, ClickableItem.empty(ItemUtils.createItem(Material.DIAMOND, "&9Rütbe", Arrays.asList(C.WHITE + profile.getRank().getColoredName()))));
        contents.set(1, 5, ClickableItem.empty(ItemUtils.createItem(Material.EYE_OF_ENDER, "&9Sembol", Arrays.asList(C.WHITE + "Çok yakında, yenilecek sistem.."))));
        contents.set(1, 6, ClickableItem.empty(ItemUtils.createItem(Material.CHEST, "&aMevcut Sunucu", Arrays.asList(C.WHITE + redisPlayerData.getLastSeenServer()))));
        contents.set(1, 7, ClickableItem.empty(ItemUtils.createItem(Material.GOLD_INGOT, "&aMCoins", Arrays.asList(C.WHITE + profile.getCoins()))));
        contents.set(2, 2, ClickableItem.empty(ItemUtils.createItem(Material.WATER_LILY, "&aAktiflik Durumu", Arrays.asList(C.WHITE + (redisPlayerData.getRedisPlayerAction() == RedisPlayerAction.JOINING_SERVER ? "Aktif" : "Kapalı")))));
        contents.set(2, 3, ClickableItem.empty(ItemUtils.createItem(Material.WATCH, "&eİlk Görülme", Arrays.asList(C.WHITE + TimeUtils.getDurationWords(System.currentTimeMillis() - profile.getFirstLoginTime(), true, true) + " önce"))));
        contents.set(2, 4, ClickableItem.empty(ItemUtils.createItem(Material.SIGN, "&eSon Görülme", Arrays.asList(C.WHITE + TimeUtils.getDurationWords(System.currentTimeMillis() - profile.getLastLoginTime(), true, true) + " önce"))));
        contents.set(2, 5, ClickableItem.empty(ItemUtils.createItem(Material.PACKED_ICE, "&dDondurulmuşluk Durumu", Arrays.asList(profile.isFrozenStatus() ? C.GREEN + "Açık" : C.RED + "Kapalı"))));
        contents.set(2, 6, ClickableItem.empty(ItemUtils.createItem(Material.REDSTONE, "&dPersonel Modu", Arrays.asList(profile.isStaffMode() ? C.GREEN + "Açık" : C.RED + "Kapalı"))));
    }
}
