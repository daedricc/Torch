package com.climpy.torch.profile.moderator.listener;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.moderator.FollowManager;
import com.climpy.torch.profile.moderator.event.PlayerStartFollowingEvent;
import com.climpy.torch.profile.moderator.event.PlayerStopFollowingEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class FollowListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerStartFollowingEvent(PlayerStartFollowingEvent event) {
        Player followerPlayer = Bukkit.getPlayer(event.getFollowerPlayer());
        Player followingPlayer = Bukkit.getPlayer(event.getFollowingPlayer());

        followerPlayer.teleport(followingPlayer);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerStopFollowingEvent(PlayerStopFollowingEvent event) {
        Player followerPlayer = Bukkit.getPlayer(event.getFollowerPlayer());
        FollowManager followManager = TorchPlugin.getInstance().getFollowManager();

        followManager.getFollowings().remove(followerPlayer.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        FollowManager followManager = TorchPlugin.getInstance().getFollowManager();
        for (UUID followerPlayer : followManager.getFollowers(player.getUniqueId())) {
            followManager.stopFollowing(followerPlayer);
        }

        followManager.stopFollowing(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        FollowManager followManager = TorchPlugin.getInstance().getFollowManager();
        for (UUID follower : followManager.getFollowers(player.getUniqueId())) {
            Player followerPlayer = Bukkit.getPlayer(follower);
            if(followerPlayer == null) {
                continue;
            }

            followerPlayer.teleport(player);
        }
    }
}
