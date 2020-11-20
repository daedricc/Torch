package com.climpy.torch.commands.core;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.MiscUtils;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltsCommand extends RedrockCommand implements TabCompleter {
    public AltsCommand() {
        super("alts", "Alt hesapları görüntüler.", new String[]{"alt", "atlar"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.alts")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 1) {
            sender.sendMessage(C.RED + "Kullanım: /" + label + " <kullanıcıAdı>");
            return RedrockResultCommand.SUCCESS;
        }

        Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);
        if (targetProfile == null) {
            sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
            return RedrockResultCommand.SUCCESS;
        }

        StringBuilder alts = new StringBuilder();
        for (UUID altUuid : targetProfile.getAlts()) {
            Profile altProfile = TorchPlugin.getInstance().getProfileManager().getProfile(altUuid);

            if (altProfile != null) {
                alts.append(altProfile.getName()).append(", ");
            }
        }

        sender.sendMessage(C.CHAT_BAR);
        sender.sendMessage(C.YELLOW + "IP: " + targetProfile.getCurrentAddress());
        sender.sendMessage(C.YELLOW + "Alternatif Hesaplar: " + C.WHITE + (targetProfile.getAlts().isEmpty() ? "(0)" : "(" + targetProfile.getAlts().size() + ")"));
        sender.sendMessage(C.BLUE + " » " + C.RED + MiscUtils.separatorCheck(alts.toString(), ", "));
        sender.sendMessage(C.CHAT_BAR);
        return RedrockResultCommand.SUCCESS;
    }


    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Validate.notNull(sender, "Sender boş olamaz");
        Validate.notNull(args, "Arguments boş olamaz");
        Validate.notNull(label, "Alias boş olamaz");
        if (args.length == 1) {
            List<String> onlinePlayers = new ArrayList<>();
            for (Profile user : TorchPlugin.getInstance().getProfileManager().getDatabaseProfiles()) {
                if (user.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    onlinePlayers.add(user.getName());
            }
            return onlinePlayers;
        }
        return ImmutableList.of();
    }
}
