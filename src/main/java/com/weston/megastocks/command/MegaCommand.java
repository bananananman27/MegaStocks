package com.weston.megastocks.command;

import com.weston.megastocks.MegaStocksPlugin;
import com.weston.megastocks.data.PlayerData;
import com.weston.megastocks.item.HardwareCategory;
import com.weston.megastocks.item.HardwareKind;
import com.weston.megastocks.machine.MachineData;
import com.weston.megastocks.machine.MachineCategory;
import com.weston.megastocks.machine.MachineKind;
import com.weston.megastocks.machine.NetworkStats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class MegaCommand implements CommandExecutor, TabCompleter {
    private final MegaStocksPlugin plugin;

    public MegaCommand(MegaStocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase(Locale.ROOT);
        if (commandName.equals("code")) {
            code(sender, args, label);
            return true;
        }
        if (commandName.equals("gift")) {
            gift(sender, args);
            return true;
        }
        if (commandName.equals("add") || commandName.equals("take")) {
            adminBitcoinStandalone(sender, commandName, args);
            return true;
        }
        if (args.length == 0) {
            if (label.equalsIgnoreCase("stocks")) requirePlayer(sender, p -> plugin.gui().openMarket(p));
            else requirePlayer(sender, p -> plugin.gui().openMain(p));
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            help(sender, label);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "menu" -> requirePlayer(sender, p -> plugin.gui().openMain(p));
            case "shop" -> requirePlayer(sender, p -> plugin.gui().openShop(p));
            case "cooling", "coolers" -> requirePlayer(sender, p -> plugin.gui().openMachineCategory(p, MachineCategory.COOLING));
            case "wires", "wire" -> requirePlayer(sender, p -> plugin.gui().openMachineCategory(p, MachineCategory.WIRE));
            case "hardware" -> requirePlayer(sender, p -> plugin.gui().openHardwareShop(p));
            case "market", "stocks" -> requirePlayer(sender, p -> plugin.gui().openMarket(p));
            case "area", "areas" -> area(sender, args);
            case "hack" -> hack(sender, args);
            case "monitor" -> requirePlayer(sender, p -> plugin.gui().openMonitor(p));
            case "scan", "scanner" -> scan(sender);
            case "wallet", "balance" -> requirePlayer(sender, p -> sendBalance(p));
            case "sellbtc" -> requirePlayer(sender, p -> sellBtc(p));
            case "collect" -> requirePlayer(sender, p -> collectBtc(p));
            case "install" -> install(sender, args);
            case "remove" -> remove(sender, args);
            case "pickup", "pick" -> pickup(sender);
            case "repair" -> repair(sender);
            case "geyser", "bedrock", "compat" -> compatHelp(sender, label);
            case "code" -> code(sender, java.util.Arrays.copyOfRange(args, 1, args.length), "code");
            case "gift" -> gift(sender, java.util.Arrays.copyOfRange(args, 1, args.length));
            case "add", "take" -> adminBitcoinSubcommand(sender, sub, args);
            case "set" -> adminSetDirect(sender, args);
            case "admin", "staff" -> admin(sender, args);
            case "stock", "stocksadmin" -> stockAdmin(sender, args, 1);
            case "listitems" -> listItems(sender);
            case "adminstats" -> adminStats(sender);
            case "stealing", "steal" -> stealing(sender, args);
            case "hacking" -> hacking(sender, args);
            case "reload" -> reload(sender);
            case "give" -> give(sender, args);
            default -> plugin.msg().send(sender, "&cUnknown command. Try &e/" + label + " help&c.");
        }
        return true;
    }

    private void help(CommandSender sender, String label) {
        plugin.msg().send(sender, "&6MegaStocks commands:");
        sender.sendMessage(plugin.msg().color("&e/" + label + " &7- Open main menu. &8(/stocks opens stock market)"));
        sender.sendMessage(plugin.msg().color("&e/" + label + " help &7- Show this help menu. &8(/stocks help works too)"));
        sender.sendMessage(plugin.msg().color("&e/" + label + " menu &7- Open main menu."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " shop &7- Buy machines."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " cooling &7- Buy giant area cooling blocks."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " wires &7- Buy easy wires/pipes that boost an area."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " hardware &7- Buy GPUs, CPUs, RAM, parts."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " install [slot] &7- Bedrock-friendly: install held/inventory hardware into looked-at machine."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " remove <slot> &7- Bedrock-friendly: remove hardware from looked-at machine."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " repair &7- Bedrock-friendly: use held repair box on looked-at machine."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " pickup &7- Bedrock-friendly: pick up/mine looked-at or nearest machine."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " geyser &7- Shows Bedrock/Geyser backup controls."));
        sender.sendMessage(plugin.msg().color("&e/code &7- Show only your own fake BTC private code."));
        sender.sendMessage(plugin.msg().color("&e/code set <code> &7- Set the code required before mining purchases."));
        sender.sendMessage(plugin.msg().color("&e/gift <player> <btc> &7- Gift fake BTC. Also: /gift stock <player> <symbol> <shares>."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " market &7- Buy/sell fake stocks."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " area &7- Areas/monitors menu."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " area create <name> &7- Make a machine area."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " area set <name> &7- Put new machines in that area."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " area assign [name] &7- Move looked-at machine to an area."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " area station <name|all> &7- Link looked-at monitor station."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " hack &7- Open the hidden fake BTC hacking area."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " monitor &7- See power, heat, BTC, top users."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " scan &7- Use a scanner item on the machine you are looking at."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " collect &7- Collect BTC from machines."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " sellbtc &7- Sell wallet fake BTC using Vault."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " listitems &7- List item/machine IDs."));
        if (sender.hasPermission("megastocks.admin")) {
            sender.sendMessage(plugin.msg().color("&c/" + label + " give <id> [amount] [player] &7- Admin give."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " stealing <on|off|toggle|status> &7- Control machine stealing."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " hacking <on|off|toggle|status> &7- Control wallet hacking."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " admin stock create <symbol> <price> <name...> &7- Create staff-only stocks."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " admin stock set <symbol> <price> &7- Set a stock price."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " admin price machine <id> <price> &7- Set machine shop price."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " admin price hardware <id> <price> &7- Set hardware shop price."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " set bitcoin <price> &7- Set fake BTC sell price."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " add bitcoin <player> <amount> &7- Admin add fake BTC."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " take bitcoin <player> <amount> &7- Admin remove fake BTC."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " reload &7- Reload config."));
            sender.sendMessage(plugin.msg().color("&c/" + label + " adminstats &7- Server stats."));
        }
    }

    private void sendBalance(Player player) {
        PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
        plugin.msg().send(player, "&aVault: &e" + plugin.msg().money(plugin.economy().balance(player)) + " &8| &6Fake BTC: &b" + plugin.msg().btc(data.fakeBtc()));
        plugin.gui().openWallet(player);
    }

    private void sellBtc(Player player) {
        double sold = plugin.machines().sellAllBtc(player);
        if (sold < 0) plugin.msg().send(player, "&cVault economy is not ready.");
        else if (sold == 0) plugin.msg().send(player, "&cYou have no fake BTC to sell.");
        else plugin.msg().send(player, "&aSold all fake BTC for &e" + plugin.msg().money(sold) + "&a.");
    }

    private void collectBtc(Player player) {
        double collected = plugin.machines().collectAllBtc(player);
        plugin.msg().send(player, "&aCollected &b" + plugin.msg().btc(collected) + "&a from your machines.");
    }



    private void code(CommandSender sender, String[] args, String label) {
        requirePlayer(sender, player -> {
            PlayerData data = plugin.data().player(player.getUniqueId(), player.getName());
            if (args.length == 0 || args[0].equalsIgnoreCase("show")) {
                if (!data.hasBitcoinCode()) {
                    plugin.msg().send(player, "&cYou have not set your fake BTC private code yet. Use &e/code set <code>&c before buying mining gear.");
                    return;
                }
                plugin.msg().send(player, "&6Your fake BTC private code: &e" + data.bitcoinCode());
                player.sendMessage(plugin.msg().color("&7Only you can see this with /code. Code hacks never print the actual code to attackers."));
                return;
            }
            if (args[0].equalsIgnoreCase("set")) {
                if (args.length < 2) { plugin.msg().send(player, "&cUsage: /code set <code>"); return; }
                if (data.hasBitcoinCode()) { plugin.msg().send(player, "&cYou already set a code. Use &e/code reset <oldCode> <newCode>&c to change it."); return; }
                String code = joinCodeArgs(args, 1);
                if (!validCode(code)) { plugin.msg().send(player, "&cCode must be 4-32 characters and use only letters, numbers, dash, underscore, or dot."); return; }
                data.bitcoinCode(code);
                plugin.data().markDirty();
                plugin.msg().send(player, "&aPrivate fake BTC code set. Remember it: &e" + data.bitcoinCode());
                player.sendMessage(plugin.msg().color("&7You can view it later with &e/code&7. Do not share it."));
                return;
            }
            if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("change")) {
                if (args.length < 3) { plugin.msg().send(player, "&cUsage: /code reset <oldCode> <newCode>"); return; }
                if (!data.hasBitcoinCode()) { plugin.msg().send(player, "&cYou do not have a code yet. Use &e/code set <code>&c."); return; }
                String oldCode = args[1];
                if (!data.bitcoinCode().equals(oldCode)) { plugin.msg().send(player, "&cOld code is wrong. Code was not changed."); return; }
                String newCode = joinCodeArgs(args, 2);
                if (!validCode(newCode)) { plugin.msg().send(player, "&cCode must be 4-32 characters and use only letters, numbers, dash, underscore, or dot."); return; }
                data.bitcoinCode(newCode);
                data.bitcoinCodeCreatedAt(System.currentTimeMillis());
                plugin.data().markDirty();
                plugin.msg().send(player, "&aPrivate fake BTC code changed. New code: &e" + data.bitcoinCode());
                return;
            }
            plugin.msg().send(player, "&cUsage: /code, /code set <code>, or /code reset <oldCode> <newCode>");
        });
    }

    private boolean validCode(String code) {
        if (code == null || code.length() < 4 || code.length() > 32) return false;
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            boolean ok = Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.';
            if (!ok) return false;
        }
        return true;
    }

    private String joinCodeArgs(String[] args, int start) {
        StringBuilder out = new StringBuilder();
        for (int i = start; i < args.length; i++) out.append(args[i]);
        return out.toString().trim();
    }

    private void compatHelp(CommandSender sender, String label) {
        plugin.msg().send(sender, "&bGeyser/Bedrock-friendly controls:");
        sender.sendMessage(plugin.msg().color("&e/" + label + " install &7- Install the hardware in your hand into the machine you are looking at."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " install gpu &7- Install the first GPU from your inventory into the looked-at machine."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " remove gpu &7- Remove the newest GPU from the looked-at machine."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " repair &7- Use your held repair kit/box on the looked-at machine."));
        sender.sendMessage(plugin.msg().color("&e/" + label + " pickup &7- Pick up the looked-at/nearest machine and all installed parts."));
        sender.sendMessage(plugin.msg().color("&7Slots: &fgpu, cpu, ram, motherboard, psu, storage, network, module, cooling"));
        sender.sendMessage(plugin.msg().color("&e/code &7- Shows only your own BTC code. &e/code set <code> &7sets it."));
    }

    private void install(CommandSender sender, String[] args) {
        requirePlayer(sender, player -> {
            if (!player.hasPermission("megastocks.compat.commands")) {
                plugin.msg().send(player, "&cNo permission.");
                return;
            }
            MachineData machine = lookedMachine(player);
            if (machine == null) {
                plugin.msg().send(player, "&cLook at one of your MegaStocks machines within 6 blocks, then run this again.");
                return;
            }
            if (!canEdit(player, machine)) return;
            if (!machine.supportsHardware()) {
                plugin.msg().send(player, "&cThat machine cannot accept hardware.");
                return;
            }

            HardwareCategory requested = args.length >= 2 ? categoryFrom(args[1]) : null;
            if (args.length >= 2 && requested == null) {
                plugin.msg().send(player, "&cUnknown slot. Use gpu, cpu, ram, motherboard, psu, storage, network, module, or cooling.");
                return;
            }

            ItemStack hand = player.getInventory().getItemInMainHand();
            HardwareKind hardware = hardwareFrom(hand);
            int inventorySlot = -1;
            if (hardware != null && hardware.isRepairItem()) {
                plugin.msg().send(player, "&cRepair boxes use &e/mega repair&c, not /mega install.");
                return;
            }
            if (hardware == null || (requested != null && hardware.category() != requested)) {
                inventorySlot = findHardwareInInventory(player, requested);
                if (inventorySlot >= 0) hardware = hardwareFrom(player.getInventory().getItem(inventorySlot));
            }
            if (hardware == null) {
                plugin.msg().send(player, requested == null ? "&cHold a MegaStocks hardware part, or use /mega install <slot>." : "&cYou do not have an installable &e" + nice(requested.name()) + " &cpart.");
                return;
            }
            if (requested != null && hardware.category() != requested) {
                plugin.msg().send(player, "&cThat part is &e" + nice(hardware.category().name()) + "&c, not &e" + nice(requested.name()) + "&c.");
                return;
            }
            if (!machine.canInstall(hardware)) {
                plugin.msg().send(player, "&cNo open &e" + nice(hardware.category().name()) + " &cslot on that machine.");
                return;
            }
            machine.install(hardware);
            if (inventorySlot >= 0) {
                ItemStack stack = player.getInventory().getItem(inventorySlot);
                takeOne(stack);
                player.getInventory().setItem(inventorySlot, stack == null || stack.getAmount() <= 0 ? new ItemStack(Material.AIR) : stack);
            } else {
                takeOne(hand);
                player.getInventory().setItemInMainHand(hand.getAmount() <= 0 ? new ItemStack(Material.AIR) : hand);
            }
            plugin.data().markDirty();
            plugin.msg().send(player, "&aInstalled &b" + hardware.displayName() + " &aon &e" + machine.kind().displayName() + "&a.");
        });
    }

    private void remove(CommandSender sender, String[] args) {
        requirePlayer(sender, player -> {
            if (!player.hasPermission("megastocks.compat.commands")) {
                plugin.msg().send(player, "&cNo permission.");
                return;
            }
            if (args.length < 2) {
                plugin.msg().send(player, "&cUsage: /mega remove <gpu|cpu|ram|motherboard|psu|storage|network|cooling>");
                return;
            }
            HardwareCategory category = categoryFrom(args[1]);
            if (category == null) {
                plugin.msg().send(player, "&cUnknown slot. Use gpu, cpu, ram, motherboard, psu, storage, network, module, or cooling.");
                return;
            }
            MachineData machine = lookedMachine(player);
            if (machine == null) {
                plugin.msg().send(player, "&cLook at one of your MegaStocks machines within 6 blocks, then run this again.");
                return;
            }
            if (!canEdit(player, machine)) return;
            HardwareKind removed = machine.removeLast(category);
            if (removed == null) {
                plugin.msg().send(player, "&cNo installed &e" + nice(category.name()) + " &cpart in that machine.");
                return;
            }
            player.getInventory().addItem(plugin.items().hardwareItem(removed, 1)).values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            plugin.data().markDirty();
            plugin.msg().send(player, "&eRemoved &b" + removed.displayName() + " &efrom &6" + machine.kind().displayName() + "&e.");
        });
    }

    private void pickup(CommandSender sender) {
        requirePlayer(sender, player -> {
            if (!player.hasPermission("megastocks.compat.commands")) {
                plugin.msg().send(player, "&cNo permission.");
                return;
            }
            MachineData machine = lookedMachine(player);
            if (machine == null) {
                plugin.msg().send(player, "&cLook at a MegaStocks machine within 6 blocks, then run this again.");
                return;
            }
            plugin.machines().pickupMachine(player, machine, true);
        });
    }

    private void repair(CommandSender sender) {
        requirePlayer(sender, player -> {
            if (!player.hasPermission("megastocks.compat.commands")) {
                plugin.msg().send(player, "&cNo permission.");
                return;
            }
            MachineData machine = lookedMachine(player);
            if (machine == null) {
                plugin.msg().send(player, "&cLook at one of your MegaStocks machines within 6 blocks, then run this again.");
                return;
            }
            if (!canEdit(player, machine)) return;
            ItemStack hand = player.getInventory().getItemInMainHand();
            HardwareKind repair = hardwareFrom(hand);
            if (repair == null || !repair.isRepairItem()) {
                plugin.msg().send(player, "&cHold a MegaStocks repair kit/box, then run &e/mega repair&c.");
                return;
            }
            double before = machine.heat();
            machine.heat(Math.max(0.0, machine.heat() - repair.repairAmount()));
            if (machine.heat() < plugin.getConfig().getDouble("machine.overheat-at", 100.0)) machine.enabled(true);
            takeOne(hand);
            player.getInventory().setItemInMainHand(hand.getAmount() <= 0 ? new ItemStack(Material.AIR) : hand);
            plugin.data().markDirty();
            plugin.msg().send(player, "&aUsed &e" + repair.displayName() + "&a. Heat: &c" + String.format("%.1f", before) + " &7-> &a" + String.format("%.1f", machine.heat()));
        });
    }

    private HardwareKind hardwareFrom(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        if (!"hardware".equalsIgnoreCase(plugin.items().readMegaType(item))) return null;
        return HardwareKind.byId(plugin.items().readMegaId(item));
    }

    private int findHardwareInInventory(Player player, HardwareCategory category) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            HardwareKind hardware = hardwareFrom(contents[i]);
            if (hardware == null || hardware.isRepairItem()) continue;
            if (category == null || hardware.category() == category) return i;
        }
        return -1;
    }

    private HardwareCategory categoryFrom(String raw) {
        if (raw == null) return null;
        String cleaned = raw.toLowerCase(Locale.ROOT).replace("-", "_").replace(" ", "_");
        return switch (cleaned) {
            case "gpu", "graphics", "graphics_card", "graphicscard" -> HardwareCategory.GPU;
            case "cpu", "processor" -> HardwareCategory.CPU;
            case "ram", "memory" -> HardwareCategory.RAM;
            case "motherboard", "mobo", "board" -> HardwareCategory.MOTHERBOARD;
            case "psu", "power", "power_supply", "powersupply" -> HardwareCategory.PSU;
            case "storage", "drive", "ssd", "hdd" -> HardwareCategory.STORAGE;
            case "network", "wifi", "ethernet", "vpn", "firewall" -> HardwareCategory.NETWORK;
            case "module", "modules", "overclock", "automation" -> HardwareCategory.MODULE;
            case "cooling", "cooler", "cooling_part", "fan" -> HardwareCategory.COOLING_PART;
            default -> null;
        };
    }

    private void takeOne(ItemStack stack) {
        if (stack != null) stack.setAmount(Math.max(0, stack.getAmount() - 1));
    }

    private String nice(String raw) {
        String lower = raw.toLowerCase(Locale.ROOT).replace('_', ' ');
        StringBuilder out = new StringBuilder();
        for (String part : lower.split(" ")) {
            if (part.isEmpty()) continue;
            if (out.length() > 0) out.append(' ');
            out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return out.toString();
    }

    private void listItems(CommandSender sender) {
        plugin.msg().send(sender, "&6Machine IDs:");
        StringBuilder machines = new StringBuilder();
        for (MachineKind kind : MachineKind.values()) {
            if (machines.length() > 0) machines.append(", ");
            machines.append(kind.id());
        }
        sender.sendMessage(plugin.msg().color("&e" + machines));
        plugin.msg().send(sender, "&bHardware IDs:");
        StringBuilder hw = new StringBuilder();
        for (HardwareKind kind : HardwareKind.values()) {
            if (hw.length() > 0) hw.append(", ");
            hw.append(kind.id());
        }
        sender.sendMessage(plugin.msg().color("&b" + hw));
    }

    private void adminStats(CommandSender sender) {
        if (!sender.hasPermission("megastocks.admin")) {
            plugin.msg().send(sender, "&cNo permission.");
            return;
        }
        plugin.msg().send(sender, "&6Server machines: &e" + plugin.data().machines().size());
        plugin.msg().send(sender, "&6Vault hooked: &e" + plugin.economy().isReady() + " &7(" + plugin.economy().providerName() + ")");
        int owners = plugin.machines().allOwnerStats().size();
        plugin.msg().send(sender, "&6Machine owners: &e" + owners);
        plugin.msg().send(sender, "&6Machine stealing: " + (plugin.getConfig().getBoolean("machine.allow-player-stealing", true) ? "&aON" : "&cOFF"));
        plugin.msg().send(sender, "&6Wallet hacking: " + (plugin.hacking().enabled() ? "&aON" : "&cOFF"));
    }

    private void stealing(CommandSender sender, String[] args) {
        if (!sender.hasPermission("megastocks.stealing.toggle") && !sender.hasPermission("megastocks.admin")) {
            plugin.msg().send(sender, "&cNo permission.");
            return;
        }
        boolean current = plugin.getConfig().getBoolean("machine.allow-player-stealing", true);
        if (args.length < 2 || args[1].equalsIgnoreCase("status")) {
            plugin.msg().send(sender, "&6Machine stealing is currently " + (current ? "&aON" : "&cOFF") + "&6.");
            sender.sendMessage(plugin.msg().color("&7Use &e/mega stealing on&7, &e/mega stealing off&7, or &e/mega stealing toggle&7."));
            return;
        }

        String value = args[1].toLowerCase(Locale.ROOT);
        boolean enabled;
        switch (value) {
            case "on", "true", "yes", "enable", "enabled" -> enabled = true;
            case "off", "false", "no", "disable", "disabled" -> enabled = false;
            case "toggle" -> enabled = !current;
            default -> {
                plugin.msg().send(sender, "&cUsage: /mega stealing <on|off|toggle|status>");
                return;
            }
        }

        plugin.getConfig().set("machine.allow-player-stealing", enabled);
        plugin.saveConfig();
        plugin.msg().send(sender, "&6Machine stealing is now " + (enabled ? "&aON" : "&cOFF") + "&6.");
    }


    private void hack(CommandSender sender, String[] args) {
        requirePlayer(sender, player -> {
            if (args.length < 2) {
                plugin.gui().openHacking(player);
                return;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                plugin.msg().send(player, "&cTarget must be online for fake BTC hacking.");
                return;
            }
            var result = plugin.hacking().attemptHack(player, target.getUniqueId());
            plugin.msg().send(player, result.message());
        });
    }



    private void scan(CommandSender sender) {
        requirePlayer(sender, player -> {
            HardwareKind held = hardwareFrom(player.getInventory().getItemInMainHand());
            if (held == null || !held.isScannerTool()) {
                plugin.msg().send(player, "&cHold a MegaStocks scanner item, then look at a machine and use &e/mega scan&c.");
                return;
            }
            MachineData machine = lookedMachine(player);
            if (machine == null) {
                plugin.msg().send(player, "&cLook at a MegaStocks machine within 6 blocks, then use &e/mega scan&c.");
                return;
            }
            plugin.msg().send(player, "&6Scanner: &e" + held.displayName() + " &7-> &b" + machine.kind().displayName());
            player.sendMessage(plugin.msg().color("&7Owner: &f" + machine.ownerName() + " &8| &7Area: &b" + plugin.areas().displayName(machine.areaId())));
            player.sendMessage(plugin.msg().color("&7Level: &f" + machine.level() + " &8| &7Enabled: " + (machine.enabled() ? "&aYes" : "&cNo") + " &8| &7Heat: &c" + String.format("%.1f", machine.heat())));
            player.sendMessage(plugin.msg().color("&7Power Use: &e" + String.format("%,.0fW", machine.effectivePowerUse()) + " &8| &7Heat Output: &c" + String.format("%,.0f", machine.effectiveHeat())));
            player.sendMessage(plugin.msg().color("&7Hashrate: &d" + String.format("%,.0f", machine.effectiveHashRate()) + " &8| &7BTC/min: &b" + String.format("%.8f", machine.effectiveBtcPerMinute())));
            player.sendMessage(plugin.msg().color("&7Stored BTC: &b" + plugin.msg().btc(machine.storedBtc()) + " &8| &7Hardware installed: &b" + machine.installedCount()));
            if (held == HardwareKind.MACHINE_TRACKER) player.sendMessage(plugin.msg().color("&7Location key: &8" + machine.locationKey()));
            if (held == HardwareKind.POWER_USAGE_SCANNER) player.sendMessage(plugin.msg().color("&ePower tip: add generators/wires in the same area if free power is negative."));
            if (held == HardwareKind.HEAT_SCANNER) player.sendMessage(plugin.msg().color("&bHeat tip: add cooling blocks/pipes or thermal optimizer modules."));
            if (held == HardwareKind.PROFIT_SCANNER) player.sendMessage(plugin.msg().color("&aProfit tip: overclock modules boost BTC but make more heat."));
        });
    }

    private void area(CommandSender sender, String[] args) {
        requirePlayer(sender, player -> {
            if (args.length < 2 || args[1].equalsIgnoreCase("menu")) {
                plugin.gui().openAreaList(player);
                return;
            }
            String action = args[1].toLowerCase(Locale.ROOT);
            switch (action) {
                case "list" -> {
                    plugin.msg().send(player, "&6Your MegaStocks areas:");
                    for (String id : plugin.areas().areasFor(player.getUniqueId())) {
                        player.sendMessage(plugin.msg().color("&b- " + plugin.areas().displayName(id) + " &7(" + id + ") &8- &f" + plugin.areas().machineCount(player.getUniqueId(), id) + " machines"));
                    }
                    plugin.gui().openAreaList(player);
                }
                case "create", "make" -> {
                    if (args.length < 3) { plugin.msg().send(player, "&cUsage: /mega area create <name>"); return; }
                    String id = plugin.areas().sanitize(joinArgs(args, 2));
                    plugin.areas().ensureArea(player, id);
                    plugin.msg().send(player, "&aCreated area &b" + plugin.areas().displayName(id) + "&a. Use &e/mega area set " + id + " &ato place machines there.");
                    plugin.gui().openAreaList(player);
                }
                case "set", "active", "select" -> {
                    if (args.length < 3) { plugin.msg().send(player, "&cUsage: /mega area set <name>"); return; }
                    String id = plugin.areas().sanitize(joinArgs(args, 2));
                    plugin.areas().activeArea(player, id);
                    plugin.msg().send(player, "&aActive placement area is now &b" + plugin.areas().displayName(id) + "&a. New machines you place go there.");
                }
                case "clear", "default" -> {
                    plugin.areas().activeArea(player, "default");
                    plugin.msg().send(player, "&aActive placement area reset to &bDefault Area&a.");
                }
                case "delete", "remove" -> {
                    if (args.length < 3) { plugin.msg().send(player, "&cUsage: /mega area delete <name>"); return; }
                    String id = plugin.areas().sanitize(joinArgs(args, 2));
                    if (plugin.areas().deleteArea(player, id)) plugin.msg().send(player, "&eDeleted area &b" + plugin.areas().displayName(id) + "&e and moved its machines to Default Area.");
                    else plugin.msg().send(player, "&cThat area could not be deleted. Default Area cannot be deleted.");
                }
                case "assign", "move" -> {
                    String id = args.length >= 3 ? plugin.areas().sanitize(joinArgs(args, 2)) : plugin.areas().activeArea(player);
                    MachineData machine = lookedMachine(player);
                    if (machine == null) { plugin.msg().send(player, "&cLook at a MegaStocks machine within 6 blocks, then run this again."); return; }
                    if (!canEdit(player, machine)) return;
                    plugin.areas().ensureArea(player, id);
                    plugin.areas().assign(machine, id);
                    plugin.msg().send(player, "&aMoved &e" + machine.kind().displayName() + " &ato area &b" + plugin.areas().displayName(id) + "&a.");
                }
                case "station", "link" -> {
                    if (args.length < 3) { plugin.msg().send(player, "&cUsage: /mega area station <name|all>"); return; }
                    String raw = joinArgs(args, 2);
                    String id = raw.equalsIgnoreCase("all") ? "all" : plugin.areas().sanitize(raw);
                    MachineData machine = lookedMachine(player);
                    if (machine == null) { plugin.msg().send(player, "&cLook at a Resource Monitor, Area Monitor, Power Station, or Mining Control Station."); return; }
                    if (!canEdit(player, machine)) return;
                    plugin.areas().assign(machine, id);
                    plugin.msg().send(player, "&aLinked &e" + machine.kind().displayName() + " &ato monitor &b" + ("all".equalsIgnoreCase(id) ? "All Areas" : plugin.areas().displayName(id)) + "&a.");
                }
                case "monitor", "view" -> {
                    if (args.length < 3) { plugin.gui().openAreaList(player); return; }
                    String raw = joinArgs(args, 2);
                    String id = raw.equalsIgnoreCase("all") ? null : plugin.areas().sanitize(raw);
                    plugin.gui().openMonitor(player, id);
                }
                default -> plugin.msg().send(player, "&cArea commands: /mega area, /mega area create <name>, /mega area set <name>, /mega area assign [name], /mega area station <name|all>, /mega area monitor <name|all>");
            }
        });
    }

    private MachineData lookedMachine(Player player) {
        Block block = player.getTargetBlockExact(6);
        if (block != null) {
            MachineData targeted = plugin.machines().at(block);
            if (targeted != null) return targeted;
        }
        return nearestMachine(player, 6.0);
    }

    private MachineData nearestMachine(Player player, double radius) {
        MachineData best = null;
        double bestDistance = radius * radius;
        for (MachineData machine : plugin.data().allMachines()) {
            Location location = com.weston.megastocks.util.LocationKey.parse(machine.locationKey());
            if (location == null || location.getWorld() == null || !location.getWorld().equals(player.getWorld())) continue;
            double distance = location.clone().add(0.5, 0.5, 0.5).distanceSquared(player.getLocation());
            if (distance <= bestDistance) {
                bestDistance = distance;
                best = machine;
            }
        }
        return best;
    }

    private boolean canEdit(Player player, MachineData machine) {
        if (machine.owner().equals(player.getUniqueId()) || player.hasPermission("megastocks.admin")) return true;
        plugin.msg().send(player, "&cThat machine belongs to &e" + machine.ownerName() + "&c.");
        return false;
    }

    private String joinArgs(String[] args, int start) {
        StringBuilder out = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (out.length() > 0) out.append('_');
            out.append(args[i]);
        }
        return out.toString();
    }

    private void hacking(CommandSender sender, String[] args) {
        if (!sender.hasPermission("megastocks.hacking.toggle") && !sender.hasPermission("megastocks.admin")) {
            plugin.msg().send(sender, "&cNo permission.");
            return;
        }
        boolean current = plugin.hacking().enabled();
        if (args.length < 2 || args[1].equalsIgnoreCase("status")) {
            plugin.msg().send(sender, "&6Wallet hacking is currently " + (current ? "&aON" : "&cOFF") + "&6.");
            sender.sendMessage(plugin.msg().color("&7Use &e/mega hacking on&7, &e/mega hacking off&7, or &e/mega hacking toggle&7."));
            return;
        }
        String value = args[1].toLowerCase(Locale.ROOT);
        boolean enabled;
        switch (value) {
            case "on", "true", "yes", "enable", "enabled" -> enabled = true;
            case "off", "false", "no", "disable", "disabled" -> enabled = false;
            case "toggle" -> enabled = !current;
            default -> {
                plugin.msg().send(sender, "&cUsage: /mega hacking <on|off|toggle|status>");
                return;
            }
        }
        plugin.hacking().enabled(enabled);
        plugin.msg().send(sender, "&6Wallet hacking is now " + (enabled ? "&aON" : "&cOFF") + "&6.");
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("megastocks.admin")) {
            plugin.msg().send(sender, "&cNo permission.");
            return;
        }
        plugin.reloadConfig();
        plugin.economy().setup();
        plugin.msg().send(sender, "&aConfig reloaded. Vault: &e" + plugin.economy().providerName());
    }

    private void give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("megastocks.admin")) {
            plugin.msg().send(sender, "&cNo permission.");
            return;
        }
        if (args.length < 2) {
            plugin.msg().send(sender, "&cUsage: /mega give <id> [amount] [player]");
            return;
        }
        int amount = 1;
        if (args.length >= 3) {
            try { amount = Math.max(1, Integer.parseInt(args[2])); }
            catch (NumberFormatException ignored) { }
        }
        Player target = sender instanceof Player p ? p : null;
        if (args.length >= 4) target = Bukkit.getPlayerExact(args[3]);
        if (target == null) {
            plugin.msg().send(sender, "&cTarget player not found.");
            return;
        }
        String id = args[1];
        MachineKind machine = MachineKind.byId(id);
        if (machine != null) {
            target.getInventory().addItem(plugin.items().machineItem(machine, amount));
            plugin.msg().send(sender, "&aGave &e" + amount + "x " + machine.displayName() + " &ato &f" + target.getName() + "&a.");
            return;
        }
        HardwareKind hardware = HardwareKind.byId(id);
        if (hardware != null) {
            if (hardware.isBitcoinUsb()) {
                PlayerData td = plugin.data().player(target.getUniqueId(), target.getName());
                if (td.hasBitcoinCode()) target.getInventory().addItem(plugin.items().bitcoinUsbItem(td, amount));
                else target.getInventory().addItem(plugin.items().hardwareItem(hardware, amount));
            } else target.getInventory().addItem(plugin.items().hardwareItem(hardware, amount));
            plugin.msg().send(sender, "&aGave &e" + amount + "x " + hardware.displayName() + " &ato &f" + target.getName() + "&a.");
            return;
        }
        plugin.msg().send(sender, "&cUnknown id. Use &e/mega listitems&c.");
    }


    private void gift(CommandSender sender, String[] args) {
        requirePlayer(sender, player -> {
            if (!player.hasPermission("megastocks.gift")) {
                plugin.msg().send(player, "&cNo permission.");
                return;
            }
            if (args.length < 2) {
                plugin.msg().send(player, "&cUsage: /gift <player> <btc> OR /gift bitcoin <player> <amount> OR /gift stock <player> <symbol> <shares> OR /gift item <player> [amount]");
                return;
            }
            String mode = args[0].toLowerCase(Locale.ROOT);
            if (mode.equals("bitcoin") || mode.equals("btc")) {
                if (args.length < 3) { plugin.msg().send(player, "&cUsage: /gift bitcoin <player> <amount>"); return; }
                giftBitcoin(player, args[1], args[2]);
                return;
            }
            if (mode.equals("stock") || mode.equals("shares")) {
                if (args.length < 4) { plugin.msg().send(player, "&cUsage: /gift stock <player> <symbol> <shares>"); return; }
                giftStock(player, args[1], args[2], args[3]);
                return;
            }
            if (mode.equals("item")) {
                if (args.length < 2) { plugin.msg().send(player, "&cUsage: /gift item <player> [amount]"); return; }
                int amount = args.length >= 3 ? parseInt(args[2], 1) : 1;
                giftHeldItem(player, args[1], amount);
                return;
            }
            giftBitcoin(player, args[0], args[1]);
        });
    }

    private void giftBitcoin(Player player, String targetName, String amountRaw) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || target.getUniqueId().equals(player.getUniqueId())) {
            plugin.msg().send(player, "&cTarget must be an online player that is not you.");
            return;
        }
        double amount = parseDouble(amountRaw, -1.0);
        if (amount <= 0.0) { plugin.msg().send(player, "&cAmount must be positive."); return; }
        PlayerData from = plugin.data().player(player.getUniqueId(), player.getName());
        if (from.fakeBtc() < amount) {
            plugin.msg().send(player, "&cYou only have &b" + plugin.msg().btc(from.fakeBtc()) + "&c.");
            return;
        }
        PlayerData to = plugin.data().player(target.getUniqueId(), target.getName());
        from.fakeBtc(from.fakeBtc() - amount);
        to.fakeBtc(to.fakeBtc() + amount);
        plugin.data().markDirty();
        plugin.msg().send(player, "&aGifted &b" + plugin.msg().btc(amount) + " &ato &e" + target.getName() + "&a.");
        plugin.msg().send(target, "&aYou received &b" + plugin.msg().btc(amount) + " &afrom &e" + player.getName() + "&a.");
    }

    private void giftStock(Player player, String targetName, String symbolRaw, String sharesRaw) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || target.getUniqueId().equals(player.getUniqueId())) {
            plugin.msg().send(player, "&cTarget must be an online player that is not you.");
            return;
        }
        String symbol = symbolRaw.toUpperCase(Locale.ROOT);
        if (plugin.market().stock(symbol) == null) { plugin.msg().send(player, "&cUnknown stock symbol."); return; }
        int shares = parseInt(sharesRaw, -1);
        if (shares <= 0) { plugin.msg().send(player, "&cShares must be positive."); return; }
        PlayerData from = plugin.data().player(player.getUniqueId(), player.getName());
        if (from.sharesOf(symbol) < shares) { plugin.msg().send(player, "&cYou do not own that many shares of &e" + symbol + "&c."); return; }
        PlayerData to = plugin.data().player(target.getUniqueId(), target.getName());
        from.addShares(symbol, -shares);
        to.addShares(symbol, shares);
        plugin.data().markDirty();
        plugin.msg().send(player, "&aGifted &e" + shares + " &ashares of &f" + symbol + " &ato &e" + target.getName() + "&a.");
        plugin.msg().send(target, "&aYou received &e" + shares + " &ashares of &f" + symbol + " &afrom &e" + player.getName() + "&a.");
    }

    private void giftHeldItem(Player player, String targetName, int amount) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || target.getUniqueId().equals(player.getUniqueId())) {
            plugin.msg().send(player, "&cTarget must be an online player that is not you.");
            return;
        }
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!plugin.items().isMegaItem(hand)) {
            plugin.msg().send(player, "&cHold a MegaStocks item to gift it.");
            return;
        }
        int giveAmount = Math.max(1, Math.min(amount, hand.getAmount()));
        ItemStack gift = hand.clone();
        gift.setAmount(giveAmount);
        hand.setAmount(hand.getAmount() - giveAmount);
        player.getInventory().setItemInMainHand(hand.getAmount() <= 0 ? new ItemStack(Material.AIR) : hand);
        target.getInventory().addItem(gift).values().forEach(leftover -> target.getWorld().dropItemNaturally(target.getLocation(), leftover));
        plugin.msg().send(player, "&aGifted &e" + giveAmount + "x &aMegaStocks item to &e" + target.getName() + "&a.");
        plugin.msg().send(target, "&aYou received a MegaStocks item gift from &e" + player.getName() + "&a.");
    }

    private void adminBitcoinStandalone(CommandSender sender, String action, String[] args) {
        String[] shifted = new String[args.length + 1];
        shifted[0] = action;
        System.arraycopy(args, 0, shifted, 1, args.length);
        adminBitcoinSubcommand(sender, action, shifted);
    }

    private void adminBitcoinSubcommand(CommandSender sender, String action, String[] args) {
        if (!sender.hasPermission("megastocks.admin")) { plugin.msg().send(sender, "&cNo permission."); return; }
        if (args.length < 4 || !args[1].equalsIgnoreCase("bitcoin")) {
            plugin.msg().send(sender, "&cUsage: /mega " + action + " bitcoin <player> <amount> OR /" + action + " bitcoin <player> <amount>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) { plugin.msg().send(sender, "&cTarget player must be online."); return; }
        double amount = parseDouble(args[3], -1.0);
        if (amount <= 0.0) { plugin.msg().send(sender, "&cAmount must be positive."); return; }
        PlayerData data = plugin.data().player(target.getUniqueId(), target.getName());
        if (action.equalsIgnoreCase("add")) data.fakeBtc(data.fakeBtc() + amount);
        else data.fakeBtc(Math.max(0.0, data.fakeBtc() - amount));
        plugin.data().markDirty();
        plugin.msg().send(sender, "&a" + (action.equalsIgnoreCase("add") ? "Added " : "Took ") + plugin.msg().btc(amount) + " &afor &e" + target.getName() + "&a. New wallet: &b" + plugin.msg().btc(data.fakeBtc()));
        plugin.msg().send(target, "&eStaff " + (action.equalsIgnoreCase("add") ? "added " : "removed ") + "&b" + plugin.msg().btc(amount) + " &efrom your fake BTC wallet.");
    }

    private void admin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("megastocks.admin")) { plugin.msg().send(sender, "&cNo permission."); return; }
        if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
            plugin.msg().send(sender, "&cAdmin controls:");
            sender.sendMessage(plugin.msg().color("&e/mega admin stock create <symbol> <price> <name...>"));
            sender.sendMessage(plugin.msg().color("&e/mega admin stock set <symbol> <price>"));
            sender.sendMessage(plugin.msg().color("&e/mega admin stock remove <symbol>"));
            sender.sendMessage(plugin.msg().color("&e/mega admin price machine <id> <price>"));
            sender.sendMessage(plugin.msg().color("&e/mega admin price hardware <id> <price>"));
            sender.sendMessage(plugin.msg().color("&e/mega admin price bitcoin <price>"));
            sender.sendMessage(plugin.msg().color("&e/mega add bitcoin <player> <amount> &7or &e/add bitcoin <player> <amount>"));
            sender.sendMessage(plugin.msg().color("&e/mega take bitcoin <player> <amount> &7or &e/take bitcoin <player> <amount>"));
            return;
        }
        String sub = args[1].toLowerCase(Locale.ROOT);
        if (sub.equals("stock") || sub.equals("stocks")) { stockAdmin(sender, args, 2); return; }
        if (sub.equals("price") || sub.equals("set")) { adminPrice(sender, args, 2); return; }
        plugin.msg().send(sender, "&cUnknown admin command. Use /mega admin help.");
    }

    private void adminSetDirect(CommandSender sender, String[] args) {
        if (!sender.hasPermission("megastocks.admin")) { plugin.msg().send(sender, "&cNo permission."); return; }
        if (args.length < 3) { plugin.msg().send(sender, "&cUsage: /mega set bitcoin <price> OR /mega set stock <symbol> <price>"); return; }
        if (args[1].equalsIgnoreCase("bitcoin") || args[1].equalsIgnoreCase("btc")) {
            double price = parseDouble(args[2], -1.0);
            if (price <= 0.0) { plugin.msg().send(sender, "&cPrice must be positive."); return; }
            plugin.getConfig().set("economy.btc-sell-price", price);
            plugin.saveConfig();
            plugin.msg().send(sender, "&aFake BTC sell price is now &e" + plugin.msg().money(price) + " &aper BTC.");
            return;
        }
        if (args[1].equalsIgnoreCase("stock")) { stockSetPrice(sender, args[2], args.length >= 4 ? args[3] : null); return; }
        plugin.msg().send(sender, "&cUsage: /mega set bitcoin <price> OR /mega set stock <symbol> <price>");
    }

    private void stockAdmin(CommandSender sender, String[] args, int offset) {
        if (!sender.hasPermission("megastocks.admin")) { plugin.msg().send(sender, "&cNo permission."); return; }
        if (args.length <= offset) { plugin.msg().send(sender, "&cUsage: /mega admin stock <create|set|remove|list> ..."); return; }
        String action = args[offset].toLowerCase(Locale.ROOT);
        if (action.equals("create") || action.equals("add")) {
            if (args.length < offset + 4) { plugin.msg().send(sender, "&cUsage: /mega admin stock create <symbol> <price> <name...>"); return; }
            String symbol = args[offset + 1];
            double price = parseDouble(args[offset + 2], -1.0);
            String name = joinArgs(args, offset + 3).replace('_', ' ');
            if (price <= 0.0) { plugin.msg().send(sender, "&cPrice must be positive."); return; }
            if (plugin.market().createStock(symbol, name, price)) plugin.msg().send(sender, "&aCreated stock &f" + symbol.toUpperCase(Locale.ROOT) + " &aat &e" + plugin.msg().money(price) + "&a.");
            else plugin.msg().send(sender, "&cCould not create stock. Symbol must be 2-6 letters/numbers and not already exist.");
            return;
        }
        if (action.equals("set") || action.equals("setprice") || action.equals("price")) {
            if (args.length < offset + 3) { plugin.msg().send(sender, "&cUsage: /mega admin stock set <symbol> <price>"); return; }
            stockSetPrice(sender, args[offset + 1], args[offset + 2]);
            return;
        }
        if (action.equals("remove") || action.equals("delete")) {
            if (args.length < offset + 2) { plugin.msg().send(sender, "&cUsage: /mega admin stock remove <symbol>"); return; }
            if (plugin.market().removeStock(args[offset + 1])) plugin.msg().send(sender, "&eRemoved stock &f" + args[offset + 1].toUpperCase(Locale.ROOT) + "&e.");
            else plugin.msg().send(sender, "&cUnknown stock.");
            return;
        }
        if (action.equals("list")) {
            plugin.msg().send(sender, "&6Stocks: &e" + plugin.market().stocks().stream().map(s -> s.symbol() + "=" + plugin.msg().money(s.price())).limit(50).toList());
            return;
        }
        plugin.msg().send(sender, "&cUsage: /mega admin stock <create|set|remove|list> ...");
    }

    private void stockSetPrice(CommandSender sender, String symbol, String priceRaw) {
        double price = parseDouble(priceRaw, -1.0);
        if (price <= 0.0) { plugin.msg().send(sender, "&cPrice must be positive."); return; }
        if (plugin.market().setStockPrice(symbol, price)) plugin.msg().send(sender, "&aSet stock &f" + symbol.toUpperCase(Locale.ROOT) + " &ato &e" + plugin.msg().money(price) + "&a.");
        else plugin.msg().send(sender, "&cUnknown stock symbol.");
    }

    private void adminPrice(CommandSender sender, String[] args, int offset) {
        if (!sender.hasPermission("megastocks.admin")) { plugin.msg().send(sender, "&cNo permission."); return; }
        if (args.length <= offset) { plugin.msg().send(sender, "&cUsage: /mega admin price <bitcoin|machine|hardware> ..."); return; }
        String target = args[offset].toLowerCase(Locale.ROOT);
        if (target.equals("bitcoin") || target.equals("btc")) {
            if (args.length < offset + 2) { plugin.msg().send(sender, "&cUsage: /mega admin price bitcoin <price>"); return; }
            double price = parseDouble(args[offset + 1], -1.0);
            if (price <= 0.0) { plugin.msg().send(sender, "&cPrice must be positive."); return; }
            plugin.getConfig().set("economy.btc-sell-price", price);
            plugin.saveConfig();
            plugin.msg().send(sender, "&aFake BTC sell price is now &e" + plugin.msg().money(price) + " &aper BTC.");
            return;
        }
        if (target.equals("machine")) {
            if (args.length < offset + 3) { plugin.msg().send(sender, "&cUsage: /mega admin price machine <id> <price>"); return; }
            MachineKind kind = MachineKind.byId(args[offset + 1]);
            double price = parseDouble(args[offset + 2], -1.0);
            if (kind == null || price < 0.0) { plugin.msg().send(sender, "&cUnknown machine or invalid price."); return; }
            plugin.setMachinePrice(kind, price);
            plugin.msg().send(sender, "&aSet machine &e" + kind.id() + " &aprice to &e" + plugin.msg().money(price) + "&a.");
            return;
        }
        if (target.equals("hardware") || target.equals("item")) {
            if (args.length < offset + 3) { plugin.msg().send(sender, "&cUsage: /mega admin price hardware <id> <price>"); return; }
            HardwareKind kind = HardwareKind.byId(args[offset + 1]);
            double price = parseDouble(args[offset + 2], -1.0);
            if (kind == null || price < 0.0) { plugin.msg().send(sender, "&cUnknown hardware or invalid price."); return; }
            plugin.setHardwarePrice(kind, price);
            plugin.msg().send(sender, "&aSet hardware &e" + kind.id() + " &aprice to &e" + plugin.msg().money(price) + "&a.");
            return;
        }
        plugin.msg().send(sender, "&cUsage: /mega admin price <bitcoin|machine|hardware> ...");
    }

    private double parseDouble(String raw, double fallback) {
        if (raw == null) return fallback;
        try { return Double.parseDouble(raw.replace(",", "")); }
        catch (NumberFormatException ex) { return fallback; }
    }

    private int parseInt(String raw, int fallback) {
        if (raw == null) return fallback;
        try { return Integer.parseInt(raw.replace(",", "")); }
        catch (NumberFormatException ex) { return fallback; }
    }

    private void requirePlayer(CommandSender sender, PlayerAction action) {
        if (!(sender instanceof Player player)) {
            plugin.msg().send(sender, "&cOnly players can use this command.");
            return;
        }
        if (!player.hasPermission("megastocks.use")) {
            plugin.msg().send(sender, "&cNo permission.");
            return;
        }
        action.run(player);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String commandName = command.getName().toLowerCase(Locale.ROOT);
        if (commandName.equals("code")) {
            if (args.length == 1) return filter(args[0], Arrays.asList("show", "set", "reset"));
            return List.of();
        }
        if (commandName.equals("gift")) {
            if (args.length == 1) return filter(args[0], Arrays.asList("bitcoin", "btc", "stock", "item"));
            if (args.length == 2 && (args[0].equalsIgnoreCase("bitcoin") || args[0].equalsIgnoreCase("btc") || args[0].equalsIgnoreCase("stock") || args[0].equalsIgnoreCase("item"))) return null;
            return List.of();
        }
        if (commandName.equals("add") || commandName.equals("take")) {
            if (args.length == 1) return filter(args[0], Arrays.asList("bitcoin"));
            if (args.length == 2) return null;
            return List.of();
        }
        if (args.length == 1) {
            return filter(args[0], Arrays.asList("help", "menu", "shop", "cooling", "coolers", "wires", "wire", "hardware", "market", "stocks", "code", "area", "areas", "hack", "hacking", "monitor", "scan", "scanner", "wallet", "balance", "collect", "sellbtc", "install", "remove", "pickup", "repair", "geyser", "bedrock", "compat", "gift", "add", "take", "set", "admin", "staff", "stock", "listitems", "give", "stealing", "steal", "reload", "adminstats"));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("install") || args[0].equalsIgnoreCase("remove"))) {
            return filter(args[1], Arrays.asList("gpu", "cpu", "ram", "motherboard", "psu", "storage", "network", "module", "cooling"));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("stealing") || args[0].equalsIgnoreCase("steal") || args[0].equalsIgnoreCase("hacking"))) {
            return filter(args[1], Arrays.asList("on", "off", "toggle", "status"));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("area") || args[0].equalsIgnoreCase("areas"))) {
            return filter(args[1], Arrays.asList("menu", "list", "create", "set", "clear", "delete", "assign", "station", "monitor"));
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("area") || args[0].equalsIgnoreCase("areas")) && (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("assign") || args[1].equalsIgnoreCase("station") || args[1].equalsIgnoreCase("monitor"))) {
            if (sender instanceof Player p) {
                List<String> areas = new ArrayList<>(plugin.areas().areasFor(p.getUniqueId()));
                if (args[1].equalsIgnoreCase("station") || args[1].equalsIgnoreCase("monitor")) areas.add("all");
                return filter(args[2], areas);
            }
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("staff"))) {
            return filter(args[1], Arrays.asList("help", "stock", "price"));
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("staff")) && args[1].equalsIgnoreCase("stock")) {
            return filter(args[2], Arrays.asList("create", "set", "remove", "list"));
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("staff")) && args[1].equalsIgnoreCase("price")) {
            return filter(args[2], Arrays.asList("bitcoin", "machine", "hardware"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return filter(args[1], Arrays.asList("bitcoin", "stock"));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("take"))) {
            return filter(args[1], Arrays.asList("bitcoin"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("hack")) {
            return null;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> ids = new ArrayList<>();
            for (MachineKind kind : MachineKind.values()) ids.add(kind.id());
            for (HardwareKind kind : HardwareKind.values()) ids.add(kind.id());
            return filter(args[1], ids);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            return null;
        }
        return List.of();
    }

    private List<String> filter(String prefix, List<String> options) {
        String p = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(p)).limit(40).toList();
    }

    @FunctionalInterface
    private interface PlayerAction {
        void run(Player player);
    }
}
