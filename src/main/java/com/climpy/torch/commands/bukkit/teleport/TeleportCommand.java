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

public class TeleportCommand extends RedrockCommand {
    public TeleportCommand() {
        super("teleport", "Işınlanma komutu.", new String[]{"tp"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!profile.toPlayer().hasPermission("torch.command.teleport")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 1) {
            player.sendMessage(C.RED + "Kullanım: /" + label + " <oyuncuAdı> <isteğe bağlı: 2. oyuncuAdı>");
        }

        if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            player.teleport(targetPlayer);
            sender.sendMessage(targetProfile.getColoredName() + C.BLUE + " oyuncusuna ışınlandın.");
            return RedrockResultCommand.SUCCESS;
        }

        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);

            Player target = Bukkit.getPlayer(args[1]);
            Profile targetProfile2 = TorchPlugin.getInstance().getProfileManager().getProfile(args[1]);
            if (targetPlayer == null && target == null) {
                sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            assert targetPlayer != null;
            targetPlayer.teleport(target);
            sender.sendMessage(targetProfile.getColoredName() + C.BLUE + ", " + targetProfile2.getColoredName() + C.BLUE + " oyuncusuna ışınlandı.");
            return RedrockResultCommand.SUCCESS;
        }

        return RedrockResultCommand.SUCCESS;
    }
}
