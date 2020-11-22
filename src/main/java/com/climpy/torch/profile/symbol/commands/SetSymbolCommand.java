package com.climpy.torch.profile.symbol.commands;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.symbol.menu.SymbolMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSymbolCommand extends RedrockCommand {

    public SetSymbolCommand() {
        super("setsymbol");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile user = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = (Player) sender;

        if (sender instanceof Player && !player.isOp() && !user.getRank().hasPermission("torch.symbol")) {
            sender.sendMessage(ChatColor.RED + "Yetkiniz yok.");
            return RedrockResultCommand.SUCCESS;
        }

        SymbolMenu.getInventory(user).open(user.toPlayer());

     /*   if (args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cDoğru Kullanım: /" + label + " [Oyuncu-Adı] <symbolID>"));
            return RedrockResultCommand.SUCCESS;
        }

        Profile targetUser = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);

        if (targetUser == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f" + args[0] + " &cismine sahip bir oyuncu bulunamadı."));
            return RedrockResultCommand.SUCCESS;
        }

        SymbolType symbolType;

        try {
            symbolType = SymbolType.valueOf(args[1].toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cGirilen &f" + args[1] + " &csembolü bulunamadı. "));
            return RedrockResultCommand.SUCCESS;
        }

        Player t = Bukkit.getPlayer(args[0]);

        targetUser.setSymbolType(symbolType);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',user.getRank().getColor() + t.getName() + " &aoyuncusunun sembolü &f" + symbolType.getDisplayName() + " &aolarak güncellendi."));
        t.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSembolünüz &f" + symbolType.getDisplayName() + " &aolarak güncellendi."));
        targetUser.saveProfile(); */
        return RedrockResultCommand.SUCCESS;
    }
}
