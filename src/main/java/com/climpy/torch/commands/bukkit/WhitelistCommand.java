package com.climpy.torch.commands.bukkit;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.redrock.redis.RedisManager;
import com.climpy.redrock.redis.data.DataPublisher;
import com.climpy.redrock.redis.messages.WhitelistSubscriber;
import com.climpy.redrock.server.ServerData;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class WhitelistCommand extends RedrockCommand {
    public WhitelistCommand() {
        super("whitelist", "Beyaz liste modunu açar.", new String[]{"beyazliste"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.whitelist")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length < 2) {
            String STRING_SEPARATOR = "\n";
            String stringBuilder = ChatColor.RED + "Kullanım: /whitelist (add:remove:on:off) [oyuncuAdı/sunucuAdı]";
            sender.sendMessage(stringBuilder);
            return RedrockResultCommand.SUCCESS;
        }

        DataPublisher dataPublisher = RedrockPlugin.getInstance().getRedisConnection().getDataPublisher();
        JsonObject whitelistObject = new JsonObject();
        if (args[0].equals("add") || args[0].equals("ekle") || args[0].equals("remove") || args[0].equals("delete") || args[0].equals("sil")) {
            Profile targetUser = TorchPlugin.getInstance().getProfileManager().getProfile(args[1]);
            if (targetUser == null) {
                sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            if (args[0].equals("add") || args[0].equals("ekle")) {
                WhitelistSubscriber.WhitelistType whitelistType = WhitelistSubscriber.WhitelistType.ADD;
                whitelistObject.addProperty("type", whitelistType.getInternalName());
                whitelistObject.addProperty("target", targetUser.getUniqueUUID().toString());
                sender.sendMessage(ChatColor.GRAY + "[Network] " + ChatColor.GREEN + "'" + targetUser.getName() + "' beyazlisteye eklendi.");
                RedrockPlugin.getInstance().getLogger().log(Level.INFO, "[Whitelist] " + targetUser.getName() + " beyaz-listeye eklendi.");
            } else if (args[0].equals("remove") || args[0].equals("delete") || args[0].equals("sil")) {
                WhitelistSubscriber.WhitelistType whitelistType = WhitelistSubscriber.WhitelistType.REMOVE;
                whitelistObject.addProperty("type", whitelistType.getInternalName());
                whitelistObject.addProperty("target", targetUser.getUniqueUUID().toString());
                sender.sendMessage(ChatColor.GRAY + "[Network] " + ChatColor.GREEN + "'" + targetUser.getName() + "' beyaz-listeden silindi.");
                RedrockPlugin.getInstance().getLogger().log(Level.INFO, "[Whitelist] " + targetUser.getName() + " beyaz-listeden silindi.");
            }

        } else if (args[0].equals("on") || args[0].equals("open") || args[0].equals("aç") || args[0].equals("off") || args[0].equals("disable") || args[0].equals("kapat")) {
            RedisManager redisManager = RedrockPlugin.getInstance().getRedisManager();
            ServerData serverData = redisManager.getServerByName(args[1]);
            if (serverData == null) {
                sender.sendMessage(ChatColor.RED + "Girilen sunucu bulunamadı.");
                return RedrockResultCommand.SUCCESS;
            }

            if (args[0].equals("on") || args[0].equals("open") || args[0].equals("aç")) {
                if (serverData.isWhitelisted()) {
                    sender.sendMessage(ChatColor.RED + serverData.getServerName() + " beyazliste modunda.");
                    return RedrockResultCommand.SUCCESS;
                }

                WhitelistSubscriber.WhitelistType whitelistType = WhitelistSubscriber.WhitelistType.ON;
                whitelistObject.addProperty("type", whitelistType.getInternalName());
                whitelistObject.addProperty("target", serverData.getServerName());
                RedrockPlugin.getInstance().getServer().broadcastMessage(ChatColor.WHITE + serverData.getServerName() + ChatColor.GOLD + " beyazlisteye girdi.");
                RedrockPlugin.getInstance().getLogger().log(Level.INFO, "[Whitelist] " + serverData.getServerName() + " için beyaz-liste açıldı.");
            } else if (args[0].equals("off") || args[0].equals("disable") || args[0].equals("kapat")) {
                if (!serverData.isWhitelisted()) {
                    sender.sendMessage(ChatColor.RED + serverData.getServerName() + " zaten beyazlistede değil.");
                    return RedrockResultCommand.SUCCESS;
                }

                WhitelistSubscriber.WhitelistType whitelistType = WhitelistSubscriber.WhitelistType.OFF;
                whitelistObject.addProperty("type", whitelistType.getInternalName());
                whitelistObject.addProperty("target", serverData.getServerName());
                RedrockPlugin.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "[Network] " + ChatColor.GREEN + serverData.getServerName() + " beyazlisteden kaldırıldı.");
                RedrockPlugin.getInstance().getLogger().log(Level.INFO, "[Whitelist] " + serverData.getServerName() + " için beyazliste kaldırıldı.");
            }
        }
        if (args[0].equals("reload") || args[0].equals("yenile")) {
            RedrockPlugin.getInstance().getServer().reloadWhitelist();
            RedrockPlugin.getInstance().getServer().broadcastMessage(ChatColor.GREEN + "Beyazliste sunucuları yeniden yüklendi.");
            RedrockPlugin.getInstance().getLogger().log(Level.WARNING, "[Whitelist] Veriler beyaz-liste sunucusu için yeniden yüklendi.");
        }
        dataPublisher.write("whitelist", whitelistObject);
        return RedrockResultCommand.SUCCESS;
    }
}