package com.climpy.torch.profile;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.mongo.CollectionManager;
import com.climpy.torch.mongo.MongoAPI;
import com.climpy.torch.mongo.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

@Data
public class ProfileManager {
    public Map<UUID, Profile> profiles;

    public ProfileManager() {
        this.profiles = new HashMap<>();
    }
    public Profile getProfile(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (offlinePlayer == null) {
            return null;
        }

        return this.getPlayerProfile(offlinePlayer.getUniqueId());
    }
    
    private Profile getPlayerProfile(UUID uniqueUUID) {
        return this.getPlayerProfile(uniqueUUID, null);
    }
    public Profile getProfile(UUID uniqueUUID) {
        return this.getPlayerProfile(uniqueUUID);
    }

    private Profile getPlayerProfile(UUID uniqueUUID, String name) {
        if (this.profiles.containsKey(uniqueUUID) && Bukkit.getPlayer(uniqueUUID) != null) {
            return this.profiles.get(uniqueUUID);
        } else {
            Document document = MongoAPI.find("profiles", Filters.eq("uniqueUUID", uniqueUUID.toString()));
            boolean create = true;

            if (document == null && (name == null || !name.isEmpty())) {
                Document document2 = MongoAPI.find("profiles", Filters.eq("name", name));
                if (document2 != null) {
                    document2.put("uniqueUUID", uniqueUUID);
                    document = document2;
                } else {
                    create = false;
                }
            }

            if (create) {
                this.profiles.put(uniqueUUID, new Profile(document));
                return this.profiles.get(uniqueUUID);
            }

            return null;
        }
    }

    public Profile getDatabaseProfile(UUID uniqueUUID) {
        Document document = MongoAPI.find("profiles", Filters.eq("uniqueUUID", uniqueUUID.toString()));
        return document == null ? null : new Profile(document);
    }

    public Profile getDatabaseProfile(String name) {
        Document document = MongoAPI.find("profiles", Filters.eq("name", name));
        return document == null ? null : new Profile(document);
    }
    
    public List<Profile> getDatabaseProfiles() {
        List<Profile> profiles = new LinkedList<>();
        try (MongoCursor mongoCursor = MongoAPI.findAll("profiles").iterator()) {
            while (mongoCursor.hasNext()) {
                profiles.add(new Profile((Document) mongoCursor.next()));
            }
        }

        return profiles;
    }

    public List<Profile> getDatabaseIPProfile(String ipAddress) {
        List<Profile> profiles = new LinkedList<>();

        MongoConnection mongoConnection = TorchPlugin.getInstance().getMongoConnection();
        CollectionManager collectionManager = mongoConnection.getCollectionManager();
        
        MongoCollection mongoCollection = collectionManager.getMongoCollection("profiles");
        try (MongoCursor mongoCursor = mongoCollection.find(Filters.eq("currentAddress", ipAddress)).iterator()) {
            while (mongoCursor.hasNext()) {
                profiles.add(new Profile((Document) mongoCursor.next()));
            }
        }

        return profiles;
    }
}
