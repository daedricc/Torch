package com.climpy.torch.profile.rank;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.mongo.MongoAPI;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.profile.ProfileManager;
import com.climpy.torch.utils.C;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Rank {
    private static MongoCollection<Document> collection;
    private static Map<UUID, Rank> ranks = new HashMap<>();

    private UUID uniqueUUID;

    private String displayName, prefix;
    private List<String> permissions;

    private int weight;
    private ChatColor color;
    private boolean defaultRank;

    public Rank(String displayName) {
        this.prefix = "";
        this.color = ChatColor.WHITE;
        this.permissions = new ArrayList<>();
        this.uniqueUUID = UUID.randomUUID();
        this.displayName = displayName;
        ranks.put(this.uniqueUUID, this);
    }

    public Rank(UUID uuid, String displayName) {
        this.prefix = "";
        this.color = ChatColor.WHITE;
        this.permissions = new ArrayList<>();
        this.uniqueUUID = uuid;
        this.displayName = displayName;
    }

    public Rank(UUID uniqueUUID, String displayName, String prefix, int weight, ChatColor color, boolean defaultRank) {
        this.prefix = "";
        this.color = ChatColor.WHITE;
        this.permissions = new ArrayList<>();
        this.uniqueUUID = uniqueUUID;
        this.displayName = displayName;
        this.prefix = prefix;
        this.color = color;
        this.weight = weight;
        this.defaultRank = defaultRank;
        ranks.put(uniqueUUID, this);
    }

    private void load(Document document) {
        if (document == null)
            return;

        this.prefix = ChatColor.translateAlternateColorCodes('&', document.getString("prefix"));
        this.color = ChatColor.valueOf(document.getString("color"));
        this.weight = document.getInteger("weight");
        this.defaultRank = document.getBoolean("defaultRank");
        this.permissions = new Gson().fromJson(document.getString("permissions"), new TypeToken<List<String>>() {}.getType());
    }

    public void saveRanks() {
        Document document = new Document();

        document.put("uuid", this.uniqueUUID.toString());
        document.put("displayName", this.displayName);
        document.put("prefix", this.prefix.replace(String.valueOf('§'), "&"));
        document.put("color", this.color.name());
        document.put("weight", this.weight);
        document.put("defaultRank", this.defaultRank);
        document.put("permissions", new Gson().toJson(this.permissions));

        MongoAPI.replaceOne("ranks", Filters.eq("uuid", this.uniqueUUID.toString()), document, true);
    }

    public boolean addPermission(String permission) {
        if (!this.permissions.contains(permission)) {
            this.permissions.add(permission);
            return true;
        }

        return false;
    }

    public boolean deletePermission(String permission) {
        return this.permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        if (this.permissions.contains(permission)) {
            return true;
        }

        return false;
    }

    public List<String> getAllPermissions() {
        return new ArrayList<>(this.permissions);
    }

    public void delete() {
        ranks.remove(this.uniqueUUID);
        collection.deleteOne(Filters.eq("uuid", this.uniqueUUID.toString()));
        //TODO: Silinen rank broadcast
    }

    public String getColoredName() {
        return C.translate(this.color + this.displayName);
    }

    public static Rank getRankByUuid(UUID uuid) {
        return ranks.get(uuid);
    }

    public static Rank getRankByDisplayName(String name) {
        for (Rank rank : ranks.values()) {
            if (rank.getDisplayName().equalsIgnoreCase(name))
                return rank;
        }
        return null;
    }

    public boolean isDefaultRank() {
        return this.defaultRank;
    }

    public static Rank getDefaultRank() {
        for (Rank rank : ranks.values()) {
            if (rank.isDefaultRank()) {
                return rank;
            }
        }

        Rank defaultRank = new Rank("Default");
        defaultRank.getPermissions().add("torch.default");
        defaultRank.setDefaultRank(true);
        defaultRank.saveRanks();

        ranks.put(defaultRank.getUniqueUUID(), defaultRank);

        return defaultRank;
    }

    public int compareTo(Rank other) {
        return Integer.compare(this.weight, other.weight);
    }

    public static void init() {
        collection = TorchPlugin.getInstance().getMongoConnection().getCollectionManager().getMongoCollection("ranks");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Rank rank = new Rank(UUID.fromString(document.getString("uuid")), document.getString("displayName"));
                rank.load(document);
                ranks.put(rank.getUniqueUUID(), rank);
            }
        }

        getDefaultRank();
    }

    public static Rank getRankOrDefault(String internalName) {
        Rank rank;

        try {
            rank = Rank.getRankByDisplayName(internalName);
        } catch (Exception ex) {
            rank = getDefaultRank();
        }

        return rank;
    }

    public static Map<UUID, Rank> getRanks() {
        return ranks;
    }
}
