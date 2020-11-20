package com.climpy.torch.commands.bukkit;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.utils.C;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import com.climpy.redrock.commands.RedrockCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModeCommand extends RedrockCommand implements TabExecutor {
    public GameModeCommand() {
        super("gamemode", "Oyun modunu değiştirir.", new String[]{"oyunmodu", "gm"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (sender instanceof Player) {
            Player targetPlayer;
            Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (!profile.toPlayer().hasPermission("torch.command.gamemode")) {
                return RedrockResultCommand.INVALID_PERMISSION;
            }

            if (args.length < 1) {
                sender.sendMessage(C.RED + "Kullanım: /" + label + " <oyun-Modu> <oyuncuAdı>");
                return RedrockResultCommand.SUCCESS;
            }

            GameMode gameMode = gameModeName(args[0]);
            if (gameMode == null) {
                sender.sendMessage(C.RED + "Girilen oyun modu '" + args[0] + "' geçersiz.");
                return RedrockResultCommand.SUCCESS;
            }

            if (args.length == 2) {
                if (profile.toPlayer().hasPermission("torch.command.gamemode")) {
                    targetPlayer = Bukkit.getPlayerExact(args[1]);
                    if (targetPlayer == null) {
                        sender.sendMessage(C.RED + "Oyuncu bulunamadı.");
                        return RedrockResultCommand.SUCCESS;
                    }
                } else {
                    targetPlayer = (Player) sender;
                }
            } else {
                targetPlayer = (Player) sender;
            }

            if (targetPlayer.getGameMode() == gameMode) {
                sender.sendMessage(C.RED + "Oyun modu " + targetPlayer.getName() + " için zaten " + C.WHITE + gameMode.name() + C.RED + '.');
                return RedrockResultCommand.SUCCESS;
            }

            targetPlayer.setGameMode(gameMode);
            if (targetPlayer == sender) {
                sender.sendMessage(C.BLUE + "Oyun modu sizin için " + C.WHITE + gameMode.name() + C.BLUE + " olarak ayarlandı.");
                return RedrockResultCommand.SUCCESS;
            }

            Command.broadcastCommandMessage(sender, C.BLUE + "Oyun modu " + targetPlayer.getName() + C.BLUE + " için " + C.WHITE + gameMode.name() + C.BLUE + " olarak ayarlandı.");
            return RedrockResultCommand.SUCCESS;
        }

        sender.sendMessage("Bu islem sadece oyundan yurutulebilir.");
        return RedrockResultCommand.SUCCESS;
    }


    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Validate.notNull(sender, "Sender bos olamaz");
        Validate.notNull(args, "Arguments bos olamaz");
        Validate.notNull(label, "Alias bos olamaz");

        if (args.length == 2) {
            List<String> onlinePlayers = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.canSee(player) && player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    onlinePlayers.add(player.getName());
                }
            }

            return onlinePlayers;
        }

        return null;
    }

    private GameMode gameModeName(String gameModeName) {
        if (gameModeName.toLowerCase().equals("a") || gameModeName.toLowerCase().equals("advanture") || gameModeName.toLowerCase().equals("2") || gameModeName.toLowerCase().equals("advan"))
            return GameMode.ADVENTURE;
        if (gameModeName.toLowerCase().equals("c") || gameModeName.toLowerCase().equals("creative") || gameModeName.toLowerCase().equals("1") || gameModeName.toLowerCase().equals("creat"))
            return GameMode.CREATIVE;
        if (gameModeName.toLowerCase().equals("s") || gameModeName.toLowerCase().equals("survival") || gameModeName.toLowerCase().equals("0") || gameModeName.toLowerCase().equals("surv"))
            return GameMode.SURVIVAL;
        if (gameModeName.toLowerCase().equals("spec") || gameModeName.toLowerCase().equals("spectator") || gameModeName.toLowerCase().equals("3") || gameModeName.toLowerCase().equals("sp")) {
            return GameMode.SPECTATOR;
        }
        return null;
    }
}
