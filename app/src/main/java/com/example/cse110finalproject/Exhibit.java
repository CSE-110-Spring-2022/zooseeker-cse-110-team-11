package com.example.cse110finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

@Entity(tableName = "exhibits")
public class Exhibit {
    /**
     * Load ZooNode's from a JSON file (such as vertex_info.json).
     *
     * @param infoReader a reader from which to read the JSON input.
     * @return a list
     */
    public static List<Exhibit> fromJson(Reader infoReader) {
        var gson = new Gson();
        var type = new TypeToken<List<Exhibit>>() {
        }.getType();
        return gson.fromJson(infoReader, type);
    }

    public static void toJson(List<Exhibit> infos, Writer writer) throws IOException {
        var gson = new Gson();
        var type = new TypeToken<List<Exhibit>>() {
        }.getType();
        gson.toJson(infos, type, writer);
        writer.flush();
        writer.close();
    }

    public enum Kind {
        // The SerializedName annotation tells GSON how to convert
        // from the strings in our JSON to this Enum.
        @SerializedName("gate") GATE,
        @SerializedName("exhibit") EXHIBIT,
        @SerializedName("intersection") INTERSECTION,
        @SerializedName("exhibit_group") EXHIBIT_GROUP;
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @NonNull
    public final String id;

    @ColumnInfo(name = "group_id")
    @SerializedName("group_id")
    @Nullable
    public final String groupId;

    @ColumnInfo(name = "kind")
    @SerializedName("kind")
    @NonNull
    public final Kind kind;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @NonNull
    public final String name;

    @ColumnInfo(name = "tags")
    @SerializedName("tags")
    @NonNull
    public final List<String> tags;

    @ColumnInfo(name = "lat")
    @SerializedName("lat")
    public final Double lat;

    @ColumnInfo(name = "lng")
    @SerializedName("lng")
    public final Double lng;

    public boolean isExhibit() {
        return kind.equals(Kind.EXHIBIT);
    }

    public boolean isIntersection() {
        return kind.equals(Kind.INTERSECTION);
    }

    public boolean isGroup() {
        return kind.equals(Kind.EXHIBIT_GROUP);
    }

    public boolean hasGroup() {
        return groupId != null;
    }

    public Exhibit(@NonNull String id,
                   @Nullable String groupId,
                   @NonNull Kind kind,
                   @NonNull String name,
                   @NonNull List<String> tags,
                   @Nullable Double lat,
                   @Nullable Double lng) {
        this.id = id;
        this.groupId = groupId;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.lat = lat;
        this.lng = lng;

        if (!this.hasGroup() && (lat == null || lng == null)) {
            throw new RuntimeException("Nodes must have a lat/long unless they are grouped.");
        }
    }

}
