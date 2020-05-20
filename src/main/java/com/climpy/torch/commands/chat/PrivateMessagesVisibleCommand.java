package com.climpy.torch.commands.chat;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;

public class PrivateMessagesVisibleCommand extends RedrockCommand {
    public PrivateMessagesVisibleCommand() {
        super("msgtoggle", "Özel mesajları açıp/kapatır.", new String[]{"messagevisible"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());

        boolean privateMessagesDisable = !profile.isMessagesVisible();
        profile.setMessagesVisible(privateMessagesDisable);
        profile.toPlayer().sendMessage(C.BLUE + "Özel Mesajlar " + (privateMessagesDisable ? C.GREEN + "açıldı." : C.RED + "kapatıldı."));
        profile.saveProfile();
        return RedrockResultCommand.SUCCESS;
    }
}
