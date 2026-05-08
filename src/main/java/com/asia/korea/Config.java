package com.asia.korea;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = KOR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    // 恩情模式开关状态
    private static final ForgeConfigSpec.BooleanValue GRATITUDE_MODE_ENABLED = BUILDER
            .comment("Whether gratitude mode is enabled")
            .define("gratitudeModeEnabled", true);

    // 文案触发开关状态
    private static final ForgeConfigSpec.BooleanValue TEXT_TRIGGER_ENABLED = BUILDER
            .comment("Whether text trigger is enabled")
            .define("textTriggerEnabled", true);

    // 生物朝向玩家跳跃开关状态
    private static final ForgeConfigSpec.BooleanValue MOB_JUMP_TO_PLAYER_ENABLED = BUILDER
            .comment("Whether mob jump to player is enabled")
            .define("mobJumpToPlayerEnabled", true);

    // 敌对生物死亡开关状态
    private static final ForgeConfigSpec.BooleanValue HOSTILE_MOB_DEATH_ENABLED = BUILDER
            .comment("Whether hostile mob death is enabled")
            .define("hostileMobDeathEnabled", true);

    // 音乐播放开关状态
    private static final ForgeConfigSpec.BooleanValue MUSIC_PLAY_ENABLED = BUILDER
            .comment("Whether music play is enabled")
            .define("musicPlayEnabled", true);
    
    // 村庄英雄效果开关状态
    private static final ForgeConfigSpec.BooleanValue VILLAGE_HERO_ENABLED = BUILDER
            .comment("Whether village hero effect is enabled")
            .define("villageHeroEnabled", true);
    
    // 发光效果开关状态
    private static final ForgeConfigSpec.BooleanValue GLOWING_EFFECT_ENABLED = BUILDER
            .comment("Whether glowing effect is enabled")
            .define("glowingEffectEnabled", true);
    
    // 忠诚模式开关状态
    private static final ForgeConfigSpec.BooleanValue LOYALTY_MODE_ENABLED = BUILDER
            .comment("Whether loyalty mode is enabled")
            .define("loyaltyModeEnabled", true);
    
    // 酸菜块掉落配置
    private static final ForgeConfigSpec.DoubleValue SOUR_CABBAGE_BLOCK_DROP_FOUR_CHANCE = BUILDER
            .comment("Chance (0.0-1.0) for dropping 4 sour cabbages when breaking sour cabbage block with bare hands")
            .defineInRange("sourCabbageBlockDropFourChance", 0.15, 0.0, 1.0);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;
    
    // 按键状态配置
    public static boolean gratitudeModeEnabled;
    public static boolean textTriggerEnabled;
    public static boolean mobJumpToPlayerEnabled;
    public static boolean hostileMobDeathEnabled;
    public static boolean musicPlayEnabled;
    public static boolean villageHeroEnabled;
    public static boolean glowingEffectEnabled;
    public static boolean loyaltyModeEnabled;
    
    // 酸菜块掉落配置
    public static double sourCabbageBlockDropFourChance;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                .collect(Collectors.toSet());
                
        // 加载按键状态配置
        gratitudeModeEnabled = GRATITUDE_MODE_ENABLED.get();
        textTriggerEnabled = TEXT_TRIGGER_ENABLED.get();
        mobJumpToPlayerEnabled = MOB_JUMP_TO_PLAYER_ENABLED.get();
        hostileMobDeathEnabled = HOSTILE_MOB_DEATH_ENABLED.get();
        musicPlayEnabled = MUSIC_PLAY_ENABLED.get();
        villageHeroEnabled = VILLAGE_HERO_ENABLED.get();
        glowingEffectEnabled = GLOWING_EFFECT_ENABLED.get();
        loyaltyModeEnabled = LOYALTY_MODE_ENABLED.get();
        
        // 加载酸菜块掉落配置
        sourCabbageBlockDropFourChance = SOUR_CABBAGE_BLOCK_DROP_FOUR_CHANCE.get();
    }
    
    // 更新恩情模式开关状态并保存配置
    public static void setGratitudeModeEnabled(boolean value) {
        gratitudeModeEnabled = value;
        GRATITUDE_MODE_ENABLED.set(value);
        SPEC.save();
    }
    
    // 更新文案触发开关状态并保存配置
    public static void setTextTriggerEnabled(boolean value) {
        textTriggerEnabled = value;
        TEXT_TRIGGER_ENABLED.set(value);
        SPEC.save();
    }
    
    // 更新生物朝向玩家跳跃开关状态并保存配置
    public static void setMobJumpToPlayerEnabled(boolean value) {
        mobJumpToPlayerEnabled = value;
        MOB_JUMP_TO_PLAYER_ENABLED.set(value);
        SPEC.save();
    }
    
    // 更新敌对生物死亡开关状态并保存配置
    public static void setHostileMobDeathEnabled(boolean value) {
        hostileMobDeathEnabled = value;
        HOSTILE_MOB_DEATH_ENABLED.set(value);
        SPEC.save();
    }
    
    // 更新音乐播放开关状态并保存配置
    public static void setMusicPlayEnabled(boolean value) {
        musicPlayEnabled = value;
        MUSIC_PLAY_ENABLED.set(value);
        SPEC.save();
    }
    
    // 更新村庄英雄效果开关状态并保存配置
    public static void setVillageHeroEnabled(boolean value) {
        villageHeroEnabled = value;
        VILLAGE_HERO_ENABLED.set(value);
        SPEC.save();
    }
    
    // 更新发光效果开关状态并保存配置
    public static void setGlowingEffectEnabled(boolean value) {
        glowingEffectEnabled = value;
        GLOWING_EFFECT_ENABLED.set(value);
        SPEC.save();
    }
    
    // 更新忠诚模式开关状态并保存配置
    public static void setLoyaltyModeEnabled(boolean value) {
        loyaltyModeEnabled = value;
        LOYALTY_MODE_ENABLED.set(value);
        SPEC.save();
    }
}
