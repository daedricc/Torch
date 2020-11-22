package com.climpy.torch.profile.cosmetic.commands;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.color.menu.ColorMenu;
import com.climpy.torch.profile.cosmetic.menu.CosmeticMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CosmeticCommand extends RedrockCommand {
    public CosmeticCommand() {
        super("cosmetic", "Open the cosmetic menu", new String[]{"cosmetics"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String s, String[] strings) {
        Profile user = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = (Player) sender;

        if (sender instanceof Player && !player.isOp() && !user.getRank().hasPermission("torch.cosmetic")) {
            sender.sendMessage(ChatColor.RED + "Yetkiniz yok.");
            return RedrockResultCommand.SUCCESS;
        }

        CosmeticMenu.getInventory(user).open(user.toPlayer());

        return RedrockResultCommand.SUCCESS;
    }
}
