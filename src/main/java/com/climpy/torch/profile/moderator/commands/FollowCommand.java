package com.climpy.torch.profile.moderator.commands;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.moderator.FollowManager;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FollowCommand extends RedrockCommand {
    public FollowCommand() {
        super("follow", "Oyuncuyu takip eder.", new String[]{"takip"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (sender instanceof Player) {
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (!profile.toPlayer().hasPermission("torch.command.follow")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }

            if(args.length < 1) {
                player.sendMessage(C.RED + "Kullanım: /" + label + " <oyuncuAdı..>");
                return RedrockResultCommand.SUCCESS;
            }

            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if(targetPlayer == null) {
                player.sendMessage(C.RED + "Oyuncu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(targetPlayer.getUniqueId());

            if(targetPlayer == player) {
                player.sendMessage(C.RED + "Kendini takip edemezsin.");
                return RedrockResultCommand.SUCCESS;
            }

            FollowManager followManager = TorchPlugin.getInstance().getFollowManager();
            for (UUID value : followManager.getFollowings(player.getUniqueId())) {
                if(targetPlayer.getUniqueId().equals(value)) {
                    player.sendMessage(C.RED + "Zaten bu kişiyi takip ediyorsun.");
                    return RedrockResultCommand.SUCCESS;
                }
            }

            UUID followingPlayer = followManager.getFollowing(player.getUniqueId());
            if (followingPlayer != null) {
                followManager.stopFollowing(player.getUniqueId());
            }

            followManager.startFollowing(player.getUniqueId(), targetPlayer.getUniqueId());
            player.sendMessage(targetProfile.getColoredName() + C.GOLD + " adlı oyuncuyu takip ediyorsun.");
        }
        return RedrockResultCommand.SUCCESS;
    }
}
