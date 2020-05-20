package com.climpy.torch.commands.chat;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;

public class PrivateMessagesSoundCommand extends RedrockCommand {
    public PrivateMessagesSoundCommand() {
        super("soundtoggle", "Özel mesajların sesini açıp/kapatır.", new String[]{"özelmesajsesleri", "privatemessagessound", "privmsgsound"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());

        boolean privateMessagesSound = !profile.isMessagingSounds();
        profile.setMessagingSounds(privateMessagesSound);
        profile.toPlayer().sendMessage(C.BLUE + "Özel Mesajlar için sesler " + (privateMessagesSound ? C.GREEN + "açıldı." : C.RED + "kapatıldı."));
        profile.saveProfile();
        return RedrockResultCommand.SUCCESS;
    }
}
