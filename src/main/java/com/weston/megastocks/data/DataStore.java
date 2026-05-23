package com.weston.megastocks.data;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.area.AreaManager;
import com.weston.megastocks.machine.MachineData;
import com.weston.megastocks.machine.MachineKind;
import com.weston.megastocks.item.HardwareCategory;
import com.weston.megastocks.item.HardwareKind;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DataStore {
    private final MegaStocksPlugin plugin;
    private final File machinesFile;
    private final File playersFile;
    private final File marketFile;
    private final Map<String, MachineData> machines = new HashMap<>();
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private YamlConfiguration marketConfig;
    private boolean dirty;

    public DataStore(MegaStocksPlugin plugin) {
        this.plugin = plugin;
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        this.machinesFile = new File(dataFolder, "machines.yml");
        this.playersFile = new File(dataFolder, "players.yml");
        this.marketFile = new File(dataFolder, "market.yml");
    }

    public void loadAll() {
        loadMachines();
        loadPlayers();
        this.marketConfig = YamlConfiguration.loadConfiguration(marketFile);
    }

    private void loadMachines() {
        machines.clear();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(machinesFile);
        ConfigurationSection root = yml.getConfigurationSection("machines");
        if (root == null) return;
        for (String key : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(key);
            if (s == null) continue;
            MachineKind kind = MachineKind.byId(s.getString("kind", ""));
            if (kind == null) continue;
            UUID owner;
            try {
                owner = UUID.fromString(s.getString("owner", ""));
            } catch (IllegalArgumentException ex) {
                continue;
            }
            MachineData data = new MachineData(key, owner, s.getString("owner-name", "Unknown"), kind);
            data.enabled(s.getBoolean("enabled", true));
            data.heat(s.getDouble("heat", 0.0));
            data.storedBtc(s.getDouble("stored-btc", 0.0));
            data.level(s.getInt("level", 1));
            data.placedAt(s.getLong("placed-at", System.currentTimeMillis()));
            data.areaId(s.getString("area-id", AreaManager.DEFAULT_AREA));
            ConfigurationSection installed = s.getConfigurationSection("installed");
            if (installed != null) {
                for (String categoryName : installed.getKeys(false)) {
                    HardwareCategory category;
                    try {
                        category = HardwareCategory.valueOf(categoryName.toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }
                    List<HardwareKind> parts = new ArrayList<>();
                    for (String partId : installed.getStringList(categoryName)) {
                        HardwareKind hardware = HardwareKind.byId(partId);
                        if (hardware != null) parts.add(hardware);
                    }
                    data.loadInstalled(category, parts);
                }
            }
            machines.put(key, data);
        }
    }

    private void loadPlayers() {
        players.clear();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(playersFile);
        ConfigurationSection root = yml.getConfigurationSection("players");
        if (root == null) return;
        for (String key : root.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException ex) {
                continue;
            }
            ConfigurationSection s = root.getConfigurationSection(key);
            if (s == null) continue;
            PlayerData data = new PlayerData(uuid, s.getString("name", "Unknown"));
            data.fakeBtc(s.getDouble("fake-btc", 0.0));
            data.lastHackAt(s.getLong("last-hack-at", 0L));
            data.bitcoinCode(s.getString("bitcoin-code", ""));
            data.bitcoinCodeCreatedAt(s.getLong("bitcoin-code-created-at", 0L));
            data.activeAreaId(s.getString("active-area-id", AreaManager.DEFAULT_AREA));
            for (String area : s.getStringList("areas")) data.areas().add(area);
            data.areas().add(AreaManager.DEFAULT_AREA);
            ConfigurationSection shares = s.getConfigurationSection("shares");
            if (shares != null) {
                for (String symbol : shares.getKeys(false)) {
                    int amount = shares.getInt(symbol, 0);
                    if (amount > 0) data.shares().put(symbol.toUpperCase(), amount);
                }
            }
            players.put(uuid, data);
        }
    }

    public void saveAll() {
        saveMachines();
        savePlayers();
        saveMarket();
        dirty = false;
    }

    private void saveMachines() {
        YamlConfiguration yml = new YamlConfiguration();
        for (MachineData data : machines.values()) {
            String path = "machines." + data.locationKey();
            yml.set(path + ".kind", data.kind().id());
            yml.set(path + ".owner", data.owner().toString());
            yml.set(path + ".owner-name", data.ownerName());
            yml.set(path + ".enabled", data.enabled());
            yml.set(path + ".heat", data.heat());
            yml.set(path + ".stored-btc", data.storedBtc());
            yml.set(path + ".level", data.level());
            yml.set(path + ".placed-at", data.placedAt());
            yml.set(path + ".area-id", data.areaId());
            for (Map.Entry<HardwareCategory, List<HardwareKind>> entry : data.installedHardware().entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                List<String> ids = entry.getValue().stream().map(HardwareKind::id).toList();
                yml.set(path + ".installed." + entry.getKey().name(), ids);
            }
        }
        saveFile(yml, machinesFile);
    }

    private void savePlayers() {
        YamlConfiguration yml = new YamlConfiguration();
        for (PlayerData data : players.values()) {
            String path = "players." + data.uuid();
            yml.set(path + ".name", data.name());
            yml.set(path + ".fake-btc", data.fakeBtc());
            yml.set(path + ".last-hack-at", data.lastHackAt());
            yml.set(path + ".bitcoin-code", data.bitcoinCode());
            yml.set(path + ".bitcoin-code-created-at", data.bitcoinCodeCreatedAt());
            yml.set(path + ".active-area-id", data.activeAreaId());
            yml.set(path + ".areas", data.areas().stream().sorted().toList());
            for (Map.Entry<String, Integer> entry : data.shares().entrySet()) {
                yml.set(path + ".shares." + entry.getKey(), entry.getValue());
            }
        }
        saveFile(yml, playersFile);
    }

    private void saveMarket() {
        if (marketConfig != null) saveFile(marketConfig, marketFile);
    }

    private void saveFile(YamlConfiguration yml, File file) {
        try {
            yml.save(file);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save " + file.getName() + ": " + ex.getMessage());
        }
    }

    public Map<String, MachineData> machines() { return machines; }
    public Collection<MachineData> allMachines() { return machines.values(); }
    public MachineData machine(String key) { return machines.get(key); }
    public void putMachine(MachineData data) { machines.put(data.locationKey(), data); markDirty(); }
    public MachineData removeMachine(String key) { MachineData removed = machines.remove(key); markDirty(); return removed; }

    public PlayerData player(UUID uuid, String name) {
        PlayerData data = players.computeIfAbsent(uuid, id -> new PlayerData(id, name));
        data.name(name);
        return data;
    }

    public PlayerData player(UUID uuid) { return players.get(uuid); }
    public YamlConfiguration marketConfig() { return marketConfig; }
    public boolean isDirty() { return dirty; }
    public void markDirty() { dirty = true; }
}
