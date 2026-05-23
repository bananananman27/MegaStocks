# MegaStocks

MegaStocks is a Paper Minecraft plugin project for a huge fake stock-market + fake Bitcoin-mining economy server.

It does **not** mine real crypto. Everything is simulated gameplay.

## Target

- Paper 1.21.x
- Java/JDK 21
- Maven
- Vault + a Vault-compatible economy plugin, such as EssentialsX Economy

## Build with Maven

Open a terminal in this folder and run:

```bash
mvn clean package
```

The jar will be in:

```text
target/MegaStocksPlugin-4.3.0.jar
```

## Install

1. Build the jar.
2. Put the jar into your server's `plugins/` folder.
3. Install Vault.
4. Install an economy plugin that supports Vault.
5. Restart the server.
6. Run `/megastocks`.

## Main commands

```text
/megastocks      - Open the main menu
/mega            - Also opens the main menu
/stocks          - Opens the stock market directly
/mega help       - Command help
/stocks help     - Same help, using the stocks alias
/mega shop       - Buy machines by category
/mega hardware   - Buy organized GPUs, CPUs, RAM, PSUs, tools, and parts
/mega market     - Fake stock market
/mega monitor    - Resource monitor
/mega collect    - Collect fake BTC from machines
/mega sellbtc    - Sell fake BTC for Vault money
/mega listitems  - List all machine/hardware IDs
```

Admin:

```text
/mega give <id> [amount] [player]
/mega stealing <on|off|toggle|status>
/mega reload
/mega adminstats
```

## Update highlights

- Maven build added.
- JDK 21 build target added.
- `/megastocks` opens the main GUI directly.
- Back buttons added to the menus.
- Machine shop is category-based.
- Hardware shop is category-based.
- Machines and server racks support installable hardware.
- Install hardware by opening a machine and left-clicking a matching hardware slot. It works from your cursor, main hand, or inventory.
- Remove installed hardware by right-clicking the matching hardware slot.
- Repairing no longer uses money. Buy a repair item and right-click your placed machine while holding it.
- Breaking your own machine gives the machine back, drops installed hardware, and recovers stored fake BTC.
- Black Hole Miner uses Sculk Shrieker.

## Performance design

This plugin is designed to be TPS-friendly:

- One central simulation loop, not one task per machine.
- Default simulation interval is 10 seconds.
- Default market interval is 60 seconds.
- Machines are plain blocks tracked in YAML, not constantly ticking entities.
- No particles, holograms, bossbars, or scoreboards running every tick.
- GUI data is generated only when players open menus.
- Config has server and per-player machine limits.

Important config paths:

```yaml
performance:
  simulation-interval-seconds: 10
  market-update-seconds: 60
  autosave-seconds: 300
  max-machines-server: 10000
  max-machines-per-player: 250
```

## Model/resource pack status

This version intentionally uses vanilla Minecraft blocks/items so it works without Blockbench models. Later, the same IDs can be mapped to custom resource-pack models.


## v3.1 compile hotfix

Fixed missing `MenuType.SHOP_MENU`, `MenuType.HARDWARE_MENU`, and `HardwareCategory.NETWORK` enum constants so Maven compilation can continue.


## v3.3 bugfix update

- Removed the Blueprint hardware item completely from shops, commands, and list output.
- Made Vault withdrawals transactional for shops, stock buys, and machine upgrades so failed withdrawals do not give free items/upgrades.
- Fixed hardware cursor installs so the cursor stack updates correctly.
- Fixed repair item consumption so the final repair box clears safely instead of leaving an invalid amount.
- Improved cooling simulation so coolers actually reduce heat on hot machines instead of only affecting idle/cool machines.


## v3.4 stealing + easy pickup update

- Added configurable machine stealing.
- Default: `machine.allow-player-stealing: true`, so players can mine/break other players' MegaStocks machines.
- Admins/ops can toggle stealing in-game with `/mega stealing on`, `/mega stealing off`, `/mega stealing toggle`, or check it with `/mega stealing status`.
- Mining/breaking a MegaStocks machine now gives the breaker everything: the machine item, installed hardware drops, and stored fake BTC goes into the breaker's wallet.
- If stealing is disabled, only the owner or admins with `megastocks.break.other` / `megastocks.admin` can break other players' machines.
- Optional owner alerts are controlled by `machine.notify-owner-on-steal`.

## v3.5 hidden hacking + wallet security update

- Added a simulated fake BTC hacking system. This is only Minecraft gameplay; it never touches real networks, accounts, computers, or crypto.
- The hidden hacking area is inside the Stock Market menu in the bottom-right `???` button.
- Added `/mega hack` to open the hidden hacking menu.
- Added `/mega hack <onlinePlayer>` to attempt a target directly.
- Added `/mega hacking <on|off|toggle|status>` for admins/ops. Hacking is OFF by default.
- Added attack hardware: `hacker_laptop`, `packet_sniffer`, `password_cracker`, `zero_day_chip`, and `black_hat_ai`.
- Added security hardware: `vpn_router`, `firewall_node`, `encrypted_wallet_module`, and `quantum_firewall`.
- Hacking requires high attack power before a player even gets a chance.
- Higher target security means the hacker needs stronger installed hardware.
- Bank vaults, server racks, and security hardware increase wallet security.
- Bank vaults now accept more network/security hardware slots.
- Hacking has configurable cooldowns, success chance caps, and maximum steal limits in `config.yml`.

## v3.6 area + linked monitor update

- Added machine areas/zones.
- Open the area menu with `/mega area` or the new **Areas / Monitors** button in `/megastocks`.
- Create areas with `/mega area create <name>`.
- Set your active placement area with `/mega area set <name>`.
- New machines you place automatically go into your active area.
- Move a looked-at machine into an area with `/mega area assign [name]`.
- Open an area-only monitor with `/mega area monitor <name>`.
- Link a looked-at monitor/power/mining station to an area with `/mega area station <name>`.
- Link a station to every area with `/mega area station all`.
- Added station machines: `area_control_terminal` and `area_monitor_station`.
- Resource Monitor, Power Management, and Mining Control now support area-filtered views.
- Simulation now calculates power/cooling per area, so one area's generators/coolers do not magically power/cool every other area.

## v3.7 cooling blocks + easy wires update

- Added a new **Wire** machine category in the Machine Shop.
- Added `/mega wires` to open the wire/pipe shop directly.
- Added `/mega cooling` to open the cooling shop directly.
- New easy wire items:
  - `power_wire`
  - `heavy_power_wire`
  - `high_voltage_cable`
  - `area_relay_node`
  - `quantum_link_cable`
  - `coolant_pipe`
  - `cryo_coolant_pipe`
  - `universal_bus_cable`
- Wires/pipes are easy: place them in the same area as your machines and they automatically boost area power/cooling.
- No manual redstone linking is required.
- Added huge area cooling blocks:
  - `server_cooling_block`
  - `cryo_server_block`
  - `liquid_nitrogen_block`
  - `area_frost_beacon`
- These cooling blocks cool the whole area by a CRAZY amount.
- Resource Monitor and Power Management now show wire/pipe count, boosted power, boosted cooling, and server-cooling blocks.
- TPS design stays safe: wires do not run per-block tasks; they are just counted during the central simulation pass.


## v3.8 safety/defaults update

- Removed all forbidden egg-block usage from the plugin. The high-tier Ender Singularity Miner uses `END_STONE_BRICKS`.
- Hacking is now OFF by default in `config.yml`, in the code fallback, and on first upgrade to v3.8. Admins/ops can enable it with `/mega hacking on`.

## v3.9 notes

### Anti-cheat item protection
MegaStocks items now cannot be used in vanilla crafting/conversion menus. This stops custom items that visually use valuable vanilla materials from being turned into vanilla profit.

Protected by default:
- crafting tables and player crafting
- smithing tables
- anvils and grindstones
- furnaces, blast furnaces, smokers
- brewing stands
- looms, cartography tables, and stonecutters
- hardware/custom items cannot be placed as normal vanilla blocks

Config toggle:
```yml
anti-cheat:
  protect-megastocks-items: true
```

### Geyser / Bedrock-friendly controls
The normal GUIs still work, but Bedrock players can also use simple command controls while looking at a machine:

```text
/mega install
/mega install gpu
/mega install cpu
/mega install ram
/mega install motherboard
/mega install psu
/mega install storage
/mega install network
/mega install cooling
/mega remove gpu
/mega repair
/mega pickup
/mega geyser
```

These are meant as backups if a Bedrock client handles Java inventory clicks differently through Geyser.


## v4.0 Stock/Code/Security Update

- `/code` shows only the player their own fake BTC private code.
- `/code set <code>` is required before players buy mining machines or mining hardware.
- `/code reset <oldCode> <newCode>` changes a code only if the old code is known.
- Added expensive `bitcoin_usb` as a private code backup item.
- Hidden stock-market hacker terminal supports two attempts:
  - Left-click target = wallet hack.
  - Right-click target = code hack. Code hacks are much harder, generate huge heat, and never show the actual victim code to the attacker.
- Added scanners, security devices, and overclock/automation modules.
- Added a MODULE hardware slot in machine GUIs and Geyser-friendly `/mega install module` / `/mega remove module`.
- Expanded stock market symbols so the stock area has more to trade.


## v4.1 Modrinth/server-hardening update

- Removed all End Portal Frame usage. Every machine now uses a mineable block material.
- `quantum_miner` now uses `PURPUR_BLOCK`.
- `ender_reactor` now uses `PURPUR_PILLAR`.
- Machine placement now reminds players that `/mega pickup` safely picks up the machine, installed hardware, and stored fake BTC.
- Machine item lore also shows the `/mega pickup` tip.
- Anti-cheat protection now covers vanilla Crafter blocks using `CrafterCraftEvent`, so players cannot route MegaStocks items through an auto-crafter to bypass crafting protection.
- Hoppers and hopper minecarts are blocked from moving MegaStocks items into protected conversion inventories, including crafters, furnaces, smithing tables, anvils, grindstones, brewing, stonecutters, and similar menus.

### Big-server release checklist

Before posting/running on a large server:

```yaml
anti-cheat:
  protect-megastocks-items: true

hacking:
  enabled: false

performance:
  simulation-interval-seconds: 10
  market-update-seconds: 60
  autosave-seconds: 300
  max-machines-server: 10000
  max-machines-per-player: 250
```

For a very large public server, start with stealing and hacking off while testing, then enable only if the community wants PvP economy chaos:

```yaml
machine:
  allow-player-stealing: false

hacking:
  enabled: false
```


## v4.2 Server Launch / Modrinth Prep Notes

- No `DRAGON_EGG`, `END_PORTAL`, or `END_PORTAL_FRAME` blocks are used by machines.
- Every placed machine item says players can use `/mega pickup` to safely recover it with installed hardware and stored fake BTC.
- Crafter protection was added: vanilla Crafters, hoppers, crafting tables, smithing, smelting, brewing, anvils, grindstones, and other conversion menus cannot use MegaStocks items to create vanilla profit.
- The stock market GUI is paginated, so staff can add lots of stocks without breaking the menu.
- Staff commands:
  - `/mega admin stock create <symbol> <price> <name...>`
  - `/mega admin stock set <symbol> <price>`
  - `/mega admin stock remove <symbol>`
  - `/mega admin price bitcoin <price>`
  - `/mega admin price machine <id> <price>`
  - `/mega admin price hardware <id> <price>`
  - `/mega add bitcoin <player> <amount>` or `/add bitcoin <player> <amount>`
  - `/mega take bitcoin <player> <amount>` or `/take bitcoin <player> <amount>`
- Player gifting:
  - `/gift <player> <btc>`
  - `/gift bitcoin <player> <amount>`
  - `/gift stock <player> <symbol> <shares>`
  - `/gift item <player> [amount]` while holding a MegaStocks item.

For a real big server, start with conservative limits in `config.yml`, then raise `performance.max-machines-server` and `performance.max-machines-per-player` after watching timings/TPS.


## v4.3 fast bug sweep + help update

- Added/confirmed `/mega help` and `/stocks help`.
- `/stocks` now opens the stock market directly, while `/megastocks` and `/mega` open the main menu.
- New stock defaults are added on upgrades without deleting staff-created stocks.
- Hardened anti-crafter protection with actual craft cancellation, furnace-fuel blocking, hotbar-number-key swaps, and offhand swaps into protected inventories.
- Removed high-value vanilla block materials from machine placements/icons where possible: no Dragon Egg, End Portal, End Portal Frame, Beacon, Netherite, Gold Block, Emerald Block, or Iron Block machine bodies.
- Existing placed machines from older versions can still be recovered with `/mega pickup` or by mining them if they are registered.
