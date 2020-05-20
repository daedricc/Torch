package com.climpy.torch.commands.core;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.redis.player.RedisPlayerCache;
import com.climpy.redrock.redis.player.RedisPlayerData;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.profile.menu.ProfileMenu;
import com.climpy.torch.utils.C;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhoisCommand extends RedrockCommand implements TabCompleter {
    public WhoisCommand() {
        super("whois", "Kimdir bu?", new String[]{"kim", "kimdir", "kimlik"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String s, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = Bukkit.getPlayer(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.whois")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }


        if (args.length < 1) {
            sender.sendMessage(C.translate("&cDoğru Kullanım: /" + s + " <oyuncuAdı>"));
            return RedrockResultCommand.SUCCESS;
        }

        Profile targetUser = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);

        if (targetUser == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cVeritabanında &f'" + args[0] + "' &cismine sahip bir oyuncu bulunamadı."));
            return RedrockResultCommand.SUCCESS;
        }

        RedisPlayerCache redisPlayerCache = RedrockPlugin.getInstance().getRedisPlayerCache();
        RedisPlayerData redisPlayerData = redisPlayerCache.getRedisPlayerData(targetUser.getUniqueUUID());
        if (redisPlayerData == null) {
            sender.sendMessage(ChatColor.RED + "Oyuncu ağımız üzerinde kayıtlı durumda değil.");
            return RedrockResultCommand.SUCCESS;
        }

        ProfileMenu.getInventory(profile).open(player);
        return RedrockResultCommand.SUCCESS;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Validate.notNull(sender, "Sender boş olamaz");
        Validate.notNull(args, "Arguments boş olamaz");
        Validate.notNull(label, "Alias boş olamaz");
        if (args.length == 1) {
            List<String> onlinePlayers = new ArrayList<>();
            for (Profile user : TorchPlugin.getInstance().getProfileManager().getDatabaseProfiles()) {
                if (user.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    onlinePlayers.add(user.getName());
            }
            return onlinePlayers;
        }
        return ImmutableList.of();
    }
}
