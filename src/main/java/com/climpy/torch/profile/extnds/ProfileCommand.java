package com.climpy.torch.profile.extnds;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ProfileCommand extends RedrockCommand {
    public ProfileCommand() {
        super("profile", "Profillere ve profile dair değiştirebilireceğiniz ayarlar.", new String[]{"profil", "hesap"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command cmd, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if(!profile.toPlayer().hasPermission("torch.moderator")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if(args.length < 2) {
            profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " <oyuncu> <Değer 1> [Değer 2]");
            profile.toPlayer().sendMessage(C.RED + "Değer 1 için oluşturulan değerler: " + C.WHITE + "rank, coins");
            return RedrockResultCommand.SUCCESS;
        }

        Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);
        if(targetProfile == null) {
            profile.toPlayer().sendMessage(C.RED + "Oyuncu veritabanında bulunamadı.");
            return RedrockResultCommand.SUCCESS;
        }

        return RedrockResultCommand.SUCCESS;
    }
}
