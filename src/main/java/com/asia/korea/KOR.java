package com.asia.korea;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import com.google.common.collect.ImmutableSet;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.asia.korea.effect.SpicyEffect;
import com.asia.korea.enchantment.GunMuEnchantment;
import com.asia.korea.block.KimchiJarBlock;
import com.asia.korea.blockentity.BlockEntities;
import com.asia.korea.menu.ModMenus;
import com.asia.korea.screen.KimchiJarScreen;
import net.minecraftforge.registries.DeferredRegister;
import com.asia.korea.item.SunItem;
import com.asia.korea.item.AirDropItem;
import com.asia.korea.item.PurpleEggItem;
import com.asia.korea.item.ArmyStewItem;
import com.asia.korea.item.SteelRodSword;
import com.asia.korea.item.StyleSourCabbageItem;
import com.asia.korea.item.SweatyFootWeapon;
import com.asia.korea.entity.PurpleEggEntity;
import com.asia.korea.entity.ArmyStewEntity;
import com.asia.korea.entity.NorthKoreanRefugee;
import com.asia.korea.client.renderer.PurpleEggRenderer;
import com.asia.korea.client.renderer.ArmyStewRenderer;
import com.asia.korea.client.renderer.NorthKoreanRefugeeRenderer;
import com.asia.korea.client.renderer.NortheastRainSisterRenderer;
import com.asia.korea.init.Paintings;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.lwjgl.glfw.GLFW;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KOR.MODID)
public class KOR
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "kor";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Items which will all be registered under the "kor" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    // Create a Deferred Register to hold Blocks which will all be registered under the "kor" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    
    // Create a Deferred Register to hold Entities which will all be registered under the "kor" namespace
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "kor" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    // Create a Deferred Register to hold SoundEvents which will all be registered under the "kor" namespace
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    
    // Create a Deferred Register to hold MobEffects which will all be registered under the "kor" namespace
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    
    // Create a Deferred Register to hold Enchantments which will all be registered under the "kor" namespace
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    

    
    // 注册辣效果
    public static final RegistryObject<MobEffect> SPICY_EFFECT = MOB_EFFECTS.register("spicy", SpicyEffect::new);
    
    // 注册棍母附魔
    public static final RegistryObject<Enchantment> GUN_MU = ENCHANTMENTS.register("gun_mu", GunMuEnchantment::new);
    

    
    // 注册万岁音效事件
    public static final RegistryObject<SoundEvent> WANSUI01 = SOUND_EVENTS.register("wansui01", 
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "wansui01")));
    
    public static final RegistryObject<SoundEvent> WANSUI02 = SOUND_EVENTS.register("wansui02", 
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "wansui02")));
    
    // 注册音乐事件
    public static final RegistryObject<SoundEvent> NIRUOSANDONG = SOUND_EVENTS.register("niruosandong", 
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "niruosandong")));
    
    public static final RegistryObject<SoundEvent> NIRUOSANDONG_DJ = SOUND_EVENTS.register("niruosandongdj", 
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "niruosandongdj")));
    
    public static final RegistryObject<SoundEvent> NIRUOSANDONG_DJSU = SOUND_EVENTS.register("niruosandongdjsu", 
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "niruosandongdjsu")));
    
    public static final RegistryObject<SoundEvent> NIRUOSANDONG_CHAOXIAN = SOUND_EVENTS.register("niruosandongchaoxian", 
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "niruosandongchaoxian")));
    
    // 注册钢棍音乐事件

    // 注册太阳套装装备
    public static final RegistryObject<Item> SUN_HELMET = ITEMS.register("sun_helmet", SunItem.Helmet::new);
    public static final RegistryObject<Item> SUN_CHESTPLATE = ITEMS.register("sun_chestplate", SunItem.Chestplate::new);
    public static final RegistryObject<Item> SUN_LEGGINGS = ITEMS.register("sun_leggings", SunItem.Leggings::new);
    public static final RegistryObject<Item> SUN_BOOTS = ITEMS.register("sun_boots", SunItem.Boots::new);
    
    // 注册空输套装装备
    public static final RegistryObject<Item> AIR_DROP_HELMET = ITEMS.register("air_drop_helmet", AirDropItem.Helmet::new);
    public static final RegistryObject<Item> AIR_DROP_CHESTPLATE = ITEMS.register("air_drop_chestplate", AirDropItem.Chestplate::new);
    public static final RegistryObject<Item> AIR_DROP_LEGGINGS = ITEMS.register("air_drop_leggings", AirDropItem.Leggings::new);
    public static final RegistryObject<Item> AIR_DROP_BOOTS = ITEMS.register("air_drop_boots", AirDropItem.Boots::new);
    
    // 注册紫蛋投掷物
    public static final RegistryObject<Item> PURPLE_EGG = ITEMS.register("purple_egg", () -> new PurpleEggItem(new Item.Properties()));
    
    // 注册部队火锅投掷物
    public static final RegistryObject<Item> ARMY_STEW = ITEMS.register("army_stew", () -> new ArmyStewItem(new Item.Properties()));
    
    // 注册钢棍剑
    public static final RegistryObject<Item> STEEL_ROD_SWORD = ITEMS.register("steel_rod_sword", () -> new SteelRodSword());
    
    // 注册棒子武器
    public static final RegistryObject<Item> STICK_WEAPON = ITEMS.register("stick_weapon", () -> new com.asia.korea.item.StickWeapon());
    
    // 注册辣泡菜物品
    public static final RegistryObject<Item> SPICY_KIMCHI = ITEMS.register("spicy_kimchi", () -> new com.asia.korea.item.SpicyKimchiItem());
    
    // 注册辣酱物品
    public static final RegistryObject<Item> HOT_SAUCE = ITEMS.register("hot_sauce", () -> new com.asia.korea.item.HotSauceItem());
    
    // 注册韩式炸鸡物品
    public static final RegistryObject<Item> KOREAN_FRIED_CHICKEN = ITEMS.register("korean_fried_chicken", () -> new com.asia.korea.item.KoreanFriedChickenItem());
    
    // 注册腌制鸡肉物品
    public static final RegistryObject<Item> PICKLED_CHICKEN = ITEMS.register("pickled_chicken", () -> new com.asia.korea.item.PickledChickenItem());
    
    // 注册酸菜物品
    public static final RegistryObject<Item> SOUR_CABBAGE = ITEMS.register("sour_cabbage", () -> new com.asia.korea.item.SourCabbageItem());
    
    // 注册韩式火鸡面物品
    public static final RegistryObject<Item> KOREAN_FIRE_NOODLES = ITEMS.register("korean_fire_noodles", () -> new com.asia.korea.item.KoreanFireNoodlesItem());
    
    // 注册鸡蛋面物品
    public static final RegistryObject<Item> EGG_NOODLES = ITEMS.register("egg_noodles", () -> new com.asia.korea.item.EggNoodlesItem());
    
    // 注册鸡肉蘑菇面物品
    public static final RegistryObject<Item> CHICKEN_MUSHROOM_NOODLES = ITEMS.register("chicken_mushroom_noodles", () -> new com.asia.korea.item.ChickenMushroomNoodlesItem());
    
    // 注册牛肉胡萝卜面物品
    public static final RegistryObject<Item> BEEF_CARROT_NOODLES = ITEMS.register("beef_carrot_noodles", () -> new com.asia.korea.item.BeefCarrotNoodlesItem());
    
    // 注册咸鱼海带面物品
    public static final RegistryObject<Item> SALTED_FISH_KELP_NOODLES = ITEMS.register("salted_fish_kelp_noodles", () -> new com.asia.korea.item.SaltedFishKelpNoodlesItem());
    
    // 注册太阳之光物品
    public static final RegistryObject<Item> SUN_LIGHT_BOOK = ITEMS.register("sun_light_book", () -> new com.asia.korea.item.SunLightBook());
    
    // 注册木棍木物品
    public static final RegistryObject<Item> WOOD_STICK_WOOD = ITEMS.register("wood_stick_wood", () -> new com.asia.korea.item.WoodStickWoodItem());
    
    // 注册泡菜缸方块
    public static final RegistryObject<Block> KIMCHI_JAR_BLOCK = BLOCKS.register("kimchi_jar", KimchiJarBlock::new);
    
    // 注册泡菜缸方块物品
    public static final RegistryObject<Item> KIMCHI_JAR_ITEM = ITEMS.register("kimchi_jar", 
        () -> new net.minecraft.world.item.BlockItem(KIMCHI_JAR_BLOCK.get(), new Item.Properties())
    );
    
    // 注册酸菜块方块
    public static final RegistryObject<Block> SOUR_CABBAGE_BLOCK = BLOCKS.register("sour_cabbage_block", com.asia.korea.block.SourCabbageBlock::new);
    
    // 注册酸菜块方块物品
    public static final RegistryObject<Item> SOUR_CABBAGE_BLOCK_ITEM = ITEMS.register("sour_cabbage_block", 
        () -> new net.minecraft.world.item.BlockItem(SOUR_CABBAGE_BLOCK.get(), new Item.Properties())
    );
    
    // 注册格调酸菜块物品
    public static final RegistryObject<Item> STYLE_SOUR_CABBAGE = ITEMS.register("style_sour_cabbage", 
        () -> new StyleSourCabbageItem(new Item.Properties())
    );
    
    // 注册大汗脚武器
    public static final RegistryObject<Item> SWEATY_FOOT_WEAPON = ITEMS.register("sweaty_foot_weapon", 
        () -> new SweatyFootWeapon()
    );
    

    


    
    // 注册紫蛋实体
    public static final RegistryObject<EntityType<PurpleEggEntity>> PURPLE_EGG_ENTITY = ENTITIES.register("purple_egg", 
        () -> EntityType.Builder.<PurpleEggEntity>of(PurpleEggEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("purple_egg"));
    
    // 注册部队火锅实体
    public static final RegistryObject<EntityType<ArmyStewEntity>> ARMY_STEW_ENTITY = ENTITIES.register("army_stew", 
        () -> EntityType.Builder.<ArmyStewEntity>of(ArmyStewEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("army_stew"));
    
    // 注册朝鲜难民实体
    public static final RegistryObject<EntityType<NorthKoreanRefugee>> NORTH_KOREAN_REFUGEE = ENTITIES.register("north_korean_refugee", 
        () -> EntityType.Builder.<NorthKoreanRefugee>of(NorthKoreanRefugee::new, MobCategory.CREATURE)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(10)
            .updateInterval(3)
            .build("north_korean_refugee"));


    
    // 注册朝鲜难民生成蛋
    public static final RegistryObject<Item> NORTH_KOREAN_REFUGEE_SPAWN_EGG = ITEMS.register("north_korean_refugee_spawn_egg", 
        () -> {
            // 使用Forge提供的ForgeSpawnEggItem，它能更好地处理实体注册
            return new net.minecraftforge.common.ForgeSpawnEggItem(
                NORTH_KOREAN_REFUGEE,
                0xFF4343, // 淡红色主色
                0x1E90FF, // 淡蓝色斑点
                new Item.Properties()
            );
        }
    );
    
    // 注册东北雨姐Boss实体
    public static final RegistryObject<EntityType<com.asia.korea.entity.NortheastRainSister>> NORTHEAST_RAIN_SISTER = ENTITIES.register("northeast_rain_sister", 
        () -> EntityType.Builder.<com.asia.korea.entity.NortheastRainSister>of(com.asia.korea.entity.NortheastRainSister::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(10)
            .updateInterval(3)
            .fireImmune()
            .build("northeast_rain_sister"));
    
    // 注册东北雨姐Boss生成蛋
    public static final RegistryObject<Item> NORTHEAST_RAIN_SISTER_SPAWN_EGG = ITEMS.register("northeast_rain_sister_spawn_egg", 
        () -> {
            return new net.minecraftforge.common.ForgeSpawnEggItem(
                NORTHEAST_RAIN_SISTER,
                0xFF6B6B, // 淡红色主色
                0x9370DB, // 紫色斑点
                new Item.Properties()
            );
        }
    );



    

    
    // 注册恩情创造物品栏
    public static final RegistryObject<CreativeModeTab> CUSTOM_TAB = CREATIVE_MODE_TABS.register("custom_tab", 
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(PURPLE_EGG.get()))
            .title(Component.translatable("itemGroup.kor.custom_tab"))
            .displayItems((parameters, output) -> {
                // 添加紫蛋和太阳套装到恩情物品栏
                output.accept(PURPLE_EGG.get());
                output.accept(SUN_HELMET.get());
                output.accept(SUN_CHESTPLATE.get());
                output.accept(SUN_LEGGINGS.get());
                output.accept(SUN_BOOTS.get());
                // 添加朝鲜难民生成蛋
                output.accept(NORTH_KOREAN_REFUGEE_SPAWN_EGG.get());
                // 添加太阳之光
                output.accept(SUN_LIGHT_BOOK.get());
            })
            .build());
    
    // 注册忠诚创造物品栏
    public static final RegistryObject<CreativeModeTab> LOYALTY_TAB = CREATIVE_MODE_TABS.register("loyalty_tab", 
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(SPICY_KIMCHI.get()))
            .title(Component.translatable("itemGroup.kor.loyalty_tab"))
            .displayItems((parameters, output) -> {
                // 添加空输套装、钢棍和棒子武器
                output.accept(AIR_DROP_HELMET.get());
                output.accept(AIR_DROP_CHESTPLATE.get());
                output.accept(AIR_DROP_LEGGINGS.get());
                output.accept(AIR_DROP_BOOTS.get());
                output.accept(STEEL_ROD_SWORD.get());
                output.accept(STICK_WEAPON.get());
                // 添加泡菜、辣酱、韩式炸鸡、腌制鸡肉和韩式火鸡面
                output.accept(SPICY_KIMCHI.get());
                output.accept(HOT_SAUCE.get());
                output.accept(KOREAN_FRIED_CHICKEN.get());
                output.accept(PICKLED_CHICKEN.get());
                output.accept(KOREAN_FIRE_NOODLES.get());
                // 添加部队火锅
                output.accept(ARMY_STEW.get());
                // 添加泡菜缸
                output.accept(KIMCHI_JAR_ITEM.get());
            })
            .build());

    // 太阳套装音乐播放状态（0表示未播放，1-3表示播放中的音乐编号）
    public static int currentSunArmorMusic = 0;

    // 设置界面按键绑定 (C键)
    public static KeyMapping settingsMenuKey = new KeyMapping(
        "key.kor.settings_menu", // 按键绑定的唯一标识符
        GLFW.GLFW_KEY_C, // 默认按键为C
        "category.kor.misc" // 按键分类
    );
    
    // 按键状态配置 - 使用Config类中的配置值
    public static boolean gratitudeModeEnabled() { return Config.gratitudeModeEnabled; }
    public static boolean textTriggerEnabled() { return Config.textTriggerEnabled; }
    public static boolean mobJumpToPlayerEnabled() { return Config.mobJumpToPlayerEnabled; }
    public static boolean hostileMobDeathEnabled() { return Config.hostileMobDeathEnabled; }
    public static boolean musicPlayEnabled() { return Config.musicPlayEnabled; }
    public static boolean villageHeroEnabled() { return Config.villageHeroEnabled; }
    public static boolean glowingEffectEnabled() { return Config.glowingEffectEnabled; }
    public static boolean loyaltyModeEnabled() { return Config.loyaltyModeEnabled; }



    public KOR()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        
        // 注册实体属性创建事件
        modEventBus.addListener(this::registerAttributes);
        


        // Register the Deferred Register to the MOD Event Bus so items get registered
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        Paintings.REGISTRY.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        // 注册村民职业
        com.asia.korea.villager.ModVillagerProfessions.PROFESSIONS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));

        // 配置朝鲜难民的生成规则
        event.enqueueWork(() -> {
            net.minecraft.world.entity.SpawnPlacements.register(
                NORTH_KOREAN_REFUGEE.get(),
                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, reason, pos, random) -> {
                    // 适合村庄生成的条件：地面上有方块，不是空中，有一定概率
                    return net.minecraft.world.entity.Mob.checkMobSpawnRules(entityType, level, reason, pos, random) && 
                           level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below()) &&
                           random.nextFloat() < 0.15F;
                }
            );
        });
    }

    // Add items to the combat creative tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        // 检查是否是战斗标签页
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
        {
            // 添加紫蛋到战斗标签页
            event.accept(PURPLE_EGG.get());
            event.accept(ARMY_STEW.get());
            // 添加太阳套装到战斗标签页
            event.accept(SUN_HELMET.get());
            event.accept(SUN_CHESTPLATE.get());
            event.accept(SUN_LEGGINGS.get());
            event.accept(SUN_BOOTS.get());
            // 添加空输套装到战斗标签页
            event.accept(AIR_DROP_HELMET.get());
            event.accept(AIR_DROP_CHESTPLATE.get());
            event.accept(AIR_DROP_LEGGINGS.get());
            event.accept(AIR_DROP_BOOTS.get());
            // 添加钢棍剑到战斗标签页
            event.accept(STEEL_ROD_SWORD.get());
            // 添加棒子武器到战斗标签页
            event.accept(STICK_WEAPON.get());
            // 添加太阳之光到战斗标签页
            event.accept(SUN_LIGHT_BOOK.get());
        }
        // 检查是否是工具和实用物品标签页
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            // 添加太阳之光到工具和实用物品标签页
            event.accept(SUN_LIGHT_BOOK.get());
            // 添加格调酸菜块到工具和实用物品标签页
            event.accept(STYLE_SOUR_CABBAGE.get());
        }
        // 检查是否是繁殖标签页，添加生成蛋
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
        {
            // 直接通过资源位置获取刷怪蛋
            Item spawnEgg = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "north_korean_refugee_spawn_egg"));
            if (spawnEgg != null) {
                event.accept(spawnEgg);
            } else {
                LOGGER.error("Failed to find NorthKoreanRefugee spawn egg in registry");
            }
        }
        // 检查是否是功能方块标签页，添加泡菜缸和酸菜块
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
        {
            event.accept(KIMCHI_JAR_ITEM.get());
            event.accept(SOUR_CABBAGE_BLOCK_ITEM.get());
        }
        // 检查是否是自然方块标签页，添加酸菜块
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS)
        {
            event.accept(SOUR_CABBAGE_BLOCK_ITEM.get());
        }
        // 检查是否是食物与饮品标签页，添加泡菜、辣酱、韩式炸鸡、腌制鸡肉、酸菜和韩式火鸡面
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
        {
            event.accept(SPICY_KIMCHI.get());
            event.accept(HOT_SAUCE.get());
            event.accept(KOREAN_FRIED_CHICKEN.get());
            event.accept(PICKLED_CHICKEN.get());
            event.accept(SOUR_CABBAGE.get());
            event.accept(KOREAN_FIRE_NOODLES.get());
            event.accept(EGG_NOODLES.get());
            event.accept(CHICKEN_MUSHROOM_NOODLES.get());
            event.accept(BEEF_CARROT_NOODLES.get());
            event.accept(SALTED_FISH_KELP_NOODLES.get());
        }
    }
    
    // 注册实体属性
    private void registerAttributes(final EntityAttributeCreationEvent event)
    {
        event.put(NORTH_KOREAN_REFUGEE.get(), NorthKoreanRefugee.createAttributes().build());
        event.put(NORTHEAST_RAIN_SISTER.get(), com.asia.korea.entity.NortheastRainSister.createAttributes().build());
    }
    


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            
            // 注册屏幕
            net.minecraft.client.gui.screens.MenuScreens.register(ModMenus.KIMCHI_JAR_MENU.get(), KimchiJarScreen::new);
        }
        
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
        {
            // 注册按键绑定
            event.register(settingsMenuKey);
        }
        
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
        {
            // 注册紫蛋的渲染器
            event.registerEntityRenderer(KOR.PURPLE_EGG_ENTITY.get(), PurpleEggRenderer::new);
            // 注册部队火锅的渲染器
            event.registerEntityRenderer(KOR.ARMY_STEW_ENTITY.get(), ArmyStewRenderer::new);
            // 注册朝鲜难民的渲染器
            event.registerEntityRenderer(KOR.NORTH_KOREAN_REFUGEE.get(), NorthKoreanRefugeeRenderer::new);
            // 注册东北雨姐Boss的渲染器
            event.registerEntityRenderer(KOR.NORTHEAST_RAIN_SISTER.get(), NortheastRainSisterRenderer::new);
        }
    }

    // 处理按键事件的事件监听器
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class KeyInputHandler
    {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent event)
        {
            if (event.phase != ClientTickEvent.Phase.END)
            {
                return;
            }
            
            // 检查设置界面按键是否被按下
            while (settingsMenuKey.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    minecraft.setScreen(new com.asia.korea.gui.ModSettingsScreen(minecraft.screen));
                }
            }
            

        }
        

    }
}
