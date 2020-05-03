package com.climpy.torch.punishments.args;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.punishments.Punishment;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.PunishmentType;
import com.climpy.torch.punishments.extnds.Ban;
import com.climpy.torch.utils.DateTimeUtils;
import com.climpy.torch.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class BanCommand extends RedrockCommand {

    public BanCommand() {
        super("ban");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if (!(sender instanceof ConsoleCommandSender)) {
            if (!profile.toPlayer().hasPermission("redrock.moderator")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /" + label + " (" + ChatColor.YELLOW + "-s" + ChatColor.RED + ") <oyuncu> <süre> [sebep...]");
            sender.sendMessage(ChatColor.RED + "Örnek: /" + label + " -s Redrock 7d (MCheat) Haksız Avantaj");
            return RedrockResultCommand.SUCCESS;
        }

        boolean silent = false;
        if (args[0].equals("-s")) {
            silent = true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(silent ? args[1] : args[0]);
        Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(offlinePlayer.getUniqueId());
        if (targetProfile == null) {
            sender.sendMessage(String.format(ChatColor.RED + "%1$s adlı oyuncu bulunamadı.", silent ? args[1] : args[0]));
            return RedrockResultCommand.SUCCESS;
        }

        long duration = DateTimeUtils.parseLong(silent ? args.length <= 2 ? "0" : args[2] : args.length <= 1 ? "0" : args[1]);
        String reason = MiscUtils.joinStrings(args, silent ? duration == 0 ? 2 : 3 : duration == 0 ? 1 : 2);
        if (reason.isEmpty()) reason = "[MoosyAC] Haksız Avantaj";

        PunishmentManager punishmentManager = TorchPlugin.getInstance().getPunishmentManager();
        Punishment punishment = punishmentManager.getPunishment(targetProfile.getUniqueUUID(), PunishmentType.BAN, PunishmentType.IP_BAN, PunishmentType.BLACKLIST);
        if (punishment != null) {
            sender.sendMessage(ChatColor.RED + "Kullanıcı zaten alternatif ceza kapsamında.");
            return RedrockResultCommand.SUCCESS;
        }

        punishment = duration == 0 ? new Ban(PunishmentType.BAN, targetProfile.getCurrentAddress(), targetProfile.getUniqueUUID(), profile.getUniqueUUID(), reason) :
                new Ban(PunishmentType.BAN, targetProfile.getCurrentAddress(), targetProfile.getUniqueUUID(), profile.getUniqueUUID(), reason, System.currentTimeMillis() + duration);

        punishmentManager.applyPunishment(punishment, silent, true);
        return RedrockResultCommand.SUCCESS;
    }
}
