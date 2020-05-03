package com.climpy.torch;

import com.climpy.redrock.RedrockPlugin;
import com.climpy.redrock.commands.RedrockRegisterCommand;
import com.climpy.torch.config.FileConfig;
import com.climpy.torch.mongo.MongoConnection;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.profile.listener.ProfileListener;
import com.climpy.torch.profile.rank.Rank;
import com.climpy.torch.profile.rank.extnds.RankCommand;
import com.climpy.torch.punishments.PunishmentListener;
import com.climpy.torch.punishments.PunishmentManager;
import com.climpy.torch.punishments.args.*;
import com.climpy.torch.punishments.history.HistoryCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
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

        mongoConnection = new MongoConnection();
        mongoConnection.setupDatabaseConnection();

        this.profileManager = new ProfileManager();
        this.punishmentManager = new PunishmentManager();

        Rank.init();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ProfileListener(), this);
        pluginManager.registerEvents(new PunishmentListener(), this);
    }


    private void registerCommands() {
        this.redrockRegisterCommand = new RedrockRegisterCommand();

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
    }

    public static TorchPlugin getInstance() { return instance; }
}
