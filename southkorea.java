package com.asia.korea;

import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;

import java.util.List;
import java.util.Random;
import java.util.Arrays;

@Mod.EventBusSubscriber
public class southkorea {

    // 效果作用范围（格）
    private static final double EFFECT_RANGE = 20.0;
    // 检查间隔（刻）20刻 = 1秒
    private static final int CHECK_INTERVAL = 20;
    // 音乐时长（毫秒）3分28秒 = 208秒 = 208000毫秒
    private static final int MUSIC_DURATION = 208000;
    // 文本触发间隔（刻）10秒 = 200刻
    private static final int TEXT_TRIGGER_INTERVAL = 200;

    // 随机数生成器
    private static final Random RANDOM = new Random();
    // 音乐播放状态
    private static MusicState musicState = MusicState.STOPPED;
    // 当前播放音乐的卫道士ID
    private static int currentPlayingVindicator = -1;
    // 音乐实例，用于停止特定音乐
    private static SimpleSoundInstance currentMusicInstance = null;
    // 音乐播放位置（毫秒）
    private static long musicPlayPosition = 0;
    // 音乐开始播放的时间
    private static long musicStartTime = 0;
    // 音乐自然结束计时器
    private static java.util.Timer musicEndTimer = null;
    // 记录忠诚效果上次的状态
    private static boolean lastLoyaltyState = true;
    // 上次触发文本的时间
    private static long lastTextTriggerTime = 0;

    // 自定义音乐资源位置
    private static final ResourceLocation GZPN = new ResourceLocation("korea", "gzpn");
    // 音乐汉化名称
    private static final String MUSIC_NAME = "光州无限制格斗大赛";

    // 随机文本列表
    private static final List<String> RANDOM_TEXTS = Arrays.asList(
            "[生物名字]:不行，我们空输部队的颜面何在！",
            "[生物名字]:这已经不是一般的村民了，必须重拳出击！",
            "[村民名字]:三棍打散村庄魂，长官我是刌民人",
            "地图加载中……..",
            "[生物名字]:执法有温度，甩棍有力度，脚下有准度。",
            "[生物名字]:忠橙！",
            "1111 5！1111 5！",
            "[生物名字]:犯错就要认，挨打要立正"
    );

    // 音乐状态枚举
    private enum MusicState {
        STOPPED, PLAYING, PAUSED
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只在服务端执行，每20刻检查一次
        if (event.phase == TickEvent.Phase.END ||
                event.player.level().isClientSide ||
                event.player.tickCount % CHECK_INTERVAL != 0) {
            return;
        }

        Player player = event.player;

        // 检查玩家附近的卫道士、村民和铁傀儡（无论忠诚效果是否启用）
        checkVindicatorAndTargets(player);

        // 检查当前播放音乐的卫道士是否还活着
        if (korea.southKoreaEnabled) {
            checkVindicatorAlive(player);
        }
    }

    // 客户端tick事件，用于检测按键
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();

            // 检查C键是否被按下 - 切换忠诚效果
            while (KeyBindings.TOGGLE_SOUTH_KOREA_KEY.consumeClick()) {
                // 切换忠诚效果状态
                korea.southKoreaEnabled = !korea.southKoreaEnabled;

                // 如果忠诚效果禁用，停止音乐
                if (!korea.southKoreaEnabled && musicState != MusicState.STOPPED) {
                    stopGzpnMusic();
                }

                // 更新上次状态
                lastLoyaltyState = korea.southKoreaEnabled;

                // 在聊天栏显示状态消息
                if (minecraft.player != null) {
                    String status = korea.southKoreaEnabled ? "启用" : "禁用";
                    minecraft.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("忠诚效果: " + status),
                            true
                    );
                }
            }
        }
    }

    private static void checkVindicatorAndTargets(Player player) {
        // 获取玩家周围的所有卫道士
        List<Vindicator> nearbyVindicators = player.level().getEntitiesOfClass(
                Vindicator.class,
                player.getBoundingBox().inflate(EFFECT_RANGE)
        );

        // 获取玩家周围的所有村民
        List<Villager> nearbyVillagers = player.level().getEntitiesOfClass(
                Villager.class,
                player.getBoundingBox().inflate(EFFECT_RANGE)
        );

        // 获取玩家周围的所有铁傀儡
        List<IronGolem> nearbyIronGolems = player.level().getEntitiesOfClass(
                IronGolem.class,
                player.getBoundingBox().inflate(EFFECT_RANGE)
        );

        // 如果没有村民和铁傀儡，直接返回
        if (nearbyVillagers.isEmpty() && nearbyIronGolems.isEmpty()) {
            // 如果没有目标，暂停音乐
            if (musicState == MusicState.PLAYING && korea.southKoreaEnabled) {
                pauseGzpnMusic(player);
            }
            return;
        }

        boolean foundChasingVindicator = false;

        for (Vindicator vindicator : nearbyVindicators) {
            // 检查卫道士是否拿着铁斧
            if (isHoldingIronAxe(vindicator) && vindicator.isAlive()) {

                // 找到最近的目标（优先村民，然后是铁傀儡）
                LivingEntity nearestTarget = findNearestTarget(vindicator, nearbyVillagers, nearbyIronGolems);

                if (nearestTarget != null && nearestTarget.isAlive()) {
                    // 让卫道士追着目标（只有在忠诚效果启用时）
                    if (korea.southKoreaEnabled) {
                        makeVindicatorChaseTarget(vindicator, nearestTarget);
                    }

                    foundChasingVindicator = true;

                    // 检查是否触发随机文本
                    if (player.level().getGameTime() - lastTextTriggerTime > TEXT_TRIGGER_INTERVAL) {
                        triggerRandomText(player, vindicator, nearestTarget);
                    }

                    // 音乐播放逻辑（只有在忠诚效果启用时）
                    if (korea.southKoreaEnabled) {
                        // 如果音乐是暂停状态，继续播放
                        if (musicState == MusicState.PAUSED) {
                            resumeGzpnMusic(player, vindicator);
                        }
                        // 如果音乐是停止状态，开始播放
                        else if (musicState == MusicState.STOPPED) {
                            playGzpnMusic(player, vindicator);
                        }
                        // 如果音乐正在播放，但卫道士不同，更新当前卫道士
                        else if (musicState == MusicState.PLAYING && currentPlayingVindicator != vindicator.getId()) {
                            currentPlayingVindicator = vindicator.getId();
                        }
                    }
                }
            }
        }

        // 如果没有找到追逐目标的卫道士，暂停音乐
        if (!foundChasingVindicator && musicState == MusicState.PLAYING && korea.southKoreaEnabled) {
            pauseGzpnMusic(player);
        }
    }

    // 找到最近的目标（优先村民，然后是铁傀儡）
    private static LivingEntity findNearestTarget(Vindicator vindicator, List<Villager> villagers, List<IronGolem> ironGolems) {
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        // 先检查村民
        for (Villager villager : villagers) {
            if (villager.isAlive()) {
                double distance = vindicator.distanceToSqr(villager);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = villager;
                }
            }
        }

        // 如果没有村民，检查铁傀儡
        if (nearest == null) {
            for (IronGolem ironGolem : ironGolems) {
                if (ironGolem.isAlive()) {
                    double distance = vindicator.distanceToSqr(ironGolem);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = ironGolem;
                    }
                }
            }
        }

        return nearest;
    }

    // 触发随机文本
    private static void triggerRandomText(Player player, Vindicator vindicator, LivingEntity target) {
        // 随机选择一条文本
        String randomText = RANDOM_TEXTS.get(RANDOM.nextInt(RANDOM_TEXTS.size()));

        // 替换文本中的占位符
        String formattedText = randomText
                .replace("[生物名字]", vindicator.getDisplayName().getString())
                .replace("[村民名字]", target.getDisplayName().getString());

        // 在聊天栏显示随机文本
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(formattedText),
                false // 不显示在操作栏，显示在聊天栏
        );

        // 更新上次触发文本的时间
        lastTextTriggerTime = player.level().getGameTime();

        // 添加一些粒子效果增强体验
        for (int i = 0; i < 5; i++) {
            player.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.NOTE,
                    player.getX() + (RANDOM.nextDouble() - 0.5) * 2,
                    player.getY() + 1.0,
                    player.getZ() + (RANDOM.nextDouble() - 0.5) * 2,
                    (RANDOM.nextDouble() - 0.5) * 0.1,
                    0.1,
                    (RANDOM.nextDouble() - 0.5) * 0.1
            );
        }
    }

    // 检查当前播放音乐的卫道士是否还活着
    private static void checkVindicatorAlive(Player player) {
        if (currentPlayingVindicator != -1 && musicState == MusicState.PLAYING) {
            // 获取当前播放音乐的卫道士
            Vindicator vindicator = (Vindicator) player.level().getEntity(currentPlayingVindicator);

            // 如果卫道士不存在或已经死亡，暂停音乐并触发死亡文本
            if (vindicator == null || !vindicator.isAlive()) {
                pauseGzpnMusic(player);

                // 触发死亡文本（无论忠诚效果是否启用）
                String deathText = "不行！我们空输部队的颜面何在？";
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(deathText),
                        false
                );

                // 在聊天栏显示音乐暂停原因
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("卫道士已死亡，音乐暂停"),
                        true
                );
            }
        }
    }

    // 检查卫道士是否拿着铁斧
    private static boolean isHoldingIronAxe(Vindicator vindicator) {
        ItemStack mainHandItem = vindicator.getMainHandItem();
        return mainHandItem.getItem() == Items.IRON_AXE;
    }

    // 让卫道士追着目标
    private static void makeVindicatorChaseTarget(Vindicator vindicator, LivingEntity target) {
        // 设置移动目标
        vindicator.getNavigation().moveTo(target, 1.0);

        // 让卫道士看着目标
        vindicator.lookAt(target, 30.0F, 30.0F);

        // 添加追逐粒子效果
        if (vindicator.tickCount % 10 == 0) {
            vindicator.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER,
                    vindicator.getX(),
                    vindicator.getY() + 1.0,
                    vindicator.getZ(),
                    0, 0.1, 0
            );
        }

        // 添加目标害怕粒子效果
        if (target.tickCount % 15 == 0) {
            target.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SMOKE,
                    target.getX(),
                    target.getY() + 1.0,
                    target.getZ(),
                    0, 0.1, 0
            );
        }
    }

    // 播放GZPN音乐
    @OnlyIn(Dist.CLIENT)
    private static void playGzpnMusic(Player player, Vindicator vindicator) {
        Minecraft minecraft = Minecraft.getInstance();
        SoundManager soundManager = minecraft.getSoundManager();

        // 创建并播放音乐实例
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(GZPN);
        currentMusicInstance = new SimpleSoundInstance(
                soundEvent.getLocation(),
                SoundSource.HOSTILE, // 使用敌对生物声音栏
                0.5F, // 音量
                1.0F,
                net.minecraft.util.RandomSource.create(),
                false,
                0,
                SimpleSoundInstance.Attenuation.LINEAR,
                vindicator.getX(),
                vindicator.getY(),
                vindicator.getZ(),
                false
        );

        soundManager.play(currentMusicInstance);
        musicState = MusicState.PLAYING;
        currentPlayingVindicator = vindicator.getId();
        musicStartTime = System.currentTimeMillis();
        musicPlayPosition = 0;

        // 取消之前的定时器（如果有）
        if (musicEndTimer != null) {
            musicEndTimer.cancel();
        }

        // 设置一个定时器，在音乐自然结束后重置状态
        musicEndTimer = new java.util.Timer();
        musicEndTimer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // 只有在音乐仍然在播放时才重置状态
                        if (musicState == MusicState.PLAYING) {
                            musicState = MusicState.STOPPED;
                            currentPlayingVindicator = -1;
                            currentMusicInstance = null;
                            musicPlayPosition = 0;

                            // 在聊天栏显示音乐结束
                            if (minecraft.player != null) {
                                minecraft.player.displayClientMessage(
                                        net.minecraft.network.chat.Component.literal("音乐结束: " + MUSIC_NAME),
                                        true
                                );
                            }
                        }
                    }
                },
                MUSIC_DURATION - musicPlayPosition // 剩余播放时间
        );

        // 在聊天栏显示播放的音乐
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("卫道士开始追逐目标，播放音乐: " + MUSIC_NAME + " (" + (MUSIC_DURATION / 60000) + "分" + ((MUSIC_DURATION % 60000) / 1000) + "秒)"),
                true
        );
    }

    // 暂停GZPN音乐
    @OnlyIn(Dist.CLIENT)
    private static void pauseGzpnMusic(Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        SoundManager soundManager = minecraft.getSoundManager();

        // 停止音乐
        if (currentMusicInstance != null) {
            soundManager.stop(currentMusicInstance);

            // 计算音乐播放位置
            if (musicState == MusicState.PLAYING) {
                musicPlayPosition = System.currentTimeMillis() - musicStartTime;
            }
        }

        // 取消定时器
        if (musicEndTimer != null) {
            musicEndTimer.cancel();
            musicEndTimer = null;
        }

        musicState = MusicState.PAUSED;

        // 在聊天栏显示音乐暂停
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("音乐暂停"),
                true
        );
    }

    // 继续播放GZPN音乐
    @OnlyIn(Dist.CLIENT)
    private static void resumeGzpnMusic(Player player, Vindicator vindicator) {
        Minecraft minecraft = Minecraft.getInstance();
        SoundManager soundManager = minecraft.getSoundManager();

        // 创建并播放音乐实例（从上次位置继续）
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(GZPN);
        currentMusicInstance = new SimpleSoundInstance(
                soundEvent.getLocation(),
                SoundSource.HOSTILE, // 使用敌对生物声音栏
                0.5F, // 音量
                1.0F,
                net.minecraft.util.RandomSource.create(),
                false,
                0,
                SimpleSoundInstance.Attenuation.LINEAR,
                vindicator.getX(),
                vindicator.getY(),
                vindicator.getZ(),
                false
        );

        soundManager.play(currentMusicInstance);
        musicState = MusicState.PLAYING;
        currentPlayingVindicator = vindicator.getId();
        musicStartTime = System.currentTimeMillis() - musicPlayPosition;

        // 取消之前的定时器（如果有）
        if (musicEndTimer != null) {
            musicEndTimer.cancel();
        }

        // 设置一个新的定时器，在剩余时间后结束音乐
        musicEndTimer = new java.util.Timer();
        musicEndTimer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // 只有在音乐仍然在播放时才重置状态
                        if (musicState == MusicState.PLAYING) {
                            musicState = MusicState.STOPPED;
                            currentPlayingVindicator = -1;
                            currentMusicInstance = null;
                            musicPlayPosition = 0;

                            // 在聊天栏显示音乐结束
                            if (minecraft.player != null) {
                                minecraft.player.displayClientMessage(
                                        net.minecraft.network.chat.Component.literal("音乐结束: " + MUSIC_NAME),
                                        true
                                );
                            }
                        }
                    }
                },
                MUSIC_DURATION - musicPlayPosition // 剩余播放时间
        );

        // 在聊天栏显示继续播放音乐
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("卫道士继续追逐目标，音乐继续播放: " + MUSIC_NAME),
                true
        );
    }

    // 停止GZPN音乐
    @OnlyIn(Dist.CLIENT)
    private static void stopGzpnMusic() {
        Minecraft minecraft = Minecraft.getInstance();
        SoundManager soundManager = minecraft.getSoundManager();

        // 停止特定的音乐实例
        if (currentMusicInstance != null) {
            soundManager.stop(currentMusicInstance);
        } else {
            // 如果没有特定的音乐实例，停止所有敌对生物声音栏的音乐
            soundManager.stop(null, SoundSource.HOSTILE);
        }

        // 取消定时器
        if (musicEndTimer != null) {
            musicEndTimer.cancel();
            musicEndTimer = null;
        }

        musicState = MusicState.STOPPED;
        currentPlayingVindicator = -1;
        currentMusicInstance = null;
        musicPlayPosition = 0;
    }
}