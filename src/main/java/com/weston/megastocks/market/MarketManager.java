package com.weston.megastocks.market;

import com.weston.megastocks.MegaStocksPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public final class MarketManager {
    private final MegaStocksPlugin plugin;
    private final Map<String, StockData> stocks = new LinkedHashMap<>();
    private final Random random = new Random();
    private long lastUpdateMillis;

    public MarketManager(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void ensureDefaultMarket() {
        YamlConfiguration yml = plugin.data().marketConfig();
        ConfigurationSection root = yml.getConfigurationSection("stocks");
        if (root == null) {
            addDefault("MEGA", "Mega Industries", 42.50);
            addDefault("BTCX", "BlockCoin Index", 315.00);
            addDefault("MINE", "Miner Corp", 18.75);
            addDefault("GPU", "GPU Forge", 55.00);
            addDefault("CPU", "Thread Labs", 38.20);
            addDefault("RAM", "MemoryWorks", 22.60);
            addDefault("PWR", "Power Grid Co", 31.40);
            addDefault("COOL", "Cryo Cooling", 26.80);
            addDefault("VOID", "Void Ventures", 250.00);
            addDefault("EMRD", "Emerald Bank", 75.00);
            addDefault("ASIC", "ASIC Fabrication", 128.00);
            addDefault("RACK", "Server Rack Systems", 84.25);
            addDefault("VAULT", "Vault Financial", 110.00);
            addDefault("AI", "AI Compute Labs", 145.00);
            addDefault("NETH", "Nether Energy", 97.00);
            addDefault("ENDR", "Ender Networks", 188.00);
            addDefault("BHOL", "Black Hole Mining", 500.00);
            addExpandedDefaults();
            saveMarketToConfig();
            plugin.data().markDirty();
            return;
        }
        stocks.clear();
        for (String symbol : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(symbol);
            if (s == null) continue;
            StockData data = new StockData(symbol, s.getString("name", symbol), s.getDouble("price", 10.0));
            data.previousPrice(s.getDouble("previous-price", data.price()));
            data.lastNews(s.getString("last-news", "Market opened."));
            stocks.put(symbol.toUpperCase(), data);
        }
        // On upgrades, add new built-in market sectors without deleting staff-created stocks.
        addExpandedDefaults();
        saveMarketToConfig();
        plugin.data().markDirty();
    }

    private void addExpandedDefaults() {
        addIfMissing("USB", "Cold Wallet USB Co", 860.00);
        addIfMissing("VPN", "VPN Routing Systems", 72.00);
        addIfMissing("SECU", "Security Key Labs", 118.00);
        addIfMissing("HONE", "Honeypot Defense", 96.00);
        addIfMissing("SCAN", "Scanner Tools Inc", 44.00);
        addIfMissing("OVRC", "Overclock Dynamics", 135.00);
        addIfMissing("AUTO", "Automation Modules", 61.00);
        addIfMissing("COLD", "Cold Storage Trust", 210.00);
        addIfMissing("HACK", "Black Market Compute", 13.37);
        addIfMissing("QBIT", "Quantum Hash Labs", 777.00);
        addIfMissing("FIBR", "Fiber Network Group", 54.00);
        addIfMissing("BANK", "Server Bank Holdings", 130.00);
    }

    private void addIfMissing(String symbol, String name, double price) {
        if (!stocks.containsKey(symbol.toUpperCase())) addDefault(symbol, name, price);
    }

    private void addDefault(String symbol, String name, double price) {
        stocks.put(symbol.toUpperCase(), new StockData(symbol, name, price));
    }

    public Collection<StockData> stocks() {
        return stocks.values();
    }

    public StockData stock(String symbol) {
        if (symbol == null) return null;
        return stocks.get(symbol.toUpperCase());
    }

    public boolean createStock(String symbol, String name, double price) {
        String clean = cleanSymbol(symbol);
        if (clean == null || stocks.containsKey(clean)) return false;
        StockData data = new StockData(clean, name == null || name.isBlank() ? clean : name.trim(), Math.max(0.01, price));
        stocks.put(clean, data);
        saveMarketToConfig();
        plugin.data().markDirty();
        return true;
    }

    public boolean setStockPrice(String symbol, double price) {
        StockData stock = stock(symbol);
        if (stock == null) return false;
        stock.previousPrice(stock.price());
        stock.price(Math.max(0.01, price));
        stock.lastNews("Staff manually set the price.");
        saveMarketToConfig();
        plugin.data().markDirty();
        return true;
    }

    public boolean removeStock(String symbol) {
        String clean = cleanSymbol(symbol);
        if (clean == null || stocks.remove(clean) == null) return false;
        saveMarketToConfig();
        plugin.data().markDirty();
        return true;
    }

    public String cleanSymbol(String symbol) {
        if (symbol == null) return null;
        String clean = symbol.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (clean.length() < 2 || clean.length() > 6) return null;
        return clean;
    }

    public void updateMarket() {
        double walk = plugin.getConfig().getDouble("market.random-walk-percent", 4.0) / 100.0;
        double eventChance = plugin.getConfig().getDouble("market.event-chance-percent", 12.0) / 100.0;
        double min = plugin.getConfig().getDouble("market.min-stock-price", 1.0);
        double max = plugin.getConfig().getDouble("market.max-stock-price", 1000000.0);

        for (StockData stock : stocks.values()) {
            stock.previousPrice(stock.price());
            double move = (random.nextDouble() * 2.0 - 1.0) * walk;
            String news = "Normal trading.";
            if (random.nextDouble() < eventChance) {
                double eventMove = (random.nextDouble() * 2.0 - 1.0) * walk * 4.0;
                move += eventMove;
                if (eventMove > 0) news = bullishNews(stock.symbol());
                else news = bearishNews(stock.symbol());
            }
            double newPrice = Math.max(min, Math.min(max, stock.price() * (1.0 + move)));
            stock.price(newPrice);
            stock.lastNews(news);
        }
        lastUpdateMillis = System.currentTimeMillis();
        saveMarketToConfig();
        plugin.data().markDirty();
    }

    private String bullishNews(String symbol) {
        String[] news = {
                symbol + " demand surges after miner upgrades trend.",
                symbol + " beats fake earnings expectations.",
                symbol + " announces new datacenter deal.",
                symbol + " benefits from bull-run hype."
        };
        return news[random.nextInt(news.length)];
    }

    private String bearishNews(String symbol) {
        String[] news = {
                symbol + " drops after power cost fears.",
                symbol + " hit by fake market panic.",
                symbol + " slows after hardware shortage.",
                symbol + " investors worry about overheating reports."
        };
        return news[random.nextInt(news.length)];
    }

    public void saveMarketToConfig() {
        YamlConfiguration yml = plugin.data().marketConfig();
        yml.set("stocks", null);
        for (StockData stock : stocks.values()) {
            String path = "stocks." + stock.symbol();
            yml.set(path + ".name", stock.name());
            yml.set(path + ".price", stock.price());
            yml.set(path + ".previous-price", stock.previousPrice());
            yml.set(path + ".last-news", stock.lastNews());
        }
        yml.set("last-update", lastUpdateMillis);
    }

    public long lastUpdateMillis() {
        return lastUpdateMillis;
    }
}
