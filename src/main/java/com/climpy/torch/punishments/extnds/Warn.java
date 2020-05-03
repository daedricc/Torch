package com.climpy.torch.punishments.extnds;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.punishments.Punishment;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.PunishmentType;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;

import java.util.UUID;

public class Warn extends Punishment {

    public Warn(String ipAddress, UUID uniqueUUID, UUID addedBy, String addedReason) {
        super(PunishmentType.WARN, ipAddress, uniqueUUID, addedBy, addedReason);
    }

    public Warn(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getWarningMessage(boolean active, boolean silent) {
        StringBuilder message = new StringBuilder((silent ? "[Sessiz]" : ""));
        Profile staffProfile = TorchPlugin.getInstance().getProfileManager().getProfile(active ? this.addedBy : this.removedBy);
        Profile targetProfile = TorchPlugin.getInstance().getProfileManager().getProfile(this.uniqueUUID);

       /* if (active) {
            message.append(targetProfile.getActiveRank().getColor()).append(targetProfile.getName()).append(ChatColor.GREEN).append(" adlı oyuncu uyarıldı, by ").
                    append(staffProfile.getActiveRank().getColor()).append(staffProfile.getName());
        }*/

        return message.toString();
    }

    @Override
    public StringBuilder getPunishmentMessage() {
        final PunishmentManager punishmentManager = TorchPlugin.getInstance().getPunishmentManager();
        int warnCount = punishmentManager.getPunishments(this.uniqueUUID, PunishmentType.WARN).size() + 1;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n").append("\n");
        stringBuilder.append(ChatColor.GREEN).append(ChatColor.BOLD).append("Uyarıldınız").append(ChatColor.GREEN).append("! ").append(warnCount).append(" adet uyarınız bulunmakta.").append("\n");
        stringBuilder.append(ChatColor.GREEN).append("Neden: ").append(ChatColor.WHITE).append(this.addedReason).append("\n");

        if ((warnCount + 1) % 10 == 0) {
            stringBuilder.append(ChatColor.AQUA).append(ChatColor.BOLD).append("Uyarı sayısı on ve onun katlarını geçtiğinde farklı sürelerde uzaklaştırma işlemleri hesabınız üzerine uygulanacaktır.");
        }

        stringBuilder.append("\n").append("\n ");
        return stringBuilder;
    }
}
