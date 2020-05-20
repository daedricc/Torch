package com.climpy.torch.commands.chat;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand extends RedrockCommand {
    public BroadcastCommand() {
        super("broadcast", "Duyuru yayınlar.", new String[]{"duyuru"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = Bukkit.getPlayer(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.broadcast")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 1) {
            sender.sendMessage(C.RED + "Kullanım: /" + label + " [bilgilendirme-mesajı..]");
            return RedrockResultCommand.SUCCESS;
        }

        String broadcastMessage = MiscUtils.joinStrings(args, 0);
        Bukkit.broadcastMessage(C.translate("&6[Duyuru] &r" + broadcastMessage));
        return RedrockResultCommand.SUCCESS;
    }
}
