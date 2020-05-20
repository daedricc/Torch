package com.climpy.torch.profile.moderator.menu;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.inventory.ClickableItem;
import com.climpy.redrock.inventory.SmartInventory;
import com.climpy.redrock.inventory.content.InventoryContents;
import com.climpy.redrock.inventory.content.InventoryProvider;
import com.climpy.redrock.inventory.content.SlotPos;
import com.climpy.redrock.redis.RedisManager;
import com.climpy.redrock.redis.player.RedisPlayerAction;
import com.climpy.redrock.redis.player.RedisPlayerCache;
import com.climpy.redrock.redis.player.RedisPlayerData;
import com.climpy.redrock.server.ServerData;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.utils.ItemUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OnlineStaffMenu implements InventoryProvider {

    public static SmartInventory getInventory() {
        return SmartInventory.builder()
                .provider(new OnlineStaffMenu())
                .size(1, 9)
                .title("&8Çevrimiçi Yetkililer")
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {
        RedisPlayerCache redisPlayerCache = RedrockPlugin.getInstance().getRedisPlayerCache();
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        RedisManager redisManager = RedrockPlugin.getInstance().getRedisManager();

        List<Profile> profiles = profileManager.getDatabaseProfiles().stream().filter(profile -> {
            return profile.toPlayer().hasPermission("torch.rank.mod");
        }).collect(Collectors.toList());

        List<Profile> targetProfiles = profiles.stream().filter(profile -> {
            RedisPlayerData redisPlayerData = redisPlayerCache.getRedisPlayerData(profile.getUniqueUUID());
            return redisPlayerData != null && redisPlayerData.getRedisPlayerAction() == RedisPlayerAction.JOINING_SERVER;
        }).collect(Collectors.toList());

        RedisPlayerData redisPlayerData = redisPlayerCache.getRedisPlayerData(player.getUniqueId());
        for (int i = 0; i < targetProfiles.size(); i++) {
            Profile targetProfile = targetProfiles.get(i);

            RedisPlayerData targetPlayerRedisData = redisPlayerCache.getRedisPlayerData(targetProfile.getUniqueUUID());
            contents.set(SlotPos.of(0, i), ClickableItem.of(ItemUtils.createSkullItem(targetProfile.getColoredName(), (short) 3, targetProfile.getName(), Arrays.asList(
                    "§7Sunucu: §f" + targetPlayerRedisData.getLastSeenServer(),
                    "§7Staff Modu: " + (targetProfile.isStaffMode() ? "§aAçık" : "§cKapalı"),
                    "§7Görünmezlik: " + (targetProfile.isVanishStatus() ? "§aAçık" : "§cKapalı"),
                    "",
                    (targetProfile.getUniqueUUID().equals(player.getUniqueId()) ? "§cBu hesap size ait." : "§eYanına gitmek için tıkla!")
            )), e -> {
                if (!targetProfile.getUniqueUUID().equals(player.getUniqueId())) {
                    if(targetPlayerRedisData.getLastSeenServer().equals(redisPlayerData.getLastSeenServer())) {
                        player.teleport(targetProfile.toPlayer());
                    } else {
                        ServerData serverData = redisManager.getServerByName(targetPlayerRedisData.getLastSeenServer());
                        redisManager.sendRedisServer(player.getUniqueId(), serverData);
                    }
                }
            }));

        }
    }
}
