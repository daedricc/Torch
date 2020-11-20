package com.climpy.torch.commands.core;

import com.climpy.redrock.server.ServerType;
import com.climpy.redrock.server.provider.DefaultServerProvider;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class HealCommand extends RedrockCommand {
    public HealCommand() {
        super("heal", "Canlandırma komutu.", new String[]{"canlandır", "canlan"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player p = Bukkit.getPlayer(sender.getName());
        DefaultServerProvider defaultServerProvider = new DefaultServerProvider();
        if (!profile.toPlayer().hasPermission("torch.command.heal")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length == 0) {
            p.sendMessage("§aKendinizi iyileştirdiniz.");
            p.setHealth(20);
            p.setFireTicks(0);

            for (PotionEffect po : p.getActivePotionEffects()) {
                p.removePotionEffect(po.getType());
            }
            p.setFoodLevel(20);
            p.setSaturation(10);
            return RedrockResultCommand.SUCCESS;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOyuncu bulunamadı."));
            return RedrockResultCommand.SUCCESS;
        }

        Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(targetPlayer.getUniqueId());

        sender.sendMessage(targetProfile.getColoredName() + " §aiyileştirildi.");
        targetPlayer.sendMessage(profile.getColoredName() + " §aadlı yönetici tarafından iyileştirildiniz.");
        targetPlayer.setHealth(20);
        targetPlayer.setFireTicks(0);
        for (PotionEffect to : targetPlayer.getActivePotionEffects()) {
            targetPlayer.removePotionEffect(to.getType());
        }

        targetPlayer.setFoodLevel(20);
        targetPlayer.setSaturation(10);
        return RedrockResultCommand.SUCCESS;
    }
}
