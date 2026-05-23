package com.weston.megastocks.task;

import com.weston.megastocks.MegaStocksPlugin;

public final class MarketTask implements Runnable {
    private final MegaStocksPlugin plugin;

    public MarketTask(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.market().updateMarket();
    }
}
