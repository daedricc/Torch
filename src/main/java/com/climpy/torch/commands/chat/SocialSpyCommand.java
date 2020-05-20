package com.climpy.torch.commands.chat;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;

public class SocialSpyCommand extends RedrockCommand {
    public SocialSpyCommand() {
        super("socialspy", "Özel mesajları inceleme yeteneği.", new String[]{"gizlimesaj", "casusmodu"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.socialspy")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        boolean socialSpy = !profile.isSocialSpy();
        profile.setSocialSpy(socialSpy);
        profile.toPlayer().sendMessage(C.BLUE + "Casus Modu " + (socialSpy ? C.GREEN + "açıldı." : C.RED + "kapatıldı."));
        profile.saveProfile();
        return RedrockResultCommand.SUCCESS;
    }
}
