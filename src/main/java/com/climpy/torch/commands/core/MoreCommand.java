package com.climpy.torch.commands.core;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.redrock.server.provider.DefaultServerProvider;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand extends RedrockCommand {
    public MoreCommand() {
        super("more", "Daha fazla item verir.", new String[]{"dahası", "dahafazla", "daha"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String s, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = Bukkit.getPlayer(profile.getUniqueUUID());
        DefaultServerProvider defaultServerProvider = new DefaultServerProvider();
        if (!profile.toPlayer().hasPermission("torch.command.more")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            sender.sendMessage(C.RED + "Elinizde herhangi bir eşya bulunmamktadır.");
            return RedrockResultCommand.SUCCESS;
        }

        if (itemStack.getAmount() == itemStack.getMaxStackSize()) {
            sender.sendMessage(C.RED + "Elinizde ki eşya zaten maksimum miktarda. (" + itemStack.getMaxStackSize() + ")");
            return RedrockResultCommand.SUCCESS;
        }


        player.getItemInHand().setAmount(64);
        player.updateInventory();
        player.sendMessage(C.GREEN + "Elinizdeki eşya " + C.WHITE + "maksimum" + C.GREEN + " hale getirildi.");
        return RedrockResultCommand.SUCCESS;
    }
}
