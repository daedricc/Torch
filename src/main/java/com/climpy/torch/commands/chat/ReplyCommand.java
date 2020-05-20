package com.climpy.torch.commands.chat;

import com.climpy.torch.TorchPlugin;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.chat.manager.ChatManager;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand extends RedrockCommand {
    public ReplyCommand() {
        super("reply", "", new String[]{"cevap", "r"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());

        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        UUID lastRepliedPlayer = profile.getLastRepliedPlayer();
        if (lastRepliedPlayer == null) {
            player.sendMessage(C.RED + "Daha önce herhangi bir oyuncuya mesaj atmamışsınız.");
            return RedrockResultCommand.SUCCESS;
        }

        Profile targetProfile = profileManager.getProfile(profile.getLastRepliedPlayer());
        Player targetPlayer = Bukkit.getPlayer(targetProfile.getUniqueUUID());
        if (targetPlayer == null) {
            sender.sendMessage(C.RED + "En son mesaj yazılan kişi şu anda sunucu değil.");
            return RedrockResultCommand.SUCCESS;
        }

        String privateMessage = MiscUtils.joinStrings(args, 0);

        ChatManager chatManager = TorchPlugin.getInstance().getChatManager();
        chatManager.sendPrivateMessage(player.getUniqueId(), targetProfile.getUniqueUUID(), privateMessage);
        return RedrockResultCommand.SUCCESS;
    }
}
