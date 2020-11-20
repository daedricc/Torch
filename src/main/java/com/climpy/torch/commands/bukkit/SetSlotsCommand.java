package com.climpy.torch.commands.bukkit;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.BukkitReflection;
import com.climpy.torch.utils.C;
import com.climpy.redrock.commands.RedrockResultCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSlotsCommand extends RedrockCommand {
    public SetSlotsCommand() {
        super("setslot", "Maksimum oyuncu limitini değiştirir.", new String[]{"slotayarla", "slotset", "setslots"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = Bukkit.getPlayer(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.setslots")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 1) {
            player.sendMessage(C.RED + "Kullanım: /" + label + " <slot-Sayısı>");
            return RedrockResultCommand.SUCCESS;
        }

        int slots = Integer.parseInt(args[0]);
        if (slots < 0) {
            player.sendMessage(C.RED + "'" + slots + "' geçerli bir sayı değil.");
            return RedrockResultCommand.SUCCESS;
        }

        BukkitReflection.setMaxPlayers(TorchPlugin.getInstance().getServer(), slots);
        Bukkit.broadcastMessage(C.RED + "[⚠] " + profile.getColoredName() + C.BLUE + " maksimum oyuncu sayısını " + C.WHITE + slots + C.BLUE + " olarak değiştirdi.");
        return RedrockResultCommand.SUCCESS;
    }
}
