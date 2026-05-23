package com.weston.megastocks;

import com.weston.megastocks.area.AreaManager;
import com.weston.megastocks.command.MegaCommand;
import com.weston.megastocks.data.DataStore;
import com.weston.megastocks.economy.EconomyHook;
import com.weston.megastocks.gui.MenuListener;
import com.weston.megastocks.gui.GuiManager;
import com.weston.megastocks.hacking.HackingManager;
import com.weston.megastocks.item.ItemFactory;
import com.weston.megastocks.item.MegaItemProtectionListener;
import com.weston.megastocks.market.MarketManager;
import com.weston.megastocks.machine.MachineListener;
import com.weston.megastocks.machine.MachineManager;
import com.weston.megastocks.machine.MachineKind;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.task.MarketTask;
import com.weston.megastocks.task.SimulationTask;
import com.weston.megastocks.util.Msg;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MegaStocksPlugin extends JavaPlugin {
    private EconomyHook economyHook;
    private DataStore dataStore;
    private ItemFactory itemFactory;
    private MachineManager machineManager;
    private MarketManager marketManager;
    private HackingManager hackingManager;
    private AreaManager areaManager;
    private GuiManager guiManager;
    private Msg msg;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        applySafetyDefaults();
        this.msg = new Msg(this);
        this.economyHook = new EconomyHook(this);
        this.economyHook.setup();
        this.dataStore = new DataStore(this);
        this.itemFactory = new ItemFactory(this);
        this.machineManager = new MachineManager(this);
        this.marketManager = new MarketManager(this);
        this.hackingManager = new HackingManager(this);
        this.areaManager = new AreaManager(this);
        this.guiManager = new GuiManager(this);

        dataStore.loadAll();
        marketManager.ensureDefaultMarket();

        MegaCommand command = new MegaCommand(this);
        PluginCommand mega = getCommand("mega");
        if (mega != null) {
            mega.setExecutor(command);
            mega.setTabCompleter(command);
        }
        PluginCommand code = getCommand("code");
        if (code != null) {
            code.setExecutor(command);
            code.setTabCompleter(command);
        }
        for (String commandName : new String[]{"gift", "add", "take"}) {
            PluginCommand extra = getCommand(commandName);
            if (extra != null) {
                extra.setExecutor(command);
                extra.setTabCompleter(command);
            }
        }

        getServer().getPluginManager().registerEvents(new MachineListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new MegaItemProtectionListener(this), this);

        int simulationSeconds = Math.max(2, getConfig().getInt("performance.simulation-interval-seconds", 10));
        int marketSeconds = Math.max(10, getConfig().getInt("performance.market-update-seconds", 60));
        int autosaveSeconds = Math.max(60, getConfig().getInt("performance.autosave-seconds", 300));

        getServer().getScheduler().runTaskTimer(this, new SimulationTask(this), 20L * simulationSeconds, 20L * simulationSeconds);
        getServer().getScheduler().runTaskTimer(this, new MarketTask(this), 20L * marketSeconds, 20L * marketSeconds);
        getServer().getScheduler().runTaskTimer(this, () -> {
            if (dataStore.isDirty()) {
                dataStore.saveAll();
            }
        }, 20L * autosaveSeconds, 20L * autosaveSeconds);

        boolean geyserDetected = getServer().getPluginManager().isPluginEnabled("Geyser-Spigot") || getServer().getPluginManager().isPluginEnabled("floodgate");
        getLogger().info("MegaStocks enabled. Vault economy: " + (economyHook.isReady() ? "HOOKED" : "NOT FOUND") + (geyserDetected ? " | Geyser/Floodgate compatibility helpers active" : ""));
    }

    private void applySafetyDefaults() {
        if (!getConfig().getBoolean("hacking.defaulted-off-v38", false)) {
            getConfig().set("hacking.enabled", false);
            getConfig().set("hacking.defaulted-off-v38", true);
            saveConfig();
        }
    }


    public double machinePrice(MachineKind kind) {
        if (kind == null) return 0.0;
        return Math.max(0.0, getConfig().getDouble("prices.machines." + kind.id(), kind.cost()));
    }

    public double hardwarePrice(HardwareKind kind) {
        if (kind == null) return 0.0;
        return Math.max(0.0, getConfig().getDouble("prices.hardware." + kind.id(), kind.cost()));
    }

    public void setMachinePrice(MachineKind kind, double price) {
        if (kind == null) return;
        getConfig().set("prices.machines." + kind.id(), Math.max(0.0, price));
        saveConfig();
    }

    public void setHardwarePrice(HardwareKind kind, double price) {
        if (kind == null) return;
        getConfig().set("prices.hardware." + kind.id(), Math.max(0.0, price));
        saveConfig();
    }

    @Override
    public void onDisable() {
        if (dataStore != null) {
            dataStore.saveAll();
        }
    }

    public EconomyHook economy() {
        return economyHook;
    }

    public DataStore data() {
        return dataStore;
    }

    public ItemFactory items() {
        return itemFactory;
    }

    public MachineManager machines() {
        return machineManager;
    }

    public MarketManager market() {
        return marketManager;
    }

    public HackingManager hacking() {
        return hackingManager;
    }

    public AreaManager areas() {
        return areaManager;
    }

    public GuiManager gui() {
        return guiManager;
    }

    public Msg msg() {
        return msg;
    }
}
