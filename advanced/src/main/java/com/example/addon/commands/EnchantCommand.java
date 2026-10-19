package com.example.addon.commands;

import autismclient.commands.AutismCommandSource;
import autismclient.commands.Command;
import autismclient.util.AutismClientMessaging;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

/** Creative-mode local enchantment editor, following Meteor's .enchant command. */
public final class EnchantCommand extends Command {
    private static final SimpleCommandExceptionType NOT_CREATIVE = new SimpleCommandExceptionType(
        Component.literal("You must be in creative mode to use this."));
    private static final SimpleCommandExceptionType NO_ITEM = new SimpleCommandExceptionType(
        Component.literal("Hold an item in your main hand or offhand."));

    public EnchantCommand() {
        super("enchant", "Enchant the held item in creative mode, like Meteor's .enchant.");
    }

    @Override
    public void build(LiteralArgumentBuilder<AutismCommandSource> root) {
        root.then(LiteralArgumentBuilder.<AutismCommandSource>literal("one")
            .then(RequiredArgumentBuilder.<AutismCommandSource, String>argument("enchantment", com.mojang.brigadier.arguments.StringArgumentType.word())
                .then(LiteralArgumentBuilder.<AutismCommandSource>literal("level")
                    .then(RequiredArgumentBuilder.<AutismCommandSource, Integer>argument("level", IntegerArgumentType.integer())
                        .executes(ctx -> applyOne(ctx, enchantment -> IntegerArgumentType.getInteger(ctx, "level")))))
                .then(LiteralArgumentBuilder.<AutismCommandSource>literal("max")
                    .executes(ctx -> applyOne(ctx, Enchantment::getMaxLevel)))));

        addBulkBranch(root, "all_possible", true);
        addBulkBranch(root, "all", false);
        root.then(LiteralArgumentBuilder.<AutismCommandSource>literal("clear").executes(ctx -> {
            ItemStack stack = getHeldItem(ctx);
            EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.removeIf(enchantment -> true));
            return SUCCESS;
        }));
        root.then(LiteralArgumentBuilder.<AutismCommandSource>literal("remove")
            .then(RequiredArgumentBuilder.<AutismCommandSource, String>argument("enchantment", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(ctx -> {
                    ItemStack stack = getHeldItem(ctx);
                    Holder<Enchantment> enchantment = getEnchantment(ctx, "enchantment");
                    EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.removeIf(entry -> entry.equals(enchantment)));
                    return SUCCESS;
                })));

        root.then(LiteralArgumentBuilder.<AutismCommandSource>literal("opti").executes(ctx -> applyOptimal(ctx)));
    }

    private void addBulkBranch(LiteralArgumentBuilder<AutismCommandSource> root, String name, boolean onlyPossible) {
        root.then(LiteralArgumentBuilder.<AutismCommandSource>literal(name)
            .then(LiteralArgumentBuilder.<AutismCommandSource>literal("level")
                .then(RequiredArgumentBuilder.<AutismCommandSource, Integer>argument("level", IntegerArgumentType.integer())
                    .executes(ctx -> applyAll(ctx, onlyPossible,
                        enchantment -> IntegerArgumentType.getInteger(ctx, "level")))))
            .then(LiteralArgumentBuilder.<AutismCommandSource>literal("max")
                .executes(ctx -> applyAll(ctx, onlyPossible, Enchantment::getMaxLevel))));
    }

    private int applyOne(CommandContext<AutismCommandSource> context, ToIntFunction<Enchantment> level)
        throws CommandSyntaxException {
        ItemStack stack = getHeldItem(context);
        Holder<Enchantment> enchantment = getEnchantment(context, "enchantment");
        int targetLevel = level.applyAsInt(enchantment.value());
        if (targetLevel <= 0) throw new SimpleCommandExceptionType(Component.literal("Enchantment level must be positive.")).create();
        EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(enchantment, targetLevel));
        return SUCCESS;
    }

    private int applyAll(CommandContext<AutismCommandSource> context, boolean onlyPossible,
                         ToIntFunction<Enchantment> level) throws CommandSyntaxException {
        ItemStack stack = getHeldItem(context);
        var registry = context.getSource().mc().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        List<Holder<Enchantment>> enchantments = new ArrayList<>();
        registry.listElements().forEach(enchantments::add);
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            for (Holder<Enchantment> enchantment : enchantments) {
                if (!onlyPossible || enchantment.value().canEnchant(stack)) {
                    int targetLevel = level.applyAsInt(enchantment.value());
                    if (targetLevel > 0) mutable.set(enchantment, targetLevel);
                }
            }
        });
        return SUCCESS;
    }

    private int applyOptimal(CommandContext<AutismCommandSource> context) throws CommandSyntaxException {
        ItemStack stack = getHeldItem(context);
        var registry = context.getSource().mc().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        for (Holder<Enchantment> enchantment : optimalSet(stack)) {
            EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(enchantment, enchantment.value().getMaxLevel()));
        }
        return SUCCESS;
    }

    private List<Holder<Enchantment>> optimalSet(ItemStack stack) {
        var registry = AutismCommandSource.INSTANCE.mc().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        List<String> ids = new ArrayList<>();
        if (itemId.endsWith("_sword")) ids = List.of("sharpness", "looting", "sweeping_edge", "fire_aspect", "unbreaking", "mending");
        else if (itemId.endsWith("_pickaxe") || itemId.endsWith("_shovel") || itemId.endsWith("_hoe")) ids = List.of("efficiency", "fortune", "unbreaking", "mending");
        else if (itemId.endsWith("_axe")) ids = List.of("efficiency", "sharpness", "fortune", "unbreaking", "mending");
        else if (itemId.equals("bow")) ids = List.of("power", "punch", "flame", "infinity", "unbreaking");
        else if (itemId.equals("crossbow")) ids = List.of("quick_charge", "multishot", "unbreaking", "mending");
        else if (itemId.equals("trident")) ids = List.of("impaling", "loyalty", "channeling", "unbreaking", "mending");
        else if (itemId.equals("mace")) ids = List.of("density", "wind_burst", "fire_aspect", "unbreaking", "mending");
        else if (itemId.contains("spear")) ids = List.of("lunge", "impaling", "unbreaking", "mending");
        else if (itemId.endsWith("_helmet")) ids = List.of("protection", "respiration", "aqua_affinity", "unbreaking", "mending");
        else if (itemId.endsWith("_chestplate")) ids = List.of("protection", "thorns", "unbreaking", "mending");
        else if (itemId.endsWith("_leggings")) ids = List.of("protection", "swift_sneak", "unbreaking", "mending");
        else if (itemId.endsWith("_boots")) ids = List.of("protection", "feather_falling", "depth_strider", "soul_speed", "unbreaking", "mending");
        else if (itemId.equals("elytra")) ids = List.of("unbreaking", "mending");
        List<Holder<Enchantment>> result = new ArrayList<>();
        for (String id : ids) {
            registry.get(net.minecraft.resources.Identifier.withDefaultNamespace(id))
                .ifPresent(result::add);
        }
        if (result.isEmpty()) throw new IllegalArgumentException("No optimized enchantment set for " + itemId);
        return result;
    }

    private ItemStack getHeldItem(CommandContext<AutismCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().player();
        if (player == null || context.getSource().mc().level == null) throw NO_ITEM.create();
        if (!player.getAbilities().instabuild) throw NOT_CREATIVE.create();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) stack = player.getOffhandItem();
        if (stack.isEmpty()) throw NO_ITEM.create();
        return stack;
    }

    private Holder<Enchantment> getEnchantment(CommandContext<AutismCommandSource> context, String name)
        throws CommandSyntaxException {
        String id = com.mojang.brigadier.arguments.StringArgumentType.getString(context, name);
        var registry = context.getSource().mc().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var key = net.minecraft.resources.Identifier.tryParse(id.contains(":") ? id : "minecraft:" + id);
        if (key == null) throw new SimpleCommandExceptionType(Component.literal("Invalid enchantment id.")).create();
        return registry.get(key).orElseThrow(() -> new SimpleCommandExceptionType(
            Component.literal("Unknown enchantment: " + id)).create());
    }
}
