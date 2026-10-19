package com.example.addon.modules;

import autismclient.api.module.EnumSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import com.example.addon.ExampleAddon;

/** Selects one of the cape textures bundled from the Meteor Capes archive. */
public final class CustomCapeModule extends Module {
    private static CustomCapeModule instance;
    public enum Cape {
        AETHERIC("Aetheric", "aetheric"), AVO("AVO", "avo"), ANIME1("Anime 1", "anime1"),
        ANIME2("Anime 2", "anime2"), ANIME3("Anime 3", "anime3"), ANIME4("Anime 4", "anime4"),
        CLOWN("Clown", "clow"), DEV("Developer", "dev"), FEATHER("Feather", "feather"),
        VAPEV("VapeV", "vapev"), HACKER("Hacker", "hacker"), ANARCHY("Anarchy", "anarchy"),
        MINECON_2011("Minecon 2011", "2011"), MINECON_2012("Minecon 2012", "2012"),
        MINECON_2013("Minecon 2013", "2013"), MINECON_2015("Minecon 2015", "2015"),
        MINECON_2016("Minecon 2016", "2016"),
        FOREST_DUSK("Forest Dusk", "forest-dusk"), HD_GALAXY("HD Galaxy", "hd-galaxy"),
        PASTEL_AESTHETIC("Pastel Aesthetic", "pastel-aesthetic"), CAPES_ORI("Capes Ori", "cpaes-ori"),
        BLUE_MIGRATION_V2("Blue Migration V2", "blue-migration-v2"), NEUROSIS("Neurosis Cape", "neurosis-cape"),
        MIGRATION_16X("Migration Cape 16x", "migration-cape-16x"), OMEGA("Omega", "omega"),
        HALLOWEEN("Halloween", "halloween"), AURORA("Aurora Cape", "aurora-cape"),
        TWISTED("Twisted", "twisted"), HERO("Hero", "hero"), BUILDER("Builder", "builder"),
        CRAFTER("Crafter", "crafter"), MOONLIGHT_TRAIL("Moonlight Trail", "moonlight-trail"),
        ZOMBIE_HORSE("Zombie Horse", "zombie-horse"), COPPER("Copper", "copper"),
        COMMON("Common", "common"), YEARN("Yearn", "yearn"), HOME("Home", "home"),
        MENACE("Menace", "menace"), MOJANG_OFFICE("Mojang Office", "mojang-office"),
        MINECRAFT_EXPERIENCE("Minecraft Experience", "minecraft-experience"), MCC_15TH("MCC 15th Year", "mcc-15th-year"),
        FOLLOWER("Follower's", "follower-s"), ANNIVERSARY_15TH("15th Anniversary", "15th-anniversary"),
        PURPLE_HEART("Purple Heart", "purple-heart"), CHERRY_BLOSSOM("Cherry Blossom", "cherry-blossom"),
        VANILLA("Vanilla", "vanilla"), VALENTINE("Valentine", "valentine"),
        MIGRATOR("Migrator", "migrator-1"), MOJANG_STUDIOS("Mojang Studios", "mojang-studios"),
        BIRTHDAY("Birthday", "birthday"), PAN("Pan", "pan"), FOUNDERS("Founder's", "founder-s"),
        MOJANG("Mojang", "mojang"), COBALT("Cobalt", "cobalt"), TURTLE("Turtle", "turtle"),
        CHRISTMAS("Christmas", "christmas");

        private final String label;
        private final String texture;
        Cape(String label, String texture) { this.label = label; this.texture = texture; }
        public String texture() { return texture; }
        @Override public String toString() { return label; }
    }

    private final EnumSetting<Cape> cape = add(new EnumSetting<>("cape", "Cape", Cape.AETHERIC, Cape.values()));

    public CustomCapeModule() {
        super("damoko-addon:custom-cape", "Custom Cape", AddonCategories.DAMOKO,
            "Select a custom cape. The selected cape is visible to you and players using this addon.");
        instance = this;
    }

    public static CustomCapeModule instance() { return instance; }
    public Cape cape() { return cape.get(); }
}
