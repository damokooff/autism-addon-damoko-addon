package com.example.addon;

import autismclient.api.AutismAddon;
import autismclient.api.AutismAddons;
import autismclient.api.ApiVersion;
import com.example.addon.modules.AutoPilotModule;
import com.example.addon.modules.AutoEatModule;
import com.example.addon.modules.AutoWalkModule;
import com.example.addon.modules.AutoMendModule;
import com.example.addon.modules.MaceKillModule;
import com.example.addon.modules.MaceAuraModule;
import com.example.addon.modules.AutoGapModule;
import com.example.addon.modules.AutoExpModule;
import com.example.addon.modules.AutoLogModule;
import com.example.addon.modules.AutoRespawnModule;
import com.example.addon.modules.AutoWeaponModule;
import com.example.addon.modules.AutoBreedModule;
import com.example.addon.modules.AutoShearerModule;
import com.example.addon.modules.AutoNametagModule;
import com.example.addon.modules.SpearAuraModule;
import com.example.addon.modules.DangerModule;
import com.example.addon.modules.CustomCapeModule;
import com.example.addon.modules.SpearKillModule;
import com.example.addon.modules.PlayerActivityModule;
import com.example.addon.modules.FlightAntikickModule;
import com.example.addon.hud.ElytraCountHud;
import com.example.addon.modules.PlayerAlarmModule;
import com.example.addon.modules.PotEspModule;
import com.example.addon.modules.SpawnerScannerModule;
import com.example.addon.modules.PortalFinderModule;
import com.example.addon.modules.EnderChestScannerModule;
import com.example.addon.modules.DiamondOreScannerModule;
import com.example.addon.modules.AncientDebrisScannerModule;
import com.example.addon.modules.PortalFrameScannerModule;
import com.example.addon.modules.AdvancedItemEspModule;
import com.example.addon.modules.MobGearEspModule;
import com.example.addon.modules.CollectibleFrameEspModule;
import com.example.addon.modules.BaseFinderModule;
import com.example.addon.hud.ExampleHud;
import com.example.addon.commands.EnchantCommand;

// The addon's modules are grouped under "Damoko Add On", the name in fabric.mod.json.
public final class ExampleAddon extends AutismAddon {
    public static final String ID = "damoko-addon";

    @Override
    public int apiVersion() {
        return ApiVersion.CURRENT;
    }

    @Override
    public void onInitialize() {
        AutismAddons.modules().register(new AutoPilotModule());
        AutismAddons.modules().register(new MaceKillModule());
        AutismAddons.modules().register(new MaceAuraModule());
        AutismAddons.modules().register(new AutoEatModule());
        AutismAddons.modules().register(new AutoMendModule());
        AutismAddons.modules().register(new AutoGapModule());
        AutismAddons.modules().register(new AutoExpModule());
        AutismAddons.modules().register(new AutoLogModule());
        AutismAddons.modules().register(new AutoRespawnModule());
        AutismAddons.modules().register(new AutoWeaponModule());
        AutismAddons.modules().register(new AutoBreedModule());
        AutismAddons.modules().register(new AutoShearerModule());
        AutismAddons.modules().register(new AutoNametagModule());
        AutismAddons.modules().register(new SpearAuraModule());
        AutismAddons.modules().register(new DangerModule());
        AutismAddons.modules().register(new AutoWalkModule());
        AutismAddons.modules().register(new CustomCapeModule());
        AutismAddons.modules().register(new SpearKillModule());
        AutismAddons.modules().register(new PlayerActivityModule());
        AutismAddons.modules().register(new FlightAntikickModule());
        AutismAddons.modules().register(new PlayerAlarmModule());
        AutismAddons.modules().register(new PotEspModule());
        AutismAddons.modules().register(new SpawnerScannerModule());
        AutismAddons.modules().register(new PortalFinderModule());
        AutismAddons.modules().register(new EnderChestScannerModule());
        AutismAddons.modules().register(new DiamondOreScannerModule());
        AutismAddons.modules().register(new AncientDebrisScannerModule());
        AutismAddons.modules().register(new PortalFrameScannerModule());
        AutismAddons.modules().register(new AdvancedItemEspModule());
        AutismAddons.modules().register(new MobGearEspModule());
        AutismAddons.modules().register(new CollectibleFrameEspModule());
        AutismAddons.modules().register(new BaseFinderModule());
        AutismAddons.hud().register(new ElytraCountHud());
        AutismAddons.hud().register(new ExampleHud());
        AutismAddons.commands().register(new EnchantCommand());
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}
