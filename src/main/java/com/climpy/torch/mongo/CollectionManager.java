package com.climpy.torch.mongo;

import com.climpy.torch.TorchPlugin;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@Getter @Setter
public class CollectionManager {
    private Set<String> collections;
    private MongoClient mongoClient;

    public CollectionManager(MongoClient mongoClient) {
        this.collections = new HashSet<>();
        this.mongoClient = mongoClient;

        this.collections.add("profiles");
        this.collections.add("ranks");
        this.collections.add("report");
    }

    public void createDataConnection() {

        try {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Torch] [MongoDB]" + ChatColor.WHITE + " Collection isllemde su anda kuruluyor..");

            try {
                MongoDatabase mongoDatabase = this.mongoClient.getDatabase("redrock");

                String[] names = new String[this.collections.size()];
                for (int index = 0; index < names.length; ) {
                    for (String collection : this.collections) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Torch] [MongoDB] " + ChatColor.YELLOW + "[" + (index + 1) + "] " + ChatColor.GRAY + collection + ChatColor.WHITE + " mongoya ekleniyor...");
                        names[index++] = collection;
                    }
                }

                try {
                    Thread collectionThread = new Thread(() -> {
                        Arrays.stream(names).forEach(name -> {
                            if (!mongoDatabase.listCollectionNames().into(new ArrayList<>()).contains(name)) {
                                mongoDatabase.createCollection(name);
                            }
                        });
                    });

                    collectionThread.setName("Collection-Create-Task");
                    collectionThread.start();
                } catch (Exception ex) {
                    TorchPlugin.getInstance().getLogger().log(Level.WARNING, ex.getMessage());
                }

                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Torch] [MongoDB]" + ChatColor.WHITE + " Collection baglantilari kuruldu, baglanti bekleniyor!");
            } catch (IndexOutOfBoundsException | NullPointerException ex) {
                ex.printStackTrace();

                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Torch] [MongoDB]" + ChatColor.WHITE + " Collection hatasi, 5 saniye içinde kurtarilmaya çalisiliyor.");
                Bukkit.getScheduler().runTaskLater(TorchPlugin.getInstance(), this::createDataConnection, 5 * 20L);
            }

        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Torch] [MongoDB]" + ChatColor.WHITE + " Collection hatasi, 5 saniye içinde kurtarilmaya çalisiliyor.");
            Bukkit.getScheduler().runTaskLater(TorchPlugin.getInstance(), this::createDataConnection, 5 * 20L);
        }
    }

    public MongoCollection<Document> getMongoCollection(String registeredName) {
        MongoConnection mongoConnection = TorchPlugin.getInstance().getMongoConnection();
        MongoClient mongoClient = mongoConnection.getMongoClient();

        MongoDatabase mongoDatabase = mongoClient.getDatabase("redrock");
        for (String dataCollection : this.collections) {
            if (dataCollection.equals(registeredName)) {
                return mongoDatabase.getCollection(registeredName);
            }
        }
        return null;
    }
}
