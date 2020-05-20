package com.climpy.torch.commands.core;

import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.BukkitReflection;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpeedCommand extends RedrockCommand {
    public SpeedCommand() {
        super("speed", "Hızı değiştirir.", new String[]{"hız"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (sender instanceof Player) {
            boolean speedType;
            float speedMultiplier;
            Player targetPlayer;
            float finalSpeedMultiplier;
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (!profile.toPlayer().hasPermission("torch.command.speed")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Kullanım: /" + label + " <uçuş|yürü> <hızDeğeri|yenile> [oyuncuAdı]");
                return RedrockResultCommand.SUCCESS;
            }

            switch (args[0]) {
                case "fly":
                case "uçuş":
                    speedType = true;
                    break;
                case "walk":
                case "yürü":
                    speedType = false;
                    break;
                default:
                    speedType = false;
                    break;
            }

            if (args[1].equalsIgnoreCase("yenile") || args[1].equalsIgnoreCase("reset")) {
                speedMultiplier = speedType ? 2.0F : 1.0F;
            } else {
                speedMultiplier = Float.parseFloat(args[1]);
            }
            if (args.length == 3) {
                targetPlayer = Bukkit.getPlayerExact(args[2]);
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "Oyuncu bulunamadıç");
                    return RedrockResultCommand.SUCCESS;
                }

            } else {
                targetPlayer = Bukkit.getPlayerExact(sender.getName());
            }
            if (speedType) {
                finalSpeedMultiplier = 0.1F * speedMultiplier;
                try {
                    targetPlayer.setFlySpeed(finalSpeedMultiplier);
                } catch (IllegalArgumentException ex) {
                    if (finalSpeedMultiplier < 0.1F) {
                        sender.sendMessage(ChatColor.RED + "Girilen hız aşırı düşük.");
                        return RedrockResultCommand.SUCCESS;
                    }
                    if (finalSpeedMultiplier > 10.0F) {
                        sender.sendMessage(ChatColor.RED + "Girilen hız aşırı yüksek.");
                        return RedrockResultCommand.SUCCESS;
                    }
                }
            } else {
                finalSpeedMultiplier = 0.2F * speedMultiplier;
                try {
                    targetPlayer.setWalkSpeed(finalSpeedMultiplier);
                } catch (IllegalArgumentException ex) {
                    if (finalSpeedMultiplier < 0.2F) {
                        sender.sendMessage(ChatColor.RED + "Girilen hız aşırı düşük.");
                        return RedrockResultCommand.SUCCESS;
                    }
                    if (finalSpeedMultiplier > 10.0F) {
                        sender.sendMessage(ChatColor.RED + "Girilen hız aşırı yüksek.");
                        return RedrockResultCommand.SUCCESS;
                    }
                }
            }

            Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(targetPlayer.getUniqueId());
            sender.sendMessage(ChatColor.GOLD + (speedType ? "Uçma" : "Yürüyüş") + " hızı " + ChatColor.WHITE + targetProfile.getColoredName() + ChatColor.GOLD + " için " + ChatColor.WHITE + finalSpeedMultiplier + ChatColor.GOLD + " olarak güncellendi.");
            return RedrockResultCommand.SUCCESS;
        }

        sender.sendMessage("no");
        return RedrockResultCommand.SUCCESS;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        Validate.notNull(sender, "Sender boolamaz");
        Validate.notNull(args, "Arguments boolamaz");
        Validate.notNull(label, "Alias boolamaz");
        if (args.length == 1)
            return BukkitReflection.getCompletions(args, (List<String>) ImmutableList.of("uçuş", "yürü"));
        if (args.length == 2)
            return BukkitReflection.getCompletions(args, (List<String>) ImmutableList.of("yenile"));
        if (args.length == 3) {
            List<String> onlinePlayers = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.canSee(player) && player.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    onlinePlayers.add(player.getName());
            }
            return onlinePlayers;
        }
        return ImmutableList.of();
    }
}