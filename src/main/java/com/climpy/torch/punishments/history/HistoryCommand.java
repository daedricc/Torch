package com.climpy.torch.punishments.history;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.redrock.redis.data.DataPublisher;
import com.climpy.redrock.utils.C;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.punishments.Punishment;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.history.menu.HistoryCategoryMenu;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HistoryCommand extends RedrockCommand {
    public HistoryCommand() {
        super("history");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = Bukkit.getPlayer(sender.getName());
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
            if (!profile.toPlayer().hasPermission("redrock.moderator")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }

            if (args.length < 1) {
                setUsage(label + " <oyuncuAdı>");
                return RedrockResultCommand.INVALID_USAGE;
            }

            switch (args[0]) {
                case "clear": {
                    if (args.length < 2) {
                        setUsage(label + " clear <oyuncuAdı>");
                        return RedrockResultCommand.SUCCESS;
                    }

                    Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[1]);
                    if (targetProfile == null) {
                    //    sender.sendMessage(Locale.format(Locale.PLAYER_NOT_FOUND));
                        return RedrockResultCommand.SUCCESS;
                    }

                    List<Punishment> punishments = targetProfile.getPunishments();
                    if (punishments.isEmpty()) {
                        sender.sendMessage(C.RED + "Bu kullanıcının ceza geçmişi bulunamadı.");
                        return RedrockResultCommand.SUCCESS;
                    }

                    DataPublisher dataPublisher = RedrockPlugin.getInstance().getRedisConnection().getDataPublisher();
                    JsonObject punishmentClearObject = new JsonObject();
                    punishmentClearObject.addProperty("uniqueUUID", targetProfile.getUniqueUUID().toString());
                    dataPublisher.write("punishment_clear", punishmentClearObject);

                //    sender.sendMessage(targetProfile.getActiveRank().getColor() + targetProfile.getName() + C.GREEN + " adlı oyuncunun ceza geçmişi temizlendi.");
                    break;
                }

                case "fetch": {
                    PunishmentManager punishmentManager = TorchPlugin.getInstance().getPunishmentManager();
                    for (UUID uuid : punishmentManager.getDatabasePunishments().keySet()) {
                        Profile profile1 = TorchPlugin.getInstance().getProfileManager().getProfile(uuid);
                        if (profile1 == null) {
                            continue;
                        }

                        DataPublisher dataPublisher = RedrockPlugin.getInstance().getRedisConnection().getDataPublisher();
                        JsonObject punishmentClearObject = new JsonObject();
                        punishmentClearObject.addProperty("uniqueUUID", profile1.getUniqueUUID().toString());
                        dataPublisher.write("punishment_clear", punishmentClearObject);
                    }

                    sender.sendMessage(C.GREEN + "Veritabanındaki tüm cezalar temizlendi.");
                    break;
                }

                default: {
                    Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(args[0]);
                    if (targetProfile == null) {
                       // sender.sendMessage(Locale.format(Locale.PLAYER_NOT_FOUND));
                        return RedrockResultCommand.SUCCESS;
                    }

                    List<Punishment> punishments = targetProfile.getPunishments();
                    if (punishments.isEmpty()) {
                        sender.sendMessage(C.RED + "Bu kullanıcının ceza geçmişi bulunamadı.");
                        return RedrockResultCommand.SUCCESS;
                    }

                    HistoryCategoryMenu.getInventory(targetProfile).open(player);
                }
            }

            return RedrockResultCommand.SUCCESS;
        }

        return RedrockResultCommand.CONSOLE_ONLY;
    }
}
