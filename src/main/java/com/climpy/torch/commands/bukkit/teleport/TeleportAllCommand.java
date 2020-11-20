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

public class TeleportAllCommand extends RedrockCommand {
    public TeleportAllCommand() {
        super("teleportall", "Tüm herkesi sana ışınlar.", new String[]{"tpall"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!profile.toPlayer().hasPermission("torch.command.teleport.all")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length > 0) {
            setUsage(label);
            return RedrockResultCommand.INVALID_USAGE;
        }

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            if (onlinePlayers != player) {
                onlinePlayers.teleport(player);
                onlinePlayers.sendMessage(profile.getColoredName() + C.YELLOW + ", herkesi kendine ışınlandı.");
            }
        }

        player.sendMessage(C.GREEN + "Tüm oyuncular size ışınlandı.");
        return RedrockResultCommand.SUCCESS;
    }

}
