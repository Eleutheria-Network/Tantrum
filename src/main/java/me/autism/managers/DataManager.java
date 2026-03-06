package me.autism.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.autism.Tantrum;
import java.io.*;
import java.util.*;

public class DataManager {
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, String> data = new HashMap<>();

    public DataManager(Tantrum plugin) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.file = new File(plugin.getDataFolder(), "data.json");
        load();
    }

    public void save(String username, String hash) {
        data.put(username.toLowerCase(), hash);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public String getHash(String username) { return data.get(username.toLowerCase()); }

    private void load() {
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            data = gson.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
        } catch (IOException e) { e.printStackTrace(); }
    }
}