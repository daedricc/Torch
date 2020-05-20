package com.climpy.torch;

import com.climpy.redrock.commands.RedrockRegisterCommand;
import com.climpy.torch.chat.manager.ChatManager;
import com.climpy.torch.commands.bukkit.*;
import com.climpy.torch.commands.bukkit.teleport.TeleportAllCommand;
import com.climpy.torch.commands.bukkit.teleport.TeleportCommand;
import com.climpy.torch.commands.bukkit.teleport.TeleportHereCommand;
import com.climpy.torch.commands.bukkit.teleport.TeleportPositionCommand;
import com.climpy.torch.commands.chat.*;
import com.climpy.torch.commands.core.*;
import com.climpy.torch.commands.inventory.ClearCommand;
import com.climpy.torch.commands.inventory.HatCommand;
import com.climpy.torch.config.FileConfig;
import com.climpy.torch.locale.Locale;
import com.climpy.torch.mongo.MongoConnection;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.profile.listener.ProfileListener;
import com.climpy.torch.profile.moderator.FollowManager;
import com.climpy.torch.profile.moderator.StaffManager;
import com.climpy.torch.profile.moderator.VanishManager;
import com.climpy.torch.profile.moderator.commands.FollowCommand;
import com.climpy.torch.profile.moderator.commands.StaffModeCommand;
import com.climpy.torch.profile.moderator.commands.UnFollowCommand;
import com.climpy.torch.profile.moderator.commands.VanishCommand;
import com.climpy.torch.profile.rank.Rank;
import com.climpy.torch.profile.rank.extnds.RankCommand;
import com.climpy.torch.profile.rank.extnds.SetRankCommand;
import com.climpy.torch.punishments.PunishmentListener;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.args.*;
import com.climpy.torch.punishments.history.HistoryCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.material.Torch;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class TorchPlugin extends JavaPlugin {
    public static TorchPlugin instance;

    private FileConfig mainConfig;
    private MongoConnection mongoConnection;

    public ProfileManager profileManager;
    public PunishmentManager punishmentManager;

    private RedrockRegisterCommand redrockRegisterCommand;

    private ChatManager chatManager;

    private FollowManager followManager;
    private StaffManager staffManager;
    private VanishManager vanishManager;

    @Override
    public void onEnable() {
        instance = this;

        registerManagers();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void registerManagers() {
        this.mainConfig = new FileConfig(this, "config.yml");

        Locale.replaceStrings();

        mongoConnection = new MongoConnection();
        mongoConnection.setupDatabaseConnection();

        this.profileManager = new ProfileManager();
        this.punishmentManager = new PunishmentManager();

        this.chatManager = new ChatManager(this);

        this.followManager = new FollowManager();
        this.staffManager = new StaffManager();
        this.vanishManager = new VanishManager();

        Rank.init();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ProfileListener(), this);
        pluginManager.registerEvents(new PunishmentListener(), this);
    }


    private void registerCommands() {
        this.redrockRegisterCommand = new RedrockRegisterCommand();

        this.redrockRegisterCommand.registerBukkitCommand(new TeleportAllCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new TeleportCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new TeleportHereCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new TeleportPositionCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new DayCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new GameModeCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new ListCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new NightCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new SetSlotsCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new WhitelistCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new WorldCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new BroadcastCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new ChatClearCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new MessageCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new PrivateMessagesSoundCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new PrivateMessagesVisibleCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new ReplyCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new SocialSpyCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new AltsCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new CoinsCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new FlyCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new HealCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new MoreCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new PingCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new SpeedCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new SudoCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new WhoisCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new ClearCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new HatCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new BanCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new BlacklistCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new IPBanCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new IPMuteCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new KickCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new MuteCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new UnBanCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new UnMuteCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new WarnCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new HistoryCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new RankCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new SetRankCommand(), TorchPlugin.getInstance());

        this.redrockRegisterCommand.registerBukkitCommand(new FollowCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new UnFollowCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new StaffModeCommand(), TorchPlugin.getInstance());
        this.redrockRegisterCommand.registerBukkitCommand(new VanishCommand(), TorchPlugin.getInstance());
    }

    public static TorchPlugin getInstance() { return instance; }
}
