package com.climpy.torch.profile.moderator.commands;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.moderator.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffModeCommand extends RedrockCommand {

    public StaffModeCommand() {
        super("staff", "Personel moduna geçiş yapar.", new String[]{"staffmode", "personel", "sm"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (sender instanceof Player) {
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (!profile.toPlayer().hasPermission("torch.command.staffmode")) {
                sender.sendMessage(ChatColor.RED + "Oyuncu bulunamadı.");
                return true;
            }

            StaffManager staffManager = TorchPlugin.getInstance().getStaffManager();

            boolean newStaffModeStatus = !profile.isStaffMode();
            staffManager.setStaffMode(profile.getUniqueUUID(), newStaffModeStatus);
            sender.sendMessage(ChatColor.GOLD + "Staff Modu " + (newStaffModeStatus ? (ChatColor.GREEN + "açıldı.") : (ChatColor.DARK_RED + "kapatıldı.")));
            return true;
        }
        sender.sendMessage("Bu islem sadece oyundan yurutelebilir.");
        return true;
    }

    @Override
    public RedrockResultCommand execute(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
