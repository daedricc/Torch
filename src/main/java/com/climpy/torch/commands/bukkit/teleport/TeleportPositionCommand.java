package com.climpy.torch.commands.bukkit.teleport;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportPositionCommand extends RedrockCommand {
    public TeleportPositionCommand() {
        super("tppos", "Belirli bir koordinata ışınlanma komutu.", new String[]{"teleportpos", "teleportposition"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!profile.toPlayer().hasPermission("torch.command.teleport.position")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length != 3) {
            sender.sendMessage(C.RED + "Kullanım: /" + label + " [x] [y] [z]");
            return RedrockResultCommand.SUCCESS;
        }


        if (isDouble(args[0]) || isDouble(args[1]) || isDouble(args[2])) {
            sender.sendMessage(C.RED + "Kullanım: /" + label + " [x] [y] [z]");
            return RedrockResultCommand.SUCCESS;
        }

        double x = Double.parseDouble(args[0]) + 0.5;
        double y = Double.parseDouble(args[1]) + 0.5;
        double z = Double.parseDouble(args[2]) + 0.5;
        Location loc = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
        player.teleport(loc);
        player.sendMessage(C.GREEN + "Belirtilen kordinatlara ışınlandınız. §7(§a" + x + "§d, §a" + y + "§d, §a" + z + "§7)");
        return RedrockResultCommand.SUCCESS;
    }

    public boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
