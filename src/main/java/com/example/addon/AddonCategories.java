package com.example.addon;

import autismclient.modules.ModuleCategory;

/** Categories used to separate Damoko modules from imported Meteor modules. */
public final class AddonCategories {
    public static final ModuleCategory DAMOKO = ModuleCategory.registerAddon("damoko-addon", "Damoko Add On");
    public static final ModuleCategory TROUSER_STREAK = ModuleCategory.registerAddon("damoko-addon:trouser-streak", "Trouser Streak");
    public static final ModuleCategory METEOR = ModuleCategory.register("Meteor");

    private AddonCategories() {}
}
