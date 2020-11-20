package com.climpy.torch.profile.moderator.commands;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.moderator.FollowManager;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnFollowCommand extends RedrockCommand {
    public UnFollowCommand() {
        super("unfollow", "Takip modundan çıkarsınız.", new String[]{"takipçıkar", "unf"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (sender instanceof Player) {
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (!profile.toPlayer().hasPermission("torch.command.unfollow")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }

            FollowManager followManager = TorchPlugin.getInstance().getFollowManager();
            UUID following = followManager.getFollowing(player.getUniqueId());
            if (following == null) {
                sender.sendMessage(ChatColor.RED + "Herhangi birini takip etmiyorsun.");
                return RedrockResultCommand.SUCCESS;
            }

            Profile profile1 = TorchPlugin.getInstance().getProfileManager().getProfile(following);
            followManager.stopFollowing(player.getUniqueId());
            player.sendMessage(C.WHITE + profile1.getName() + C.GOLD + " adlı oyuncuyu artık takip etmiyorsun.");
        }
        return RedrockResultCommand.SUCCESS;
    }
}
