package com.weston.megastocks.economy;

import com.weston.megastocks.MegaStocksPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class EconomyHook {
    private final MegaStocksPlugin plugin;
    private Economy economy;

    public EconomyHook(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.economy = null;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        this.economy = rsp == null ? null : rsp.getProvider();
    }

    public boolean isReady() {
        return economy != null;
    }

    public String providerName() {
        return economy == null ? "None" : economy.getName();
    }

    public double balance(OfflinePlayer player) {
        if (economy == null) return 0.0;
        return economy.getBalance(player);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return economy != null && economy.has(player, amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        return economy != null && amount >= 0.0 && economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        return economy != null && amount >= 0.0 && economy.depositPlayer(player, amount).transactionSuccess();
    }
}
