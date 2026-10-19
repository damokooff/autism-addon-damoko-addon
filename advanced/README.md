# Damoko Add On

A client-side addon for [AUTISM Client](https://github.com/AutismDevelopment/Autism-Client), adding movement, combat, automation, and detection modules.

> This addon requires AUTISM Client. Minecraft, Fabric Loader, Fabric API, and AUTISM API compatibility are defined by the versions declared in the addon JAR.

## Features

Addon modules appear in the **Damoko Add On**, **Trouser Streak**, and **Meteor** categories in the client menu.

### Damoko Add On

- **Auto Pilot** *(formerly Elytra Fly)* — controls elytra flight with steering options and can move a replacement firework stack into the hotbar when the selected stack runs out.
- **Auto Eat**, **Auto Gap**, **Auto EXP**, and **Auto Mend** — automate food, golden apple use, and Mending repairs.
- **Auto Log** and **Auto Respawn** — leave the game below a configured health threshold and respawn after death.
- **Auto Weapon** — selects a sword from the hotbar when a target is nearby.
- **Auto Breed**, **Auto Shearer**, and **Auto Nametag** — automate selected interactions with animals and targeted entities.
- **Spear Aura** and **Mace Aura** — automatically attack nearby targets with the matching weapon.
- **Mace Kill** — automates closing in and attacking with a mace to use smash attacks.
- **Danger** — warns about projectiles moving toward the player and attempts to hit an aimed fireball at close range.
- **Custom Cape** — choose from the bundled capes. The cape is rendered for the local player; other users do not automatically see it.

### Trouser Streak

- **Base Finder** — estimates nearby base activity from utility blocks in loaded chunks.
- **Flight Anti Kick** — periodically applies a short downward movement while gliding.
- **Player Alarm** — alerts when configured player names are nearby.
- **Player Activity** — displays local notifications when players join or leave.
- **Advanced Item ESP** — notifies you when dropped items match configured identifiers.
- **Mob Gear ESP** — reports nearby mobs wearing equipment.
- **Collectible ESP** — reports nearby item frames containing items.
- **Block scanners** — scan for Ancient Debris, diamond ore, Ender Chests, Nether portals, End portal frames, and spawners.
- **Pot ESP** — reports nearby decorated pots.
- **Spear Kill** — locks onto the targeted entity and lunges with a spear while attacking.

The listed scanners and ESP modules perform local checks and send notifications; they do not all draw highlights in the world. Base Finder only estimates activity from nearby loaded blocks; it does not scan the whole world.

### Meteor

- **Auto Walk** — continuously holds forward movement.

## HUD

- **Elytra Count** *(disabled by default)* — displays the number of elytras in your inventory and the remaining durability of the equipped elytra.

## `.enchant` command

Use this command in Creative mode while holding an item in your main hand or offhand. Enter the simple enchantment name for `<enchantment>`, for example `sharpness`.

```text
.enchant one <enchantment> max
.enchant one <enchantment> level <level>
.enchant all_possible max
.enchant all_possible level <level>
.enchant all max
.enchant all level <level>
.enchant clear
.enchant remove <enchantment>
.enchant opti
```

- `all_possible` attempts to apply enchantments compatible with the item.
- `all` attempts to apply every available enchantment.
- `opti` applies a predefined enchantment set based on the held item.

The game or server may reject or change enchantment updates.

## Installation guide

### 1. Install the required client

Install **Fabric Loader** for a Minecraft version supported by AUTISM Client, then install the matching **Fabric API** and **AUTISM Client** versions. Check the AUTISM Client repository and release notes for supported versions and installation instructions.

### 2. Get the addon JAR

Download the Damoko Add On `.jar` from the **Releases** section of this GitHub repository. For a development build, compile the project using the instructions below and use the JAR from `build/libs/`.

Do not extract the JAR.

### 3. Open your Minecraft `mods` folder

Start Minecraft once with the Fabric profile, then close the game. Open the Minecraft game directory and find its `mods` folder. Common locations are:

- **Windows:** `%AppData%\.minecraft\mods`
- **macOS:** `~/Library/Application Support/minecraft/mods`
- **Linux:** `~/.minecraft/mods`

If you use a launcher with a custom game directory or modpack instance, open that instance's folder and use its `mods` directory instead.

### 4. Add the JAR and launch

Place the Damoko Add On JAR in the same `mods` folder as AUTISM Client and Fabric API. Keep the JAR files intact. Launch Minecraft using the matching Fabric profile or modpack instance.

If the game reports an incompatibility, check that the Minecraft, Fabric Loader, Fabric API, AUTISM Client, and addon versions are compatible with one another.

## Build from source

Requirements: JDK 25 and a compatible AUTISM Client/API version available from the local Maven repository configured by this project.

From the addon directory, run:

```powershell
./gradlew.bat build
```

On macOS or Linux:

```sh
./gradlew build
```

The compiled JAR is created in `build/libs/`.
