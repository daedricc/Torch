package com.climpy.torch.chat;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.redis.data.DataPublisher;
import com.climpy.redrock.server.ServerType;
import com.climpy.redrock.server.provider.DefaultServerProvider;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.chat.manager.ChatManager;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.MiscUtils;
import com.climpy.torch.utils.PatternUtils;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        ServerType serverType = RedrockPlugin.getInstance().getServerType();
        if(serverType == ServerType.TEST_SERVER || serverType == ServerType.UNKNOWN) {
            ChatManager chatManager = TorchPlugin.getInstance().getChatManager();
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getName());
            String lastMessage = (String) chatManager.getMessageHistory().getOrDefault(profile.getUniqueUUID(), null);

            if (profile.getRank().hasPermission("torch.staff")) {
                event.setCancelled(true);
                if (profile.isStaffChat()) {
                    DefaultServerProvider defaultServerProvider = new DefaultServerProvider();
                    DataPublisher dataPublisher = RedrockPlugin.getInstance().getRedisConnection().getDataPublisher();
                    JsonObject chatObject = new JsonObject();
                    chatObject.addProperty("uuid", profile.getUniqueUUID().toString());
                    chatObject.addProperty("serverName", defaultServerProvider.getServerName());
                    chatObject.addProperty("message", message);
                    dataPublisher.write("staff_chat", chatObject);
                } else {
                    profile.setStaffChat(false);
                }
            }

          /*  if (chatManager.isMuteChat()) {
                    event.setCancelled(false);
                    StringBuilder stringBuilder = new StringBuilder();
                    String STRING_SEPARATOR = "\n";
                    stringBuilder.append(ChatColor.RED).append("Mevcut sohbet içersine ").append(ChatColor.YELLOW.toString()).append(ChatColor.BOLD).append(TimeUtils.getDurationWords(editedSlowChatTime, true, true)).append(ChatColor.RED).append(" sonra mesaj yazabilirsiniz.").append("\n");
                    stringBuilder.append(ChatColor.RED).append("Yavaşlatma aktif, Bunu atlamak için " + ChatColor.AQUA + " VIP " + ChatColor.RED + "üyelik almalısın.");
                    player.sendMessage(stringBuilder.toString());
                    return;
                }
                if (System.currentTimeMillis() >= chatManager.getSlowChatUUID().get(user.getUniqueUUID()))
                    chatManager.getSlowChatUUID().remove(user.getUniqueUUID());
            } */

            if (MiscUtils.patternCheck(message, PatternUtils.REPORT_PATTERN)) {
                player.sendMessage(ChatColor.RED + "Hile mi buldun? Bunun için /report kullan.");
                player.sendMessage(ChatColor.YELLOW + "Bu sohbete girilen hile türleri diğerlerine bu hileler hakkında bilgi verebilir.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 0.5F, 0.5F);
                event.setCancelled(true);
                return;
            }

            if (MiscUtils.patternCheck(message, PatternUtils.URL_PATTERN) || MiscUtils.patternCheck(message, PatternUtils.IP_PATTERN)) {
                player.sendMessage(ChatColor.RED + "Sohbet içinde IP adreslerine izin verilmiyor.");
                player.playSound(player.getLocation(), Sound.CAT_MEOW, 0.5F, 0.5F);
                event.setCancelled(true);
                return;
            }

            if (lastMessage != null && (lastMessage.equals(message) || StringUtils.getLevenshteinDistance(message, lastMessage) <= 1) && !profile.getRank().hasPermission("torch.staff")) {
                player.sendMessage(ChatColor.RED + "Aynı mesajı 2 kere girmeyin.");
                event.setCancelled(true);
                return;
            }

          /*  if (BlockedWords.BLOCKED_WORDS.contains(message.toLowerCase())) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.DONKEY_HIT, 1.0F, 1.0F);
                player.sendMessage(C.translate("&c[&e⚠&c] Yazılan kelime yasaklanmış durumda."));

                for (Player playerStaff : Bukkit.getOnlinePlayers()) {
                    com.climpy.profile.Profile staffUser = RedrockPlugin.getInstance().getProfileManager().getProfile(playerStaff.getUniqueId());
                    if (staffUser.getRankType().isAboveOrEqual(RankType.CHAT_MOD))
                        playerStaff.sendMessage(C.translate("&&c[&e⚠&c] (Sansürlendi) " + user.getRankType().getPrefix() + player.getName() + ChatColor.DARK_GRAY + ": " + ChatColor.RESET + message));
                }
            } */

            event.setMessage(message);
            for (Player recipient : Bukkit.getOnlinePlayers()) {
                Profile recipientUser = TorchPlugin.getInstance().getProfileManager().getProfile(recipient.getUniqueId());
                recipient.sendMessage(sendMessageFormat(profile, message));
            }

            chatManager.getMessageHistory().putIfAbsent(profile.getUniqueUUID(), event.getMessage());
            event.setCancelled(true);
        }
    }

    private String sendMessageFormat(Profile profile, String message) {
        if (profile.getRank().hasPermission("torch.vip") && (profile.getRank().hasPermission("torch.staff")) ) {
            return ChatColor.translateAlternateColorCodes('&', profile.getSymbolType().getDisplayName() + profile.getRank().getPrefix() + profile.getRank().getColor() + profile.getColoredName() + "&f: &f" + message);
        } else {
            return ChatColor.translateAlternateColorCodes('&', profile.getSymbolType().getDisplayName() + profile.getRank().getPrefix() + profile.getRank().getColor() + profile.getColoredName() + "&f: &7" + message);
        }
    }

    //  LoggerUtils.enterLog("chat.txt", event.getMessage(), user.getName());
}
