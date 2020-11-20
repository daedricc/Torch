package com.climpy.torch.profile.rank.extnds;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.redrock.utils.C;
import com.climpy.redrock.utils.MiscUtils;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.profile.rank.Rank;
import com.climpy.torch.utils.ParseUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.StringJoiner;

public class RankCommand extends RedrockCommand {
    public RankCommand() {
        super("rank");
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        ProfileManager profileManager = TorchPlugin.getInstance().getProfileManager();
        Profile profile = profileManager.getProfile(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.moderator")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if (args.length == 0) {
            profile.toPlayer().sendMessage(C.CHAT_BAR);
            profile.toPlayer().sendMessage(C.BLUE + C.BOLD + "Rütbe Komutları");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " create [rütbe] " + C.GRAY + "- " + C.WHITE + "Yeni rütbe ekler");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " delete [rütbe] " + C.GRAY + "- " + C.WHITE + "Bir rütbe siler");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " setprefix [rütbe] <prefix> " + C.GRAY + "- " + C.WHITE + "Rütbenin prefixini ayarlar.");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " setcolor [rütbe] <renk> " + C.GRAY + "- " + C.WHITE + "Rütbenin rengini ayarlar");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " setweight [rank] <weight>" + C.GRAY + "- " + C.WHITE + "Rütbenin ağırlığını ayarlar");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " addperm [rank] <yetki> " + C.GRAY + "- " + C.WHITE + "Rütbeye yetki ekler");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " delperm [rank] <yetki> " + C.GRAY + "- " + C.WHITE + "Rütbeden yetki siler");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " dump [rank] " + C.GRAY + "- " + C.WHITE + "Rütbenin bilgilerini gösterir");
            profile.toPlayer().sendMessage(C.YELLOW + "/" + label + " list  " + C.GRAY + "- " + C.WHITE + "Tüm rütbeleri listeler");
            profile.toPlayer().sendMessage(C.CHAT_BAR);

            return RedrockResultCommand.SUCCESS;
        }

        switch (args[0]) {
            case "create" : {
                if (args.length != 2) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " create [rütbe]");
                    break;
                }

                String rankName = args[1];
                if (Rank.getRankByDisplayName(rankName) != null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe zaten var.");
                    break;
                }

                Rank rank = new Rank(rankName);
                rank.saveRanks();

                profile.toPlayer().sendMessage(C.WHITE + rank.getColoredName() + C.YELLOW + " adında bir küresel rütbe oluşturdunuz" + C.GRAY + ".");
                break;
            }

            case "delete" : {
                if (args.length != 2) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " delete [rütbe]");
                    break;
                }

                String rankName = args[1];
                Rank rank = Rank.getRankByDisplayName(rankName);
                if (rank == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe bulunamadı.");
                    break;
                }

                if (rankName.equals(rank.getDefaultRank().getDisplayName())) {
                    profile.toPlayer().sendMessage(C.RED + "Bu rütbeyi silemezsin.");
                    break;
                }

                rank.delete();
                profile.toPlayer().sendMessage(C.WHITE + rank.getColoredName() + C.YELLOW + " adındaki rütbeyi kaldırdınız" + C.GRAY + ".");
                break;
            }

            case "setprefix" : {
                if (args.length != 3) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " setprefix [rütbe] <prefix>");
                    break;
                }

                String rankName = args[1];
                Rank rank = Rank.getRankByDisplayName(rankName);
                if (rank == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe bulunamadı.");
                    break;
                }

                String prefix = C.translate(MiscUtils.joinStrings(args, 2));
                rank.setPrefix(prefix);
                rank.saveRanks();

                profile.toPlayer().sendMessage(C.WHITE + rank.getColoredName() + C.YELLOW + " rütbesinin prefixini " + C.WHITE + rank.getPrefix() + sender.getName() + C.YELLOW + " olarak güncellendiniz.");
                break;
            }

            case "setcolor" : {
                if (args.length != 3) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " setprefix [rütbe] <renk>");
                    break;
                }

                Rank rank;
                String rankName = args[1];
                rank = Rank.getRankByDisplayName(rankName);
                if (rank == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe bulunamadı.");
                    break;
                }

                String color;

                String childName;
                childName = rank.getColoredName();
                color = args[2];

                try {
                    rank.setColor(ChatColor.valueOf(color));
                    rank.saveRanks();
                } catch (Exception var11) {
                    profile.toPlayer().sendMessage(C.RED + "Bu renk geçersiz.");
                    profile.toPlayer().sendMessage(C.RED + "Örnek: RED, BLUE, AQUA, GREEN, YELLOW, GOLD, GRAY, WHITE, LIGHT_PURPLE, BLACK and DARK_(COLOR)");
                    break;
                }

                profile.toPlayer().sendMessage(C.translate(childName + " &erengi &f " + rank.getColoredName() + " &eiçin güncellendi."));
                break;
            } case "setweight" : {
                if (args.length != 3) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " setweight (rütbe) [ağırlık]");
                    break;
                }
                
                String rankName = args[1];
                Rank rank = Rank.getRankByDisplayName(rankName);
                if (rank == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe bulunamadı.");
                    break;
                }

                Integer weight = ParseUtils.tryParseInt(args[2]);
                if (weight == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu sayı geçersiz.");
                    break;
                }

                rank.setWeight(weight.intValue());
                rank.saveRanks();

                profile.toPlayer().sendMessage(C.translate(rank.getColoredName() + " &erütbesinin ağırlığı &f'" + rank.getWeight() + "' &eolarak güncellendi."));
                break;
            } case "addpermission" : case "addperm" : {
                if (args.length != 3) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " " + args[0] + " (rütbe) [permission]");
                    break;
                }

                String rankName = args[1];
                Rank rank = Rank.getRankByDisplayName(rankName);
                if (rank == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe bulunamadı.");
                    break;
                }

                String permission = args[2];
                if (rank.getPermissions().contains(permission)) {
                    profile.toPlayer().sendMessage(C.RED + rankName + " zaten " + permission + " yetkisine sahip.");
                    break;
                }

                rank.getPermissions().add(permission);
                rank.saveRanks();

                profile.toPlayer().sendMessage(C.translate(rank.getColoredName() + " &erütbesine &d'" + permission + "' &eyetkisi eklendi."));
                break;
            } case "removepermission" : case "removeperm" : case "delperm" :  {
                if (args.length != 3) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " " + args[0] + " (rütbe) [permission]");
                    break;
                }

                String rankName = args[1];
                Rank rank = Rank.getRankByDisplayName(rankName);
                if (rank == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe bulunamadı.");
                    break;
                }

                String permission = args[2];
                if (!rank.getPermissions().remove(permission)) {
                    profile.toPlayer().sendMessage(C.RED + rankName + " zaten " + permission + " yetkisine sahip değil.");
                    break;
                }

                rank.saveRanks();
                profile.toPlayer().sendMessage(C.translate(rank.getColoredName() + " &erütbesine &d'" + permission + "' &eyetkisi kaldırıldı."));
                break;
            } case "list" : {
                profile.toPlayer().sendMessage(C.CHAT_BAR);
                profile.toPlayer().sendMessage(C.translate("&6&lRütbeler"));
                Rank.getRanks().values().stream().sorted(Comparator.comparingInt(Rank::getWeight)).forEach(ranks -> profile.toPlayer().sendMessage(C.translate(" &f- " + ranks.getColoredName() + " &r" + (ranks.isDefaultRank() ? "&7(Varsayılan Rütbe)" : "") + " (Ağırlık: " + ranks.getWeight() + ") ")));
                profile.toPlayer().sendMessage(C.CHAT_BAR);
                break;
            } case "dump" : {
                if (args.length != 2) {
                    profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " dump (rütbe)");
                    break;
                }

                String rankName = args[1];
                Rank rank = Rank.getRankByDisplayName(rankName);
                if (rank == null) {
                    profile.toPlayer().sendMessage(C.RED + "Bu isme sahip bir rütbe mevcut değil.");
                    break;
                }

                profile.toPlayer().sendMessage(C.CHAT_BAR);
                profile.toPlayer().sendMessage(C.translate("&6Rütbe Bilgisi &7(&r" + rank.getColoredName() + "&7)"));
                profile.toPlayer().sendMessage(C.translate("&eAğırlık&7: &r" + rank.getWeight()));
                profile.toPlayer().sendMessage(C.translate("&eÜnvan&7: &r" + rank.getPrefix() + "Ornek"));
                profile.toPlayer().sendMessage(C.translate("&eRenk&7: &r" + rank.getColor().name()));
                profile.toPlayer().sendMessage(" ");
                profile.toPlayer().sendMessage(C.translate("&eYetkiler: &r(" + rank.getAllPermissions().size() + ")"));
                StringJoiner perm = new StringJoiner(", ");
                for (String permissions : rank.getAllPermissions())
                    perm.add(permissions);

                if (rank.getAllPermissions().size() > 0)
                    profile.toPlayer().sendMessage(C.translate("&r" + perm.toString()));


                profile.toPlayer().sendMessage(C.CHAT_BAR);
            break;
        }
    }

        return RedrockResultCommand.SUCCESS;
    }
}

