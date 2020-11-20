package com.climpy.torch.commands.bukkit;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.redrock.utils.MiscUtils;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.rank.Rank;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListCommand extends RedrockCommand {
    public ListCommand() {
        super("list", "Oyuncular listelenir.", new String[]{"oyuncular", "liste"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String s, String[] strings) {
        StringBuilder str = new StringBuilder();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getName());
            String displayName;
            if (profile == null) {
                displayName = player.getName();
            } else {
                if(profile.isVanishStatus()) {
                    displayName = "";
                }

                displayName = profile.getColoredName();
            }

            str.append(displayName).append(ChatColor.GRAY).append(", ");
        }

        sender.sendMessage(C.GRAY + "(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ") " + MiscUtils.separatorCheck(str.toString(), ", "));
        return RedrockResultCommand.SUCCESS;
    }
}
