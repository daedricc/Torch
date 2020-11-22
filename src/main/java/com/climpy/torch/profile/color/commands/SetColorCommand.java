package com.climpy.torch.profile.color.commands;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.color.menu.ColorMenu;
import com.climpy.torch.profile.symbol.menu.SymbolMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetColorCommand extends RedrockCommand {

    public SetColorCommand() {
        super("setcolor");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile user = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = (Player) sender;

        if (sender instanceof Player && !player.isOp() && !user.getRank().hasPermission("torch.symbol")) {
            sender.sendMessage(ChatColor.RED + "Yetkiniz yok.");
            return RedrockResultCommand.SUCCESS;
        }

        ColorMenu.getInventory(user).open(user.toPlayer());

        return RedrockResultCommand.SUCCESS;
    }
}
