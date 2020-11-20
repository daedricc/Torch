package com.climpy.torch.commands.core;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand extends RedrockCommand {
    public CoinsCommand() {
        super("coins", "MCoins yönetimi.", new String[]{"coin", "mcoins", "kredi"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.coins")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 3) {
            sender.sendMessage(C.RED + "Kullanım: /" + label + " <set:remove:give> [oyuncuAdı] <kredi-miktarı..>");
            return RedrockResultCommand.SUCCESS;
        }

        switch (args[0]) {
            case ("set"): {
                Profile targetUser = TorchPlugin.getInstance().getProfileManager().getProfile(args[1]);

                if (targetUser == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cVeritabanında &f'" + args[0] + "' &cismine sahip bir oyuncu bulunamadı."));
                    return RedrockResultCommand.SUCCESS;
                }

                Player t = Bukkit.getPlayer(args[1]);
                targetUser.setCoins(Long.parseLong(args[2]));

                sender.sendMessage(C.translate(profile.getColoredName() + " &aoyuncusunun kredi değeri &f" + MiscUtils.formatNumberByDecimal(Double.parseDouble(args[2])) + " &aolarak güncellendi."));
                t.sendMessage(C.translate("&aKredi değeriniz &f" + MiscUtils.formatNumberByDecimal(Double.parseDouble(args[2])) + " &aolarak güncellendi."));
                targetUser.saveProfile();
                break;
            }

            case ("remove"): {
                Profile targetUser = TorchPlugin.getInstance().getProfileManager().getProfile(args[1]);

                if (targetUser == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cVeritabanında &f'" + args[0] + "' &cismine sahip bir oyuncu bulunamadı."));
                    return RedrockResultCommand.SUCCESS;
                }

                Player t = Bukkit.getPlayer(args[1]);
                long amount = Long.parseLong(args[2]);

                targetUser.setCoins(targetUser.getCoins() - amount);

                sender.sendMessage(C.translate(profile.getColoredName() + " &aoyuncusunun kredi değeri &f" + MiscUtils.formatNumberByDecimal(Double.parseDouble(args[2])) + " &akadar silindi."));
                t.sendMessage(C.translate("&aKredi değeriniz &f" + MiscUtils.formatNumberByDecimal(Double.parseDouble(args[2])) + " &akadar silindi."));
                targetUser.saveProfile();
                break;
            }

            case ("give"): {
                Profile targetUser = TorchPlugin.getInstance().getProfileManager().getProfile(args[1]);


                if (targetUser == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cVeritabanında &f'" + args[0] + "' &cismine sahip bir oyuncu bulunamadı."));
                    return RedrockResultCommand.SUCCESS;
                }

                Player t = Bukkit.getPlayer(args[1]);
                long amount = Long.parseLong(args[2]);

                targetUser.setCoins(targetUser.getCoins() + amount);

                sender.sendMessage(C.translate(profile.getColoredName() + " &aoyuncusunun kredi değeri &f" + MiscUtils.formatNumberByDecimal(Double.parseDouble(args[2])) + " &akadar eklendi."));
                t.sendMessage(C.translate("&aKredi değerinize &f" + MiscUtils.formatNumberByDecimal(Double.parseDouble(args[2])) + " &akadar eklendi."));
                targetUser.saveProfile();
                break;
            }
        }
        return RedrockResultCommand.SUCCESS;
    }
}
