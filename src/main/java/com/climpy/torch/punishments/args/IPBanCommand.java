package com.climpy.torch.punishments.args;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.redrock.utils.MiscUtils;
import com.climpy.redrock.utils.TimeUtils;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.punishments.Punishment;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.PunishmentType;
import com.climpy.torch.punishments.extnds.Ban;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IPBanCommand extends RedrockCommand {

    public IPBanCommand() {
        super("ipban");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        if (!(sender instanceof ConsoleCommandSender)) {
            Profile profile = profileManager.getProfile(sender.getName());
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
        Profile targetUser = profileManager.getProfile(offlinePlayer.getUniqueId());
        if (targetUser == null) {
        //    sender.sendMessage(Locale.format(Locale.PLAYER_NOT_FOUND));
            return RedrockResultCommand.SUCCESS;
        }

        long duration = TimeUtils.getTimeParser(silent ? args.length <= 2 ? "0" : args[2] : args.length <= 1 ? "0" : args[1]);
        String reason = MiscUtils.joinStrings(args, silent ? duration == 0 ? 2 : 3 : duration == 0 ? 1 : 2);
        if (reason.isEmpty()) reason = "Haksız Avantaj";

        UUID uniqueUUID = null;
        if (sender instanceof Player) {
            uniqueUUID = ((Player) sender).getUniqueId();
        }

        PunishmentManager punishmentManager = TorchPlugin.getInstance().getPunishmentManager();
        Punishment punishment = punishmentManager.getPunishment(targetUser.getUniqueUUID(), PunishmentType.BAN, PunishmentType.IP_BAN, PunishmentType.BLACKLIST);
        if (punishment != null) {
            sender.sendMessage(ChatColor.RED + "Kullanıcı zaten alternatif ceza kapsamında.");
            return RedrockResultCommand.SUCCESS;
        }

        punishment = duration == 0 ? new Ban(PunishmentType.IP_BAN, targetUser.getCurrentAddress(), targetUser.getUniqueUUID(), uniqueUUID, reason) :
                new Ban(PunishmentType.IP_BAN, targetUser.getCurrentAddress(), targetUser.getUniqueUUID(), uniqueUUID, reason, System.currentTimeMillis() + duration);
        punishmentManager.applyPunishment(punishment, silent, true);
        return RedrockResultCommand.SUCCESS;
    }
}
