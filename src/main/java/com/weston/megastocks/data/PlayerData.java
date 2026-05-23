package com.weston.megastocks.data;

import com.weston.megastocks.area.AreaManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PlayerData {
    private final UUID uuid;
    private String name;
    private double fakeBtc;
    private long lastHackAt;
    private String bitcoinCode = "";
    private long bitcoinCodeCreatedAt;
    private String activeAreaId = AreaManager.DEFAULT_AREA;
    private final Set<String> areas = new HashSet<>();
    private final Map<String, Integer> shares = new HashMap<>();

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.areas.add(AreaManager.DEFAULT_AREA);
    }

    public UUID uuid() { return uuid; }
    public String name() { return name; }
    public void name(String name) { this.name = name; }
    public double fakeBtc() { return fakeBtc; }
    public void fakeBtc(double fakeBtc) { this.fakeBtc = Math.max(0.0, fakeBtc); }
    public long lastHackAt() { return lastHackAt; }
    public void lastHackAt(long lastHackAt) { this.lastHackAt = Math.max(0L, lastHackAt); }
    public String bitcoinCode() { return bitcoinCode == null ? "" : bitcoinCode; }
    public boolean hasBitcoinCode() { return bitcoinCode != null && !bitcoinCode.isBlank(); }
    public void bitcoinCode(String bitcoinCode) {
        this.bitcoinCode = bitcoinCode == null ? "" : bitcoinCode.trim();
        if (hasBitcoinCode() && bitcoinCodeCreatedAt <= 0L) bitcoinCodeCreatedAt = System.currentTimeMillis();
    }
    public long bitcoinCodeCreatedAt() { return bitcoinCodeCreatedAt; }
    public void bitcoinCodeCreatedAt(long bitcoinCodeCreatedAt) { this.bitcoinCodeCreatedAt = Math.max(0L, bitcoinCodeCreatedAt); }
    public String activeAreaId() { return activeAreaId == null || activeAreaId.isBlank() ? AreaManager.DEFAULT_AREA : activeAreaId; }
    public void activeAreaId(String activeAreaId) { this.activeAreaId = activeAreaId == null || activeAreaId.isBlank() ? AreaManager.DEFAULT_AREA : activeAreaId; this.areas.add(this.activeAreaId); }
    public Set<String> areas() { return areas; }
    public Map<String, Integer> shares() { return shares; }

    public int sharesOf(String symbol) {
        return shares.getOrDefault(symbol.toUpperCase(), 0);
    }

    public void addShares(String symbol, int amount) {
        symbol = symbol.toUpperCase();
        shares.put(symbol, Math.max(0, sharesOf(symbol) + amount));
        if (shares.get(symbol) <= 0) shares.remove(symbol);
    }
}
