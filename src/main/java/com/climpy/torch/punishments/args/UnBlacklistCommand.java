package com.climpy.torch.punishments.args;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.redrock.utils.MiscUtils;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.punishments.Punishment;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.PunishmentType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnBlacklistCommand extends RedrockCommand {

    public UnBlacklistCommand() {
        super("unblacklist");
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
            sender.sendMessage(ChatColor.RED + "Kullanım: /" + label + " (" + ChatColor.YELLOW + "-s" + ChatColor.RED + ") <oyuncu> [sebep...]");
            sender.sendMessage(ChatColor.RED + "Örnek: /" + label + " -s RedROCK 7d (MCheat) Haksız Avantaj");
            return RedrockResultCommand.SUCCESS;
        }

        boolean silent = false;
        if (args[0].equals("-s")) {
            silent = true;
        }

        Profile targetUser = profileManager.getDatabaseProfile(silent ? args[1] : args[0]);
        if (targetUser == null) {
        //    sender.sendMessage(Locale.format(Locale.PLAYER_NOT_FOUND));
            return RedrockResultCommand.SUCCESS;
        }

        String reason = MiscUtils.joinStrings(args, silent ? 2 : 1);
        if (reason.isEmpty()) reason = "Haksız Avantaj";

        UUID uniqueUUID = null;
        if (sender instanceof Player) {
            uniqueUUID = ((Player) sender).getUniqueId();
        }

        PunishmentManager punishmentManager = TorchPlugin.getInstance().getPunishmentManager();
        Punishment punishment = punishmentManager.getPunishment(targetUser.getUniqueUUID(), PunishmentType.BLACKLIST);
        if (punishment == null) {
            sender.sendMessage(ChatColor.RED + "Kullanıcı karaliste kapsamında değil.");
            return RedrockResultCommand.SUCCESS;
        }

        punishment.setRemovedBy(uniqueUUID);
        punishment.setRemovedReason(reason);
        punishment.setActive(false);

        punishmentManager.applyPunishment(punishment, silent, true);
        return RedrockResultCommand.SUCCESS;
    }
}
