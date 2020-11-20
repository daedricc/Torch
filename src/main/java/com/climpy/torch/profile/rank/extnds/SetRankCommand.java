package com.climpy.torch.profile.rank.extnds;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.rank.menu.RankSelectionMenu;
import com.climpy.torch.utils.C;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetRankCommand extends RedrockCommand {
    public SetRankCommand() {
        super("setrank");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if(!profile.toPlayer().hasPermission("torch.moderator")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if(args.length < 1) {
            profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " <oyuncu>");
            return RedrockResultCommand.SUCCESS;
        }

        Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);
        if(targetProfile == null) {
            profile.toPlayer().sendMessage(C.RED + "Oyuncu veritabanında bulunamadı.");
            return RedrockResultCommand.SUCCESS;
        }


        RankSelectionMenu.getInventory(targetProfile).open(profile.toPlayer());

        return RedrockResultCommand.SUCCESS;
    }
}
