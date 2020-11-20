package com.climpy.torch.commands.bukkit;

import com.climpy.redrock.server.ServerType;
import com.climpy.redrock.server.provider.DefaultServerProvider;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCommand extends RedrockCommand {
    public DayCommand() {
        super("day", "Gündüz yapar.", new String[]{"gündüz"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = Bukkit.getPlayer(sender.getName());
        DefaultServerProvider defaultServerProvider = new DefaultServerProvider();
        if (!profile.toPlayer().hasPermission("torch.command.day")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (defaultServerProvider.getServerType() == ServerType.LOBI) {
            sender.sendMessage(C.RED + "Lobi sunucularında bu komutu kullanamazsınız.");
            return RedrockResultCommand.SUCCESS;
        }

        if (args.length < 1) {
            player.sendMessage(C.RED + "&cKullanım: /" + label + " [all/me]");
            return RedrockResultCommand.SUCCESS;
        }

        if (args[0].equals("me")) {
            player.getWorld().setFullTime(6000L);
            player.sendMessage(C.GREEN + "Tüm oyuncular için şimdi gündüz vakti.");
            return RedrockResultCommand.SUCCESS;
        }

        if (args[0].equals("player")) {
            player.setPlayerTime(6000L, false);
            player.sendMessage(C.GREEN + "Sadece sizin için şimdi gündüz vakti.");
            return RedrockResultCommand.SUCCESS;
        }

        return RedrockResultCommand.SUCCESS;
    }
}
