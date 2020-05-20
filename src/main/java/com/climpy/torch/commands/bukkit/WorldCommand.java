package com.climpy.torch.commands.bukkit;

import com.climpy.redrock.commands.RedrockCommand;
import com.climpy.redrock.commands.RedrockResultCommand;
import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand extends RedrockCommand {
    public WorldCommand() {
        super("world", "Dünyaları yönetme komutu.", new String[]{"dünya"});
    }

    @Override
    public RedrockResultCommand execute(CommandSender sender, Command command, String label, String[] args) {
        Profile profile = TorchPlugin.getInstance().getProfileManager().getProfile(sender.getName());
        Player player = Bukkit.getPlayer(sender.getName());
        if (!profile.toPlayer().hasPermission("torch.command.world")) {
            return RedrockResultCommand.INVALID_PERMISSION;
        }

        if(args.length < 1) {
            profile.toPlayer().sendMessage(C.RED + "Kullanım: /" + label + " <create/teleport/list> [dünya adı..]");
            return RedrockResultCommand.SUCCESS;
        }

        switch (args[0]) {
            case "create" : {
                String worldName = "";

                for (String arg : args)
                    worldName = arg;

                player.sendMessage(C.translate("&f\"" + worldName + "\"" + " &9ismine sahip bir dünya oluşturuluyor.."));
                World world = Bukkit.createWorld(WorldCreator.name(worldName));
                player.sendMessage(C.translate("&f\"" + worldName + "\"" + " &9ismine sahip dünya oluşturuldu, ışınlanıyorsunuz.."));
                player.teleport(new Location(world, 0.0D, 80.0D, 0.0D));
                break;
            } case "teleport" : {
                String worldName = "";
                for (String arg : args)
                    worldName = arg;
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    player.teleport(new Location(world, 0.0D, 80.0D, 0.0D));
                    player.sendMessage(C.WHITE + "\"" + worldName + "\"" + C.BLUE + " dünyasına ışınlandınız.");
                } else {
                    player.sendMessage("§cBöyle bir dünya bulunamadı, oluşturmak için;");
                    player.sendMessage("§c/createworld " + args[0]);
                }
                break;
            } case "list" : {
                String[] worldNames = new String[Bukkit.getServer().getWorlds().size()];
                int count = 0;
                for (World w : Bukkit.getServer().getWorlds()) {
                    worldNames[count] = w.getName();
                    count++;
                }

                player.sendMessage(C.YELLOW + "Dünyalar (" + Bukkit.getWorlds().size() + ")");
                for (String s : worldNames) {
                    player.sendMessage(C.GRAY + "* " + C.WHITE + s);
                }
            }
        }

        return null;
    }
}
