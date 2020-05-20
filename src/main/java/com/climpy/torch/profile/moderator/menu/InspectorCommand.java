package com.climpy.torch.profile.moderator.menu;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InspectorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = Bukkit.getPlayer(commandSender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        InspectorMenu.getInventory(profile).open(player);
        return true;
    }
}
