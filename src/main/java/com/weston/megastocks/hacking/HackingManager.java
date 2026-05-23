package com.weston.megastocks.hacking;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.item.HardwareCategory;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.machine.MachineData;
import com.weston.megastocks.machine.MachineKind;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class HackingManager {
    private final MegaStocksPlugin plugin;
    private final Random random = new Random();

    public HackingManager(MegaStocksPlugin plugin) { this.plugin = plugin; }

    public boolean enabled() { return plugin.getConfig().getBoolean("hacking.enabled", false); }
    public void enabled(boolean enabled) { plugin.getConfig().set("hacking.enabled", enabled); plugin.saveConfig(); }

    public double attackPower(UUID owner) {
        double total = 0.0;
        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(owner) || !data.enabled()) continue;
            MachineKind kind = data.kind();
            total += switch (kind) {
                case BLACK_MARKET_STATION -> 1000.0;
                case NETWORK_OPERATIONS_CENTER -> 1400.0;
                case SERVER_RACK_BASIC -> 250.0;
                case SERVER_RACK_ADVANCED -> 700.0;
                case QUANTUM_SERVER_RACK -> 4200.0;
                case AI_SERVER_CLUSTER -> 6500.0;
                default -> 0.0;
            };
            for (List<HardwareKind> list : data.installedHardware().values()) {
                for (HardwareKind hardware : list) {
                    if (hardware.isHackingTool()) total += hardware.performance() * 1.90;
                    else if (hardware.category() == HardwareCategory.MODULE && !hardware.isSecurityTool()) total += hardware.performance() * 0.28;
                    else if (hardware.category() == HardwareCategory.NETWORK) total += hardware.performance() * 0.30;
                    else if (hardware.category() == HardwareCategory.CPU) total += hardware.performance() * 0.20;
                    else if (hardware.category() == HardwareCategory.RAM) total += hardware.performance() * 0.08;
                    else if (hardware.category() == HardwareCategory.GPU) total += hardware.performance() * 0.04;
                    else if (hardware.category() == HardwareCategory.STORAGE) total += hardware.performance() * 0.06;
                }
            }
        }
        return total;
    }

    public double securityPower(UUID owner) {
        PlayerData playerData = plugin.data().player(owner);
        double total = plugin.getConfig().getDouble("hacking.base-security", 350.0);
        if (playerData != null) {
            total += (playerData.fakeBtc() + storedMachineBtc(owner)) * plugin.getConfig().getDouble("hacking.security-per-wallet-btc", 900.0);
            if (playerData.hasBitcoinCode()) total += codeStrength(playerData.bitcoinCode()) * plugin.getConfig().getDouble("hacking.security-per-code-strength", 65.0);
        }

        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(owner) || !data.enabled()) continue;
            MachineKind kind = data.kind();
            total += switch (kind) {
                case BANK_VAULT -> plugin.getConfig().getDouble("hacking.bank-vault-security", 2500.0);
                case NETWORK_OPERATIONS_CENTER -> 1200.0;
                case SERVER_RACK_BASIC -> 500.0;
                case SERVER_RACK_ADVANCED -> 1100.0;
                case QUANTUM_SERVER_RACK -> 3600.0;
                case AI_SERVER_CLUSTER -> 4800.0;
                default -> 0.0;
            };
            for (List<HardwareKind> list : data.installedHardware().values()) {
                for (HardwareKind hardware : list) {
                    if (hardware.isSecurityTool()) total += hardware.performance() * 2.20;
                    else if (hardware.category() == HardwareCategory.MODULE) total += hardware.performance() * 0.12;
                    else if (hardware.category() == HardwareCategory.NETWORK) total += hardware.performance() * 0.35;
                    else if (hardware.category() == HardwareCategory.CPU) total += hardware.performance() * 0.12;
                    else if (hardware.category() == HardwareCategory.RAM) total += hardware.performance() * 0.08;
                    else if (hardware.category() == HardwareCategory.STORAGE) total += hardware.performance() * 0.18;
                }
            }
        }
        return total;
    }

    public double codeDefensePower(UUID owner) {
        PlayerData data = plugin.data().player(owner);
        double security = securityPower(owner);
        if (data != null && data.hasBitcoinCode()) security += codeStrength(data.bitcoinCode()) * plugin.getConfig().getDouble("hacking.code-defense-multiplier", 125.0);
        return security;
    }

    private int codeStrength(String code) {
        if (code == null) return 0;
        boolean lower = false, upper = false, digit = false, special = false;
        for (char c : code.toCharArray()) {
            if (Character.isLowerCase(c)) lower = true;
            else if (Character.isUpperCase(c)) upper = true;
            else if (Character.isDigit(c)) digit = true;
            else special = true;
        }
        int classes = 0;
        if (lower) classes++; if (upper) classes++; if (digit) classes++; if (special) classes++;
        return Math.max(1, code.length()) * Math.max(1, classes);
    }

    public int cooldownRemainingSeconds(Player player) {
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        long cooldownMillis = plugin.getConfig().getLong("hacking.cooldown-seconds", 900L) * 1000L;
        long remaining = (data.lastHackAt() + cooldownMillis) - System.currentTimeMillis();
        return (int) Math.max(0L, (remaining + 999L) / 1000L);
    }

    public HackResult attemptHack(Player attacker, UUID targetUuid) {
        return attempt(attacker, targetUuid, false);
    }

    public HackResult attemptCodeHack(Player attacker, UUID targetUuid) {
        return attempt(attacker, targetUuid, true);
    }

    private HackResult attempt(Player attacker, UUID targetUuid, boolean codeHack) {
        if (!enabled()) return HackResult.fail("&cHacking is disabled on this server.");
        if (!attacker.hasPermission("megastocks.hacking.use")) return HackResult.fail("&cNo permission to use hacking gameplay.");
        if (attacker.getUniqueId().equals(targetUuid)) return HackResult.fail("&cYou cannot hack yourself.");

        PlayerData attackerData = plugin.data().player(attacker.getUniqueId(), attacker.getName());
        PlayerData targetData = plugin.data().player(targetUuid);
        if (targetData == null) return HackResult.fail("&cThat player has no MegaStocks wallet yet.");
        if (codeHack && !targetData.hasBitcoinCode()) return HackResult.fail("&cThat player has no fake BTC private code to crack yet.");
        double totalAvailable = targetData.fakeBtc() + storedMachineBtc(targetUuid);
        if (totalAvailable <= 0.0) return HackResult.fail("&cThat player has no fake BTC to steal.");

        int cooldown = cooldownRemainingSeconds(attacker);
        if (cooldown > 0) return HackResult.fail("&cYour hacking terminal is cooling down. Wait &e" + cooldown + "s&c.");

        double attack = attackPower(attacker.getUniqueId());
        double minAttack = codeHack ? plugin.getConfig().getDouble("hacking.code-hack-minimum-attack-power", 85000.0) : plugin.getConfig().getDouble("hacking.minimum-attack-power", 5000.0);
        if (attack < minAttack) return HackResult.fail("&cYour attack power is only &e" + whole(attack) + "&c. You need at least &e" + whole(minAttack) + (codeHack ? " &cto even try a CODE HACK." : " &cto even try."));

        double security = codeHack ? codeDefensePower(targetUuid) : securityPower(targetUuid);
        double ratio = codeHack ? plugin.getConfig().getDouble("hacking.code-hack-minimum-attack-to-security-ratio", 1.65) : plugin.getConfig().getDouble("hacking.minimum-attack-to-security-ratio", 0.55);
        if (attack < security * ratio) return HackResult.fail("&cTarget security is too strong. Attack: &e" + whole(attack) + " &cSecurity: &b" + whole(security) + "&c. Upgrade hacker hardware first.");

        applyHackHeat(attacker.getUniqueId(), codeHack ? plugin.getConfig().getDouble("hacking.code-hack-heat-cost", 650.0) : plugin.getConfig().getDouble("hacking.wallet-hack-heat-cost", 80.0));

        double rawChance = attack / Math.max(1.0, attack + (security * (codeHack ? 1.35 : 1.0)));
        double minChance = codeHack ? plugin.getConfig().getDouble("hacking.code-hack-minimum-success-chance", 0.01) : plugin.getConfig().getDouble("hacking.minimum-success-chance", 0.05);
        double maxChance = codeHack ? plugin.getConfig().getDouble("hacking.code-hack-maximum-success-chance", 0.18) : plugin.getConfig().getDouble("hacking.maximum-success-chance", 0.70);
        double chance = Math.max(minChance, Math.min(maxChance, rawChance));

        attackerData.lastHackAt(System.currentTimeMillis());
        if (random.nextDouble() > chance) {
            plugin.data().markDirty();
            notifyTarget(targetUuid, codeHack ? "&cSomeone tried to crack your fake BTC private code, but your security blocked it." : "&cSomeone tried to hack your fake BTC wallet, but your security blocked it.");
            return HackResult.fail((codeHack ? "&cCode hack failed. " : "&cHack failed. ") + "Chance was &e" + percent(chance) + "&c. Target security blocked you and your rigs got HOT.");
        }

        double minStealPercent = codeHack ? plugin.getConfig().getDouble("hacking.code-hack-min-steal-percent", 0.20) : plugin.getConfig().getDouble("hacking.min-steal-percent", 0.03);
        double maxStealPercent = codeHack ? plugin.getConfig().getDouble("hacking.code-hack-max-steal-percent", 0.85) : plugin.getConfig().getDouble("hacking.max-steal-percent", 0.35);
        double maxStealBtc = codeHack ? plugin.getConfig().getDouble("hacking.code-hack-max-steal-btc", 25.0) : plugin.getConfig().getDouble("hacking.max-steal-btc", 2.0);
        double chanceFactor = chance / Math.max(0.01, maxChance);
        double stealPercent = minStealPercent + ((maxStealPercent - minStealPercent) * chanceFactor);
        double stolen = Math.min(maxStealBtc, totalAvailable * stealPercent);
        if (stolen <= 0.0) { plugin.data().markDirty(); return HackResult.fail("&cHack connected, but there was no fake BTC available."); }

        drainFakeBtc(targetData, targetUuid, stolen);
        attackerData.fakeBtc(attackerData.fakeBtc() + stolen);
        plugin.data().markDirty();

        String targetName = targetData.name() == null ? "Unknown" : targetData.name();
        notifyTarget(targetUuid, (codeHack ? "&cYour fake BTC code security was cracked by &e" : "&cYour fake BTC security was breached by &e") + attacker.getName() + "&c. Lost &b" + plugin.msg().btc(stolen) + "&c.");
        return HackResult.success((codeHack ? "&5CODE HACK successful against &e" : "&aHack successful against &e") + targetName + (codeHack ? "&5. The actual code was NOT shown, but you extracted &b" : "&a. Stole &b") + plugin.msg().btc(stolen) + (codeHack ? "&5. Chance was &e" : "&a. Chance was &e") + percent(chance) + (codeHack ? "&5." : "&a."));
    }

    private void applyHackHeat(UUID owner, double heatCost) {
        if (heatCost <= 0) return;
        int machines = 0;
        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(owner) || !data.enabled()) continue;
            if (data.kind() == MachineKind.BLACK_MARKET_STATION || data.kind() == MachineKind.NETWORK_OPERATIONS_CENTER || data.kind() == MachineKind.SERVER_RACK_BASIC || data.kind() == MachineKind.SERVER_RACK_ADVANCED || data.kind() == MachineKind.QUANTUM_SERVER_RACK || data.kind() == MachineKind.AI_SERVER_CLUSTER) machines++;
        }
        double share = heatCost / Math.max(1, machines);
        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(owner) || !data.enabled()) continue;
            if (machines == 0 || data.kind() == MachineKind.BLACK_MARKET_STATION || data.kind() == MachineKind.NETWORK_OPERATIONS_CENTER || data.kind() == MachineKind.SERVER_RACK_BASIC || data.kind() == MachineKind.SERVER_RACK_ADVANCED || data.kind() == MachineKind.QUANTUM_SERVER_RACK || data.kind() == MachineKind.AI_SERVER_CLUSTER) {
                data.heat(data.heat() + share);
            }
        }
    }

    private double storedMachineBtc(UUID owner) {
        double total = 0.0;
        for (MachineData data : plugin.data().allMachines()) if (data.owner().equals(owner)) total += data.storedBtc();
        return total;
    }

    private void drainFakeBtc(PlayerData targetData, UUID targetUuid, double amount) {
        double remaining = amount;
        double fromWallet = Math.min(targetData.fakeBtc(), remaining);
        if (fromWallet > 0.0) { targetData.fakeBtc(targetData.fakeBtc() - fromWallet); remaining -= fromWallet; }
        if (remaining <= 0.0) return;
        for (MachineData data : plugin.data().allMachines()) {
            if (!data.owner().equals(targetUuid) || data.storedBtc() <= 0.0) continue;
            double take = Math.min(data.storedBtc(), remaining);
            data.storedBtc(data.storedBtc() - take);
            remaining -= take;
            if (remaining <= 0.0) return;
        }
    }

    private void notifyTarget(UUID targetUuid, String message) {
        if (!plugin.getConfig().getBoolean("hacking.notify-target", true)) return;
        Player target = Bukkit.getPlayer(targetUuid);
        if (target != null && target.isOnline()) plugin.msg().send(target, message);
    }

    public String targetName(UUID uuid) {
        PlayerData playerData = plugin.data().player(uuid);
        if (playerData != null && playerData.name() != null) return playerData.name();
        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        return offline.getName() == null ? uuid.toString().substring(0, 8) : offline.getName();
    }

    private String whole(double value) { return String.format("%,.0f", value); }
    private String percent(double value) { return String.format("%.1f%%", value * 100.0); }

    public record HackResult(boolean success, String message) {
        public static HackResult success(String message) { return new HackResult(true, message); }
        public static HackResult fail(String message) { return new HackResult(false, message); }
    }
}
