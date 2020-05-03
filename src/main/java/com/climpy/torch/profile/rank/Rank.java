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
    private List<Rank> inherited;

    public Rank(String displayName) {
        this.prefix = "";
        this.color = ChatColor.WHITE;
        this.permissions = new ArrayList<>();
        this.inherited = new ArrayList<>();
        this.uniqueUUID = UUID.randomUUID();
        this.displayName = displayName;
        ranks.put(this.uniqueUUID, this);
    }

    public Rank(UUID uuid, String displayName) {
        this.prefix = "";
        this.color = ChatColor.WHITE;
        this.permissions = new ArrayList<>();
        this.inherited = new ArrayList<>();
        this.uniqueUUID = uuid;
        this.displayName = displayName;
    }

    public Rank(UUID uniqueUUID, String displayName, String prefix, int weight, ChatColor color, boolean defaultRank) {
        this.prefix = "";
        this.color = ChatColor.WHITE;
        this.permissions = new ArrayList<>();
        this.inherited = new ArrayList<>();
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

        // this.permissions = new Gson().fromJson(document.getString("permissions"), new TypeToken<List<String>>() {}.getType());
        //this.inherited = new Gson().fromJson(document.getString("inherits"), new TypeToken<List<String>>() {}.getType());


        for (JsonElement permissions : new JsonParser().parse(document.getString("permissions")).getAsJsonArray()) {
            this.permissions.add(permissions.getAsString());
        }

        for (JsonElement element : new JsonParser().parse(document.getString("inherits")).getAsJsonArray()) {
            UUID rankUUID = UUID.fromString(element.getAsString());
            Rank rank = ranks.get(rankUUID);
            if (rank == null)
                continue;

            this.inherited.add(rank);
        }
    }

    public void saveRanks() {
        Document document = new Document();

        document.put("uuid", this.uniqueUUID.toString());
        document.put("displayName", this.displayName);
        document.put("prefix", this.prefix.replace(String.valueOf('§'), "&"));
        document.put("color", this.color.name());
        document.put("weight", this.weight);
        document.put("defaultRank", this.defaultRank);
       // document.put("permissions", new Gson().toJson(this.permissions));
      //  document.put("inherits", new Gson().toJson(this.inherited.stream().map(Rank::getUniqueUUID).map(UUID::toString).collect(Collectors.toList())));

        JsonArray permissions = new JsonArray();
        for (String permission : this.permissions) {
            permissions.add(permission);
        }

        JsonArray inherits = new JsonArray();
        for (Rank rank : this.inherited) {
            inherits.add(rank.getUniqueUUID().toString());
        }

        document.put("permissions", permissions.toString());
        document.put("inherits", inherits.toString());

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
        if (this.permissions.contains(permission))
            return true;
        for (Rank rank : this.inherited) {
            if (rank.hasPermission(permission))
                return true;
        }
        return false;
    }

    public boolean canInherit(Rank rankToCheck) {
        if (this.inherited.contains(rankToCheck) || rankToCheck.inherited.contains(this))
            return false;
        for (Rank rank : this.inherited) {
            if (!rank.canInherit(rankToCheck))
                return false;
        }
        return true;
    }

    public List<String> getAllPermissions() {
        List<String> permissions = new ArrayList<>(this.permissions);
        for (Rank rank : this.inherited) {
            permissions.addAll(rank.getAllPermissions());
        }

        return permissions;
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
