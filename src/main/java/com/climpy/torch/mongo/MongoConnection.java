package com.climpy.torch.mongo;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.utils.ConfigUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientException;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.logging.Level;

@Getter
public class MongoConnection {
    private MongoCredential credential;
    private MongoClient mongoClient;
    private CollectionManager collectionManager;

    public void setupDatabaseConnection() {
        try {
            ConfigUtils mongoDatabase = new ConfigUtils(TorchPlugin.getInstance().getMainConfig(), "mongodb");
            this.credential = MongoCredential.createCredential(mongoDatabase.getString("user"),
                    mongoDatabase.getString("database"),
                    mongoDatabase.getString("password").toCharArray());

            this.mongoClient = new MongoClient(new ServerAddress(mongoDatabase.getString("host"),
                    mongoDatabase.getInt("port")), Collections.singletonList(this.credential));

            this.collectionManager = new CollectionManager(this.mongoClient);
            this.collectionManager.createDataConnection();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Torch] [MongoDB]" + ChatColor.WHITE + " Veritabani sunucusuna basariyla baglanildi.");
        } catch (MongoClientException ex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Torch] [MongoDB]" + ChatColor.WHITE + " Çalisan, yerel Mongo Veritabani sunucusuna baglanilamiyor!");
            Bukkit.getServer().shutdown();
        }
    }

    public void disableDatabaseConnection() {
        if (this.mongoClient != null) {
            TorchPlugin.getInstance().getLogger().log(Level.INFO, "[MongoDB] Veritabani baglantisi kapatiliyor..");

            try {
                this.mongoClient.close();
                TorchPlugin.getInstance().getLogger().log(Level.INFO, "[MongoDB] Veritabani baglantisi basariyla kapatildi.");
            } catch (Exception ex) {
                TorchPlugin.getInstance().getLogger().log(Level.INFO, "[MongoDB] Veritabani verileri kaydedilemedi.");
            }
        }
    }
}