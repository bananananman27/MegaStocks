package com.weston.megastocks.util;

import com.weston.megastocks.MegaStocksPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Msg {
    private final MegaStocksPlugin plugin;

    public Msg(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }

    public String prefix() {
        return color(plugin.getConfig().getString("messages.prefix", "&8[&6MegaStocks&8] &r"));
    }

    public void send(CommandSender sender, String text) {
        sender.sendMessage(prefix() + color(text));
    }

    public String money(double value) {
        return String.format("$%,.2f", value);
    }

    public String btc(double value) {
        return String.format("%.8f BTC", value);
    }

    public String pct(double value) {
        return String.format("%.2f%%", value);
    }
}
