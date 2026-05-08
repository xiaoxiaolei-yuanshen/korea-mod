package com.asia.korea;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LightBlock;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class northkorea {

    // 效果作用范围（格）
    private static final double EFFECT_RANGE = 15.0;
    // 检查间隔（刻）10刻 = 0.5秒
    private static final int CHECK_INTERVAL = 10;
    // 跳跃高度（一格高度）
    private static final double JUMP_HEIGHT = 0.42;
    // 音乐播放冷却时间（刻）- 使用最长音乐的时长
    private static final int MUSIC_COOLDOWN = 258 * 20; // 258秒 * 20刻/秒
    // 文本触发间隔（刻）10秒 = 200刻
    private static final int TEXT_TRIGGER_INTERVAL = 200;
    // 敌对生物死亡检查间隔（刻）20刻 = 1秒
    private static final int MONSTER_KILL_INTERVAL = 20;

    // 随机数生成器 - 使用 Minecraft 的 RandomSource
    private static final RandomSource RANDOM = RandomSource.create();
    // 上次播放音乐的时间
    private static long lastMusicPlayTime = 0;
    // 上次触发文本的时间
    private static long lastTextTriggerTime = 0;
    // 上次杀死敌对生物的时间
    private static long lastMonsterKillTime = 0;
    // 记录效果上次的状态
    private static boolean lastEffectState = true;
    // 记录跟随上次的状态
    private static boolean lastFollowState = true;
    // 记录上次是否穿着全套盔甲
    private static boolean lastHadFullArmor = false;
    // 记录上次触发随机文本的时间，用于延迟触发跟随文本
    private static long lastRandomTextTime = 0;
    // 记录是否需要触发跟随文本
    private static boolean shouldTriggerFollowText = false;
    // 记录上次触发随机文本的玩家和生物
    private static Player lastRandomTextPlayer = null;
    private static LivingEntity lastRandomTextMob = null;
    // 记录当前播放的音乐信息
    private static String currentMusicName = "";
    private static long musicEndTime = 0;
    // 记录敌对生物死亡后触发文本
    private static long lastMonsterDeathTime = 0;
    private static boolean shouldTriggerDeathFollowText = false;
    private static Player lastMonsterDeathPlayer = null;
    private static BlockPos lastMonsterDeathPos = null;

    // 存储每个玩家的光源方块位置
    private static final Map<UUID, BlockPos> playerLightBlocks = new HashMap<>();

    // 自定义音乐资源位置
    private static final ResourceLocation NIRUOSANDONG = new ResourceLocation("korea", "niruosandong");
    private static final ResourceLocation NIRUOSANDONG_DJ = new ResourceLocation("korea", "niruosandongdj");
    private static final ResourceLocation NIRUOSANDONG_DJSU = new ResourceLocation("korea", "niruosandongdjsu");

    // 音乐时长（毫秒）
    private static final int NIRUOSANDONG_DURATION = 258000; // 4分18秒 = 258秒 = 258000毫秒
    private static final int NIRUOSANDONG_DJ_DURATION = 192000; // 3分12秒 = 192秒 = 192000毫秒
    private static final int NIRUOSANDONG_DJSU_DURATION = 226000; // 3分46秒 = 226秒 = 226000毫秒

    // 随机文本列表（移除了指定文本）
    private static final List<String> RANDOM_TEXTS = Arrays.asList(
            "飞机一定要能飞",
            "太阳一定得有光",
            "太阳系一定要有太阳",
            "二楼一定要盖在一楼上面",
            "你滴盐，我滴醋！朝鲜人民民主主义共和国，碗碎！",
            "房子一定要能住人",
            "船一定要能下水",
            "碗碎！",
            "泡面一定要有面",
            "钢铁雄心四一定是二战的",
            "P社一定是蠢驴",
            "电脑一定要用电",
            "休息日一定要休息",
            "Minecraft一定要有方块",
            "地铁一定要有门",
            "上课一定要有学生",
            "马一定要能骑",
            "你若丹东来~换我一城雪白~想吃广东菜~",
            "太阳一定是耀眼的",
            "盖子一定得盖紧",
            "水稻一定要种在水里",
            "飞机一定要有发动机",
            "五把星使一定能合成出蝴蝶刀",
            "工地一定要有工人",
            "司机一定要会开车",
            "工资一定要按时发",
            "CS2一定是G胖做的",
            "赈灾款一定要用来赈灾",
            "此模组于凌晨3点制作！",
            "太空里面一定要有星星！",
            "最相思的一集",
            "你的模组获得了朝鲜创意模组奖，请在三个工作日内来到平壤领奖，来的机票报销",
            "百度网盘一定是锁70KB的",
            "学校一定要空调全覆盖✋😭🤚",
            "粮食是用来吃的",
            "地铁一定要能动",
            "鞋子是穿在脚上的",
            "海里运输要用船，不能用火车",
            "文化工作者一定要有文化",
            "工程师一定要学过土木",
            "设计师一定要会设计",
            "医生一定要有医术",
            "老师一定要会教书",
            "见到[玩家名字]将军一定要哭",
            "汽车一定要能动",
            "水库一定是拿来装水的",
            "法律一定要遵守",
            "儿童一定要学习",
            "专家一定要专业",
            "见到[玩家名字]将军一定要跳起来",
            "男娘化一定要抵制",
            "网络一定要管制",
            "笔是拿来写字的",
            "饭堂一定要有饭",
            "广东菜一定要能吃",
            "工厂一定要能生产",
            "谁鼓掌了我不知道，谁没鼓掌我一清二楚"
    );

    // 跟随文本列表（随机文本后触发）
    private static final List<String> FOLLOW_TEXTS = Arrays.asList(
            "\\o/\\o/\\o/\\o/\\o/\\o/\\o/",
            "✍🏻✍🏻✍🏻✍🏻✍🏻✍🏻✍🏻✍🏻✍🏻",
            "[玩家名字]的恩情还不完！！！",
            "报告[玩家名字]我跳不动了",
            "报告将军，我哭不出来怎么办？",
            "英勇的！[玩家名字]同志！万岁！！！",
            "🤚🏼😭🤚🏼🤚🏼😭🤚🏼🤚🏼😭🤚🏼🤚🏼😭🤚🏼",
            "谁扔的闪光弹！"
    );

    // 敌对生物死亡后触发的文本列表
    private static final List<String> DEATH_FOLLOW_TEXTS = Arrays.asList(
            "👏👏👏👏👏👏👏👏",
            "\\o/\\o/\\o/\\o/\\o/\\o/\\o/\\o/",
            "✋😭🤚✋😭🤚✋😭🤚",
            "英勇的！[玩家名字]同志！万岁！！！"
    );

    // 生物死亡文本列表
    private static final List<String> DEATH_TEXTS = Arrays.asList(
            "我看你是相思了",
            "你红豆吃多了",
            "我奖励你吃紫菜蛋花汤没有菜和花"
    );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只在服务端执行，每10刻检查一次
        if (event.phase == TickEvent.Phase.END ||
                event.player.level().isClientSide ||
                event.player.tickCount % CHECK_INTERVAL != 0) {
            return;
        }

        Player player = event.player;

        // 检查是否需要触发跟随文本（在随机文本触发后2秒）
        if (shouldTriggerFollowText &&
                player.level().getGameTime() - lastRandomTextTime >= 40) { // 40刻 = 2秒
            triggerFollowText(lastRandomTextPlayer, lastRandomTextMob);
            shouldTriggerFollowText = false;
            lastRandomTextPlayer = null;
            lastRandomTextMob = null;
        }

        // 检查是否需要触发死亡跟随文本（在敌对生物死亡后2秒）
        if (shouldTriggerDeathFollowText &&
                player.level().getGameTime() - lastMonsterDeathTime >= 40) { // 40刻 = 2秒
            triggerDeathFollowText(lastMonsterDeathPlayer, lastMonsterDeathPos);
            shouldTriggerDeathFollowText = false;
            lastMonsterDeathPlayer = null;
            lastMonsterDeathPos = null;
        }

        // 检查是否穿着全套下界合金盔甲并且效果已启用
        boolean hasFullArmor = isWearingFullNetheriteArmor(player);
        if (hasFullArmor && korea.effectEnabled) {
            applyJumpEffectToNearbyMobs(player);
            killMonstersNearLight(player);
            updatePlayerLightBlock(player);

            // 如果之前没有全套盔甲但现在有了，重置音乐状态
            if (!lastHadFullArmor) {
                // 重置音乐冷却时间，允许重新播放音乐
                lastMusicPlayTime = 0;
            }
        } else {
            // 如果效果禁用或没有全套盔甲，移除光源方块
            removePlayerLightBlock(player);

            // 如果没有全套盔甲但音乐还在播放，停止音乐
            if (!hasFullArmor && korea.currentPlayingMusic != 0) {
                stopCurrentMusic();
            }
        }

        // 更新上次盔甲状态
        lastHadFullArmor = hasFullArmor;
    }

    // 客户端tick事件，用于检测按键和音乐结束
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();

            // 检查音乐是否应该结束
            if (korea.currentPlayingMusic != 0 && musicEndTime > 0 &&
                    System.currentTimeMillis() >= musicEndTime) {
                handleMusicEnd();
            }

            // 检查V键是否被按下 - 切换效果
            while (KeyBindings.TOGGLE_EFFECT_KEY.consumeClick()) {
                // 切换效果状态
                korea.effectEnabled = !korea.effectEnabled;

                // 如果效果从禁用变为启用，重置冷却时间
                if (korea.effectEnabled && !lastEffectState) {
                    lastMusicPlayTime = 0; // 重置冷却时间
                }

                // 如果禁用效果，停止当前音乐
                if (!korea.effectEnabled && korea.currentPlayingMusic != 0) {
                    stopCurrentMusic();
                }

                // 更新上次状态
                lastEffectState = korea.effectEnabled;

                // 在聊天栏显示状态消息
                if (minecraft.player != null) {
                    String status = korea.effectEnabled ? "启用" : "禁用";
                    minecraft.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("恩情效果: " + status),
                            true
                    );
                }
            }

            // 检查B键是否被按下 - 切换跟随
            while (KeyBindings.TOGGLE_FOLLOW_KEY.consumeClick()) {
                // 切换跟随状态
                korea.followEnabled = !korea.followEnabled;

                // 更新上次状态
                lastFollowState = korea.followEnabled;

                // 在聊天栏显示状态消息
                if (minecraft.player != null) {
                    String status = korea.followEnabled ? "启用" : "禁用";
                    minecraft.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("中立生物跟随: " + status),
                            true
                    );
                }
            }

            // 检查是否需要停止音乐（当效果禁用但音乐还在播放时）
            if (!korea.effectEnabled && korea.currentPlayingMusic != 0) {
                stopCurrentMusic();
            }
        }
    }

    private static boolean isWearingFullNetheriteArmor(Player player) {
        // 检查所有4个盔甲槽位是否都是下界合金盔甲
        for (ItemStack armorItem : player.getArmorSlots()) {
            if (armorItem.isEmpty()) {
                return false;
            }

            boolean isNetheriteArmor = armorItem.getItem() == Items.NETHERITE_HELMET ||
                    armorItem.getItem() == Items.NETHERITE_CHESTPLATE ||
                    armorItem.getItem() == Items.NETHERITE_LEGGINGS ||
                    armorItem.getItem() == Items.NETHERITE_BOOTS;

            if (!isNetheriteArmor) {
                return false;
            }
        }
        return true;
    }

    private static void applyJumpEffectToNearbyMobs(Player player) {
        // 获取玩家周围的所有生物
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(EFFECT_RANGE)
        );

        boolean hasJumpingMobs = false;
        List<LivingEntity> jumpingMobs = new ArrayList<>();

        for (LivingEntity entity : nearbyEntities) {
            // 排除玩家自己、敌对生物和已经受到跳跃效果的生物
            if (entity == player ||
                    entity.hasEffect(MobEffects.JUMP) ||
                    !entity.isAlive() ||
                    !entity.onGround() ||
                    entity instanceof Monster) { // 排除敌对生物
                continue;
            }

            // 让生物跳跃（一格高度）
            entity.setDeltaMovement(
                    entity.getDeltaMovement().x,
                    JUMP_HEIGHT, // 一格高度的跳跃
                    entity.getDeltaMovement().z
            );

            // 标记有生物跳跃
            hasJumpingMobs = true;
            jumpingMobs.add(entity);

            // 如果是中立生物且跟随功能启用，让它们朝玩家走来
            if (isNeutralMob(entity) && korea.followEnabled) {
                makeMobMoveToPlayer(entity, player);
            }

            // 添加粒子效果
            player.level().addParticle(
                    ParticleTypes.CRIT,
                    entity.getX(),
                    entity.getY() + 0.5,
                    entity.getZ(),
                    0, 0.1, 0
            );
        }

        // 如果有生物跳跃且没有在播放音乐，则播放音乐
        // 检查冷却时间是否已过或者效果刚刚启用
        boolean canPlayMusic = player.level().getGameTime() - lastMusicPlayTime > MUSIC_COOLDOWN || lastMusicPlayTime == 0;
        if (hasJumpingMobs && korea.effectEnabled && canPlayMusic) {
            playRandomMusic(player);
        }

        // 如果有生物跳跃且效果启用，检查是否触发随机文本
        if (hasJumpingMobs && korea.effectEnabled &&
                player.level().getGameTime() - lastTextTriggerTime > TEXT_TRIGGER_INTERVAL) {
            // 随机选择一个跳跃的生物
            if (!jumpingMobs.isEmpty()) {
                LivingEntity selectedMob = jumpingMobs.get(RANDOM.nextInt(jumpingMobs.size()));
                triggerRandomText(player, selectedMob);
            }
        }
    }

    // 杀死光源附近的敌对生物
    private static void killMonstersNearLight(Player player) {
        // 每1秒检查一次
        if (player.level().getGameTime() - lastMonsterKillTime < MONSTER_KILL_INTERVAL) {
            return;
        }

        // 获取玩家周围的所有生物
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(EFFECT_RANGE)
        );

        boolean hasKilledMonster = false;

        for (LivingEntity entity : nearbyEntities) {
            // 只处理敌对生物，排除末影龙和凋零
            if (!(entity instanceof Monster) ||
                    !entity.isAlive() ||
                    entity instanceof EnderDragon || // 排除末影龙
                    entity instanceof WitherBoss) {  // 排除凋零
                continue;
            }

            // 检查生物是否在光源附近（光照等级大于8）
            int lightLevel = entity.level().getMaxLocalRawBrightness(entity.blockPosition());
            if (lightLevel > 8) {
                // 获取生物名称
                String entityName = entity.getDisplayName().getString();

                // 杀死敌对生物
                entity.kill();
                hasKilledMonster = true;

                // 添加爆炸粒子效果
                player.level().addParticle(
                        ParticleTypes.EXPLOSION,
                        entity.getX(),
                        entity.getY() + 0.5,
                        entity.getZ(),
                        0, 0, 0
                );

                // 播放物品燃烧音效
                player.level().playSound(
                        null,
                        entity.blockPosition(),
                        SoundEvents.FIRE_EXTINGUISH, // 使用物品燃烧音效
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                );

                // 随机选择死亡文本类型
                int textType = RANDOM.nextInt(4); // 0-3 四种类型
                String deathMessage;

                switch (textType) {
                    case 0:
                        // 类型1: [玩家名字]: [生物名字]我看你是相思了
                        deathMessage = player.getScoreboardName() + ": " + entityName + "我看你是相思了";
                        break;
                    case 1:
                        // 类型2: [玩家名字]: [生物名字]你红豆吃多了
                        deathMessage = player.getScoreboardName() + ": " + entityName + "你红豆吃多了";
                        break;
                    case 2:
                        // 类型3: [玩家名字]: [生物名字]我奖励你吃紫菜蛋花汤没有菜和花
                        deathMessage = player.getScoreboardName() + ": " + entityName + "我奖励你吃紫菜蛋花汤没有菜和花";
                        break;
                    case 3:
                        // 类型4: [生物名字]：将军的恩情还不完（前面不加玩家名字）
                        deathMessage = entityName + "：将军的恩情还不完";
                        break;
                    default:
                        deathMessage = player.getScoreboardName() + ": " + entityName + "我看你是相思了";
                }

                // 显示死亡文本
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(deathMessage),
                        false
                );
            }
        }

        // 如果有杀死敌对生物，设置死亡跟随文本触发
        if (hasKilledMonster) {
            shouldTriggerDeathFollowText = true;
            lastMonsterDeathTime = player.level().getGameTime();
            lastMonsterDeathPlayer = player;
            lastMonsterDeathPos = player.blockPosition();
        }

        // 更新上次杀死敌对生物的时间
        lastMonsterKillTime = player.level().getGameTime();
    }

    // 更新玩家的光源方块
    private static void updatePlayerLightBlock(Player player) {
        BlockPos currentPos = player.blockPosition();
        UUID playerId = player.getUUID();

        // 检查玩家是否移动了
        if (playerLightBlocks.containsKey(playerId)) {
            BlockPos oldPos = playerLightBlocks.get(playerId);

            // 如果玩家移动了，移除旧的光源方块
            if (!oldPos.equals(currentPos)) {
                removeLightBlock(player.level(), oldPos);
            } else {
                // 玩家没有移动，不需要更新
                return;
            }
        }

        // 放置新的光源方块
        placeLightBlock(player.level(), currentPos);
        playerLightBlocks.put(playerId, currentPos);
    }

    // 放置光源方块
    private static void placeLightBlock(net.minecraft.world.level.Level level, BlockPos pos) {
        // 使用光源方块（LIGHT）并设置光照等级为15
        BlockState lightBlock = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15);

        // 只在空气方块的位置放置光源，不替换其他方块
        if (level.isEmptyBlock(pos)) {
            level.setBlock(pos, lightBlock, 3); // 3 = 更新标志
        }
    }

    // 移除光源方块
    private static void removeLightBlock(net.minecraft.world.level.Level level, BlockPos pos) {
        // 如果是我们放置的光源方块（LIGHT），则移除它
        if (level.getBlockState(pos).getBlock() == Blocks.LIGHT) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3); // 3 = 更新标志
        }
    }

    // 移除玩家的光源方块
    private static void removePlayerLightBlock(Player player) {
        UUID playerId = player.getUUID();

        if (playerLightBlocks.containsKey(playerId)) {
            BlockPos lightPos = playerLightBlocks.get(playerId);
            removeLightBlock(player.level(), lightPos);
            playerLightBlocks.remove(playerId);
        }
    }

    // 触发随机文本（新版本，带生物参数）
    private static void triggerRandomText(Player player, LivingEntity jumpingMob) {
        // 随机选择一条文本
        String randomText = RANDOM_TEXTS.get(RANDOM.nextInt(RANDOM_TEXTS.size()));

        // 替换文本中的占位符
        randomText = randomText.replace("[玩家名字]", player.getScoreboardName());

        // 在聊天栏显示随机文本 - 格式为 [玩家名字]: [文本]
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(player.getScoreboardName() + ": " + randomText),
                false // 不显示在操作栏，显示在聊天栏
        );

        // 更新上次触发文本的时间
        lastTextTriggerTime = player.level().getGameTime();

        // 设置延迟触发跟随文本
        shouldTriggerFollowText = true;
        lastRandomTextTime = player.level().getGameTime();
        lastRandomTextPlayer = player;
        lastRandomTextMob = jumpingMob;

        // 添加一些粒子效果增强体验
        for (int i = 0; i < 5; i++) {
            player.level().addParticle(
                    ParticleTypes.NOTE,
                    player.getX() + (RANDOM.nextDouble() - 0.5) * 2,
                    player.getY() + 1.0,
                    player.getZ() + (RANDOM.nextDouble() - 0.5) * 2,
                    (RANDOM.nextDouble() - 0.5) * 0.1,
                    0.1,
                    (RANDOM.nextDouble() - 0.5) * 0.1
            );
        }
    }

    // 触发跟随文本（随机文本后触发）
    private static void triggerFollowText(Player player, LivingEntity followingMob) {
        // 随机选择一条跟随文本
        String followText = FOLLOW_TEXTS.get(RANDOM.nextInt(FOLLOW_TEXTS.size()));

        // 获取生物名称
        String mobName = followingMob.getDisplayName().getString();

        // 替换文本中的占位符
        followText = followText.replace("[玩家名字]", player.getScoreboardName());

        // 在聊天栏显示跟随文本 - 格式为 [生物名字]: [文本]
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(mobName + ": " + followText),
                false // 不显示在操作栏，显示在聊天栏
        );

        // 添加一些粒子效果增强体验
        for (int i = 0; i < 3; i++) {
            player.level().addParticle(
                    ParticleTypes.HEART,
                    followingMob.getX() + (RANDOM.nextDouble() - 0.5),
                    followingMob.getY() + 1.0,
                    followingMob.getZ() + (RANDOM.nextDouble() - 0.5),
                    0, 0.1, 0
            );
        }
    }

    // 触发死亡跟随文本（敌对生物死亡后触发）
    private static void triggerDeathFollowText(Player player, BlockPos deathPos) {
        // 获取死亡位置附近的中立生物
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                new net.minecraft.world.phys.AABB(deathPos).inflate(EFFECT_RANGE)
        );

        List<LivingEntity> neutralMobs = new ArrayList<>();

        for (LivingEntity entity : nearbyEntities) {
            // 只选择中立生物
            if (entity instanceof Monster ||
                    !entity.isAlive() ||
                    entity == player) {
                continue;
            }

            if (isNeutralMob(entity)) {
                neutralMobs.add(entity);
            }
        }

        // 如果没有中立生物，则不触发
        if (neutralMobs.isEmpty()) {
            return;
        }

        // 随机选择一个中立生物
        LivingEntity selectedMob = neutralMobs.get(RANDOM.nextInt(neutralMobs.size()));

        // 随机选择一条死亡跟随文本
        String deathFollowText = DEATH_FOLLOW_TEXTS.get(RANDOM.nextInt(DEATH_FOLLOW_TEXTS.size()));

        // 获取生物名称
        String mobName = selectedMob.getDisplayName().getString();

        // 替换文本中的占位符
        deathFollowText = deathFollowText.replace("[玩家名字]", player.getScoreboardName());

        // 在聊天栏显示死亡跟随文本 - 格式为 [生物名字]: [文本]
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(mobName + ": " + deathFollowText),
                false // 不显示在操作栏，显示在聊天栏
        );

        // 添加一些粒子效果增强体验
        for (int i = 0; i < 3; i++) {
            player.level().addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    selectedMob.getX() + (RANDOM.nextDouble() - 0.5),
                    selectedMob.getY() + 1.0,
                    selectedMob.getZ() + (RANDOM.nextDouble() - 0.5),
                    0, 0.1, 0
            );
        }
    }

    // 判断是否是中立生物
    private static boolean isNeutralMob(LivingEntity entity) {
        // 动物类（猪、牛、羊等）
        if (entity instanceof Animal) {
            return true;
        }
        // 村民
        if (entity instanceof Villager) {
            return true;
        }
        // 其他中立生物（可以继续添加）
        // 注意：这里排除了敌对生物（Monster）
        return !(entity instanceof Monster);
    }

    // 让生物朝玩家移动
    private static void makeMobMoveToPlayer(LivingEntity mob, Player player) {
        // 计算朝向玩家的方向
        double dx = player.getX() - mob.getX();
        double dz = player.getZ() - mob.getZ();

        // 计算距离
        double distance = Math.sqrt(dx * dx + dz * dz);

        // 如果距离太近，不需要移动
        if (distance < 2.0) {
            return;
        }

        // 归一化方向向量
        dx /= distance;
        dz /= distance;

        // 设置生物朝向玩家
        float targetYRot = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;
        mob.setYRot(targetYRot);
        mob.setYHeadRot(targetYRot);

        // 设置生物朝玩家移动
        double speed = 0.15; // 移动速度
        mob.setDeltaMovement(
                mob.getDeltaMovement().x + dx * speed,
                mob.getDeltaMovement().y,
                mob.getDeltaMovement().z + dz * speed
        );

        // 添加朝向玩家的粒子效果
        mob.level().addParticle(
                ParticleTypes.HEART,
                mob.getX(),
                mob.getY() + 1.0,
                mob.getZ(),
                0, 0.1, 0
        );
    }

    // 播放随机音乐 - 使用唱片机/音符盒声音栏
    @OnlyIn(Dist.CLIENT)
    private static void playRandomMusic(Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        SoundManager soundManager = minecraft.getSoundManager();

        // 如果已经有音乐在播放，先停止
        if (korea.currentPlayingMusic != 0) {
            stopCurrentMusic();
        }

        // 随机选择一首音乐
        int musicChoice = RANDOM.nextInt(3) + 1; // 1, 2 或 3
        korea.currentPlayingMusic = musicChoice;

        ResourceLocation musicResource;
        String musicName;
        int musicDuration;

        switch (musicChoice) {
            case 1:
                musicResource = NIRUOSANDONG;
                musicName = "阿悠悠 - 你若三冬"; // 汉化音乐名称
                musicDuration = NIRUOSANDONG_DURATION;
                break;
            case 2:
                musicResource = NIRUOSANDONG_DJ;
                musicName = "阿悠悠 - 你若三冬 (将军进行曲)(DJ沈乐版)"; // 汉化音乐名称
                musicDuration = NIRUOSANDONG_DJ_DURATION;
                break;
            case 3:
                musicResource = NIRUOSANDONG_DJSU;
                musicName = "阿悠悠 - 你若三冬 (将军进行曲)(0.8xDJ沈乐版)"; // 汉化音乐名称
                musicDuration = NIRUOSANDONG_DJSU_DURATION;
                break;
            default:
                return;
        }

        // 创建并播放音乐实例 - 使用唱片机/音符盒声音栏，音量调小
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(musicResource);
        SimpleSoundInstance soundInstance = new SimpleSoundInstance(
                soundEvent.getLocation(),
                SoundSource.RECORDS, // 使用唱片机/音符盒声音栏
                0.3F, // 音量调小
                1.0F,
                RANDOM, // 使用 RandomSource 而不是 Random
                false,
                0,
                SimpleSoundInstance.Attenuation.NONE,
                player.getX(),
                player.getY(),
                player.getZ(),
                false
        );

        soundManager.play(soundInstance);
        lastMusicPlayTime = player.level().getGameTime();

        // 记录音乐结束时间
        musicEndTime = System.currentTimeMillis() + musicDuration;
        currentMusicName = musicName;

        // 在聊天栏显示播放的音乐 - 使用汉化名称
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("播放音乐: " + musicName + " (" + (musicDuration / 60000) + "分" + ((musicDuration % 60000) / 1000) + "秒)"),
                true
        );
    }

    // 处理音乐结束
    @OnlyIn(Dist.CLIENT)
    private static void handleMusicEnd() {
        Minecraft minecraft = Minecraft.getInstance();

        korea.currentPlayingMusic = 0;
        musicEndTime = 0;

        // 在聊天栏显示音乐结束 - 使用汉化名称
        if (minecraft.player != null && !currentMusicName.isEmpty()) {
            minecraft.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("音乐结束: " + currentMusicName),
                    true
            );
            currentMusicName = "";
        }
    }

    // 停止当前播放的音乐
    @OnlyIn(Dist.CLIENT)
    private static void stopCurrentMusic() {
        Minecraft minecraft = Minecraft.getInstance();
        SoundManager soundManager = minecraft.getSoundManager();

        // 停止唱片机/音符盒声音栏的音乐
        soundManager.stop(null, SoundSource.RECORDS);

        korea.currentPlayingMusic = 0;
        musicEndTime = 0;

        // 在聊天栏显示音乐已停止
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("音乐已停止"),
                    true
            );
        }
    }
}