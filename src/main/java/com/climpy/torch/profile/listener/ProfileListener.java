package com.climpy.torch.profile.listener;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.redis.player.RedisPlayerAction;
import com.climpy.redrock.redis.player.RedisPlayerCache;
import com.climpy.redrock.redis.player.RedisPlayerData;
import com.climpy.redrock.server.provider.DefaultServerProvider;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.punishments.Punishment;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetAddress;
import java.util.logging.Level;

public class ProfileListener implements Listener {

    @EventHandler
    public void asyncJoinEvent(AsyncPlayerPreLoginEvent event) {
        Player player = Bukkit.getPlayer(event.getUniqueId());
        if (player != null && player.isOnline()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "\nBağlantıyı kestikten sonra çok hızlı giriş yapmayı denediniz.\nBirkaç saniye sonra tekrar deneyin.");
            TorchPlugin.getInstance().getServer().getScheduler().runTask(TorchPlugin.getInstance(), () -> player.kickPlayer(ChatColor.RED + "Yinelenen girişten dolayı atıldınız."));
            return;
        }

        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Profile profile;

        try {
            profile = new Profile(event.getUniqueId(), event.getName());
            if (profileManager.getProfile(event.getUniqueId()) != null && !profile.isLoaded()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(ChatColor.RED + "Profiliniz yüklenemedi. Daha sonra tekrar deneyin.");
                return;
            }

            profile.setName(event.getName());
            if (profile.getFirstLoginTime() == null) {
                profile.setFirstLoginTime(System.currentTimeMillis());
            }

            InetAddress inetAddress = event.getAddress();
            String ipAddress = inetAddress.getHostAddress();

            profile.setCurrentAddress(ipAddress);
            if (!profile.hasIPAddress(ipAddress)) {
                profile.addIPAddress(ipAddress);
            }

            profile.setLastLoginTime(System.currentTimeMillis());

            PunishmentManager punishmentManager = TorchPlugin.getInstance().getPunishmentManager();
            for (Profile targetProfile : TorchPlugin.getInstance().getProfileManager().getDatabaseIPProfile(profile.getCurrentAddress())) {
                if (!targetProfile.getUniqueUUID().equals(profile.getUniqueUUID())) {
                    if (!targetProfile.getAlts().contains(profile.getUniqueUUID())) {
                        targetProfile.getAlts().add(profile.getUniqueUUID());
                        targetProfile.saveProfile();
                    }

                    if (!profile.getAlts().contains(targetProfile.getUniqueUUID())) {
                        profile.getAlts().add(targetProfile.getUniqueUUID());
                        profile.saveProfile();
                    }
                }

                Punishment punishment;
                if (targetProfile.getUniqueUUID().equals(profile.getUniqueUUID())) {
                    punishment = punishmentManager.getPunishment(targetProfile.getUniqueUUID(), PunishmentType.IP_BAN, PunishmentType.BAN, PunishmentType.BLACKLIST);
                } else {
                    punishment = punishmentManager.getPunishment(targetProfile.getUniqueUUID(), PunishmentType.IP_BAN, PunishmentType.BLACKLIST);
                }

                if (punishment != null) {
                    event.setKickMessage(punishment.getPunishmentMessage().toString());
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "\nProfil yüklenirken bir hata meydana geldi.");
            return;
        }

        profileManager.getProfiles().put(profile.getUniqueUUID(), profile);
        new BukkitRunnable() {

            @Override
            public void run() {
                RedisPlayerData redisPlayerData = new RedisPlayerData(event.getUniqueId(), event.getName());
                redisPlayerData.setRedisPlayerAction(RedisPlayerAction.JOINING_SERVER);
                redisPlayerData.setLastSeenServer(new DefaultServerProvider().getServerName());
                redisPlayerData.setLastSeenAt(System.currentTimeMillis());

                RedisPlayerCache redisPlayerCache = RedrockPlugin.getInstance().getRedisPlayerCache();
                redisPlayerCache.updateRedisPlayerData(redisPlayerData);
            }
        }.runTaskLaterAsynchronously(TorchPlugin.getInstance(), 30L);
    }

    @EventHandler
    public void onQuitListener(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfiles().remove(player.getUniqueId());
        if (profile != null) {
            new BukkitRunnable() {

                @Override
                public void run() {

                    try {
                        profile.saveProfile();
                    } catch (Exception ex) {
                        TorchPlugin.getInstance().getLogger().log(Level.SEVERE, "Profil verileri kaydedilemedi -> " + player.getName());
                        ex.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(TorchPlugin.getInstance());
        }

        RedisPlayerData redisPlayerData = new RedisPlayerData(player.getUniqueId(), player.getName());
        redisPlayerData.setRedisPlayerAction(RedisPlayerAction.LEAVING_SERVER);
        redisPlayerData.setLastSeenServer(new DefaultServerProvider().getServerName());
        redisPlayerData.setLastSeenAt(System.currentTimeMillis());

        RedisPlayerCache redisPlayerCache = RedrockPlugin.getInstance().getRedisPlayerCache();
        redisPlayerCache.updateRedisPlayerData(redisPlayerData);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void joinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            return;
        }

        profile.setupBukkitPlayer(player);
    }


}
