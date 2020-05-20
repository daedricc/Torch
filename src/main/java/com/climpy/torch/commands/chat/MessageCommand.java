package com.climpy.torch.commands.chat;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.chat.manager.ChatManager;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand extends RedrockCommand {
    public MessageCommand() {
        super("message", "Mesaj gönderme komutu.", new String[]{"msg", "tell", "m"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = Bukkit.getPlayer(sender.getName());

        if(args.length < 2) {
            sender.sendMessage(C.RED + "Kullanım: /" + label + " <oyuncuAdı> [Mesaj..]" );
            return RedrockResultCommand.SUCCESS;
        }

        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Profile targetProfile = profileManager.getProfile(args[0]);
        if(targetProfile ==  null) {
            player.sendMessage(C.WHITE + args[0] + C.RED + " sunucuda bulunamadı.");
            return RedrockResultCommand.SUCCESS;
        }

        String privateMessage = MiscUtils.joinStrings(args, 1);

        ChatManager chatManager = TorchPlugin.getInstance().getChatManager();
        chatManager.sendPrivateMessage(player.getUniqueId(), targetProfile.getUniqueUUID(), privateMessage);
        return RedrockResultCommand.SUCCESS;
    }
}
