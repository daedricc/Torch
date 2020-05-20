package com.climpy.torch.commands.core;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.PingUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PingCommand extends RedrockCommand implements TabCompleter {
    public PingCommand() {
        super("ping", "Gecikmeyi gösterir.", new String[]{"gecikme"});
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        Player p = (Player) sender;
        if (arguments.length > 1) {
            sender.sendMessage(C.RED + "Kullanım: " + C.BLUE + "/" + label + " <oyuncuAdı>");
            return true;
        }
        
        if (arguments.length == 0) {
            sender.sendMessage(C.YELLOW + "Ping değeri: " + C.WHITE + PingUtil.getPing((Player) sender) + "ms.");
        }

        if (arguments.length == 1) {
            Player target = Bukkit.getServer().getPlayerExact(arguments[0]);
            if (target == null) {
                sender.sendMessage(C.RED + "Böyle bir oyuncu yok.");
            } else {
                Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(target.getUniqueId());
                sender.sendMessage(C.WHITE + targetProfile.getColoredName() + C.YELLOW + " adlı oyuncunun ping değeri: " + C.WHITE +
                        PingUtil.getPing(target) + "ms.");
            }
        }

        return true;
    }

    @Override
    public RedrockResultCommand execute(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
        if (arguments.length > 1) {
            return Collections.emptyList();
        }

        return null;
    }
}
