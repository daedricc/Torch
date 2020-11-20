package com.climpy.torch.commands.core;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends RedrockCommand {
    public FlyCommand() {
        super("fly", "Uçuş modunu açar/kapatır.", new String[]{"flight", "uç", "uçuş"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String s, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = (Player) sender;
        if (!profile.toPlayer().hasPermission("torch.command.fly")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length == 0) {
            player.setAllowFlight(!player.getAllowFlight());
            player.sendMessage(player.getAllowFlight() ? C.BLUE + "Uçuş modu " + C.WHITE + "açıldı." : C.BLUE + "Uçuş modu " + C.WHITE + "kapatıldı.");
            return RedrockResultCommand.SUCCESS;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (player.getAllowFlight()) {
                player.sendMessage(C.RED + "Zaten uçuş modu açık.");
                return RedrockResultCommand.SUCCESS;
            }

            player.setAllowFlight(true);
            player.sendMessage(C.BLUE + "Uçuş modu " + C.WHITE + "açıldı.");
            return RedrockResultCommand.SUCCESS;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (!player.getAllowFlight()) {
                player.sendMessage(C.RED + "Zaten uçuş modu kapalı.");
                return RedrockResultCommand.SUCCESS;
            }

            player.setAllowFlight(false);
            player.sendMessage(C.BLUE + "Uçuş modu " + C.WHITE + "kapatıldı.");
            return RedrockResultCommand.SUCCESS;
        }

        return RedrockResultCommand.SUCCESS;
    }
}
