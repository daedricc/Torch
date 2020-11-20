package com.climpy.torch.commands.bukkit.teleport;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportHereCommand extends RedrockCommand {
    public TeleportHereCommand() {
        super("tphere", "Hedefi size ışınlar.", new String[]{"teleporthere"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!profile.toPlayer().hasPermission("torch.command.teleport.here")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 1) {
            player.sendMessage(C.RED + "Kullanım: /" + label + " <oyuncuAdı>");
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);
            if (target == null) {
                sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            target.teleport(player);
            player.sendMessage(targetProfile.getColoredName() + C.BLUE + " oyuncusunu kendinize ışınlandınız.");
            target.sendMessage(profile.getColoredName() + C.BLUE + " isimli oyuncu kendisini size ışınladı.");

            return RedrockResultCommand.SUCCESS;
        }
        return RedrockResultCommand.SUCCESS;
    }
}
