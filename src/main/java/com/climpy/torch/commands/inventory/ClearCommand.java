package com.climpy.torch.commands.inventory;

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
import org.bukkit.inventory.PlayerInventory;

public class ClearCommand extends RedrockCommand {
    public ClearCommand() {
        super("clear", "Envanteri siler.", new String[]{"clearinventory", "invclear"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        DefaultServerProvider defaultServerProvider = new DefaultServerProvider();
        if (!profile.toPlayer().hasPermission("torch.command.clear")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (defaultServerProvider.getServerType() == ServerType.LOBI) {
            sender.sendMessage(C.RED + "Lobi sunucularında bu komutu kullanamazsınız.");
            return RedrockResultCommand.SUCCESS;
        }

        if (args.length < 1) {
            PlayerInventory targetInventory = player.getInventory();
            targetInventory.setHelmet(null);
            targetInventory.setChestplate(null);
            targetInventory.setLeggings(null);
            targetInventory.setBoots(null);
            targetInventory.clear();
            sender.sendMessage(C.GREEN + "Envanteriniz silindi.");
        }

        if (args.length == 1) {
            Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);
            if (targetProfile == null) {
                sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            Player targetPlayer = Bukkit.getPlayer(targetProfile.getUniqueUUID());
            if (targetPlayer == null) {
                sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            PlayerInventory targetInventory = targetPlayer.getInventory();
            targetInventory.setHelmet(null);
            targetInventory.setChestplate(null);
            targetInventory.setLeggings(null);
            targetInventory.setBoots(null);
            targetInventory.clear();

            if (targetProfile == profile) {
                sender.sendMessage(C.GREEN + "Envanteriniz silindi.");
            } else {
                targetPlayer.sendMessage(C.GREEN + "Envanteriniz " + C.WHITE + profile.getColoredName() + C.GREEN + " tarafından silindi.");
                sender.sendMessage(C.GREEN + targetPlayer.getCompassTarget() + " adlı oyuncunun envanteri silindi.");
            }

            return RedrockResultCommand.SUCCESS;
        }

        return RedrockResultCommand.SUCCESS;
    }
}