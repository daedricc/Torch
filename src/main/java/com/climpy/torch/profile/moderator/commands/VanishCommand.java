package com.climpy.torch.profile.moderator.commands;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.moderator.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends RedrockCommand {
    public VanishCommand() {
        super("vanish", "Görünmezlik modunu açar.", new String[]{"görünmezlik"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String s, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (sender instanceof Player) {
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (!profile.toPlayer().hasPermission("torch.command.vanish")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }

            VanishManager vanishManager = TorchPlugin.getInstance().getVanishManager();

            boolean newVanishStatus = !profile.isVanishStatus();
            profile.setVanishStatus(newVanishStatus);
            profile.saveProfile();
            vanishManager.setVanishStatus(profile.getUniqueUUID(), newVanishStatus);
            sender.sendMessage(ChatColor.GOLD + "Görünmezlik Modu " + (newVanishStatus ? (ChatColor.GREEN + "açıldı.") : (ChatColor.DARK_RED + "kapatıldı.")));
            return RedrockResultCommand.SUCCESS;
        }
        sender.sendMessage("Bu islem sadece oyundan yurutelebilir.");
        return RedrockResultCommand.SUCCESS;
    }


}
