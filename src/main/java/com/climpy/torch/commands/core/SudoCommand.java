package com.climpy.torch.commands.core;

import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand extends RedrockCommand {
    public SudoCommand() {
        super("sudo", "Kölelerine emir ver!");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!profile.toPlayer().hasPermission("torch.command.sudo")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /" + label + " <oyuncuAdı/herkes> [Komut..]");
            return RedrockResultCommand.SUCCESS;
        }

        String executingCommand = StringUtils.join((Object[]) args, " ", 1, args.length).replace("/", "");
        if (args[0].equals("hepsi") || args[0].equals("herkes") || args[0].equals("all")) {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.performCommand(executingCommand));
            sender.sendMessage(ChatColor.GOLD + "Tüm oyunculara " + ChatColor.WHITE + executingCommand + ChatColor.GOLD + " komutu girdirildi.");
            return RedrockResultCommand.SUCCESS;
        }

        Player targetPlayer = Bukkit.getPlayerExact(args[0]);
        String chat = StringUtils.join((Object[]) args, " ", 2, args.length);
        if(args[1].equals("chat")) {
            sender.sendMessage(ChatColor.GOLD + targetPlayer.getName() + " oyuncusuna " + ChatColor.WHITE + chat + ChatColor.GOLD + " mesajı girdirildi.");
            targetPlayer.chat(chat);
            return RedrockResultCommand.SUCCESS;
        }

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Oyuncu bulunamadı.");
            return RedrockResultCommand.SUCCESS;
        }

        targetPlayer.performCommand(executingCommand);
        sender.sendMessage(ChatColor.GOLD + targetPlayer.getName() + " oyuncusuna " + ChatColor.WHITE + executingCommand + ChatColor.GOLD + " komutu girdirildi.");
        return RedrockResultCommand.SUCCESS;
    }
}
