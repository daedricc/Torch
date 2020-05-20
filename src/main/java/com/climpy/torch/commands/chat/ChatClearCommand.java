package com.climpy.torch.commands.chat;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatClearCommand extends RedrockCommand {
    public ChatClearCommand() {
        super("chatclear", "Sohbeti temizler.", new String[]{"cc", "sohbetisil"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String s, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if (sender instanceof Player) {
            if (!profile.toPlayer().hasPermission("torch.command.broadcast")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                onlinePlayer.sendMessage(new String[101]);
                onlinePlayer.sendMessage(C.BLUE + "[⚠] " + C.AQUA + "Sunucu sohbeti temizlendi.");
                if (args.length >= 1)
                    onlinePlayer.sendMessage(C.BLUE + "Neden: " + C.WHITE + StringUtils.join(args, " "));
            });

            return RedrockResultCommand.SUCCESS;
        }
        sender.sendMessage("Bu komut sadece oyuncular tarafindan yurutulur.");
        return RedrockResultCommand.SUCCESS;
    }
}
