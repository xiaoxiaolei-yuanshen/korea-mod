package com.asia.korea.events;

import com.asia.korea.item.SunItem;
import com.asia.korea.item.AirDropItem;
import com.asia.korea.KOR;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@Mod.EventBusSubscriber(modid = "kor")
public class SunArmorEvents {

    // 用于跟踪生物上次跳跃的时间
    private static final Map<Integer, Integer> mobJumpCooldowns = new HashMap<>();
    private static final int JUMP_COOLDOWN = 15; // 40刻 = 2秒
    
    // 随机数生成器
    private static final Random random = new Random();
    
    // 记录已经受伤的坚守者的UUID，确保每个坚守者只受伤一次
    private static final Set<UUID> hurtWardens = new HashSet<>();
    
    // 用于跟踪坚守者上次受伤的时间，实现10秒冷却
    private static final Map<UUID, Long> wardenHurtCooldowns = new HashMap<>();
    private static final long WARDEN_HURT_COOLDOWN = 200; // 200刻 = 10秒
    
    // 用于跟踪雨姐上次受伤的时间，实现15秒冷却
    private static final Map<UUID, Long> rainSisterHurtCooldowns = new HashMap<>();
    private static final long RAIN_SISTER_HURT_COOLDOWN = 300; // 300刻 = 15秒
    
    // 用于跟踪玩家上次生成光源方块的位置
    private static final Map<UUID, BlockPos> lastLightBlockPositions = new HashMap<>();
    
    // 用于跟踪上次是否有生物跳跃，避免重复触发音乐
    private static boolean hadJumpingMobLastTick = false;
    
    // 用于跟踪玩家是否刚穿上装备，避免刚穿装备时生物跳跃触发音乐
    private static final Map<UUID, Long> playerEquipTime = new HashMap<>();
    private static final long EQUIP_COOLDOWN = 100; // 100刻 = 5秒，穿装备后5秒内不播放音乐
    
    // 用于跟踪当前tick是否有生物真正跳跃
    private static boolean hasMobJumpedThisTick = false;
    
    // 用于跟踪混合套装下生物的跳跃状态
    private static final Map<Integer, Integer> mixedArmorMobJumpCooldowns = new HashMap<>();
    private static final int MIXED_ARMOR_JUMP_COOLDOWN = 20; // 20刻 = 1秒
    
    // 用于存储被没收武器的生物及其武器
    private static final Map<Integer, ItemStack> confiscatedWeapons = new HashMap<>();
    
    // 用于跟踪玩家上次穿着的装备状态
    private static final Map<UUID, String> lastPlayerArmorState = new HashMap<>();
    
    // 文案相关
    private static final List<String> TEXT_LIST = Arrays.asList(
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
            "谁鼓掌了我不知道，谁没鼓掌我一清二楚",
            "法律从业者一定要懂法",
            "激烈的斗争一定要为人民斗",
            "资本家一定要挂路灯",
            "原神一定是牛逼的",
            "钱副官一定要跟蒋介石"
    );
    private static final int TEXT_TRIGGER_INTERVAL = 200; // 200刻 = 10秒
    private static long lastTextTriggerTime = 0;
    
    // 生物文案相关（延迟触发）
    private static final List<String> MOB_TEXT_LIST = Arrays.asList(
            "\\o/\\o/\\o/\\o/\\o/\\o/\\o/",
            "✋😭🤚✋😭🤚✋😭🤚",
            "[玩家名字]的恩情还不完！！！",
            "报告[玩家名字]我跳不动了",
            "报告将军，我哭不出来怎么办？",
            "英勇的！[玩家名字]同志！万岁！！！",
            "🤚😭🤚🤚😭🤚🤚😭🤚",
            "谁扔的闪光弹！",
            "👏👏👏👏👏👏👏👏👏",
            "📖✍📖✍📖✍📖✍📖✍📖✍",
            "报告将军我腱鞘炎跳不起来"
    );
    
    // 玩家触发的新文本列表
    private static final List<String> PLAYER_TEXT_LIST = Arrays.asList(
            "<[玩家名字]>:我看你是相思了",
            "<[玩家名字]>:你红豆吃多了",
            "<[玩家名字]>:我奖励你吃紫菜蛋花汤没有菜和花");
    
    // 敌对生物死亡文本列表（完全从文案 copy.txt重新制作）
    private static final List<String> DEATH_TEXT_LIST = Arrays.asList(
            "<[生物名字]>:👏👏👏👏👏👏👏👏",
            "<[生物名字]>:\\o/\\o/\\o/\\o/\\o/\\o/\\o/\\o/",
            "<[生物名字]>:✋😭🤚✋😭🤚✋😭🤚",
            "<[生物名字]>:英勇的！[玩家名字]同志！万岁！！！");
    // 延迟触发相关状态
    // 延迟触发相关状态
    private static boolean shouldTriggerMobText = false;
    private static long lastPlayerTextTime = 0;
    private static Player lastTextPlayer = null;
    private static Mob lastJumpingMob = null;
    
    // 敌对生物死亡文本触发状态
    private static boolean shouldTriggerDeathText = false;
    private static long lastMonsterDeathTime = 0;
    private static Player lastMonsterDeathPlayer = null;
    private static BlockPos lastMonsterDeathPos = null;
    private static net.minecraft.world.entity.Mob lastMonsterDeathMob = null;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        boolean isWearingFullSunArmor = isPlayerWearingFullSunArmor(player);
        UUID playerId = player.getUUID();
        
        // 检测玩家是否刚穿上装备
        if (isWearingFullSunArmor) {
            if (!playerEquipTime.containsKey(playerId)) {
                // 玩家刚穿上装备，记录时间
                playerEquipTime.put(playerId, player.level().getGameTime());
            }
        } else {
            // 玩家脱下装备，移除记录并停止音乐
            playerEquipTime.remove(playerId);
            SunArmorMusicHandler.stopCurrentMusic();
        }
        
        // 检测玩家食物使用状态
        checkPlayerFoodConsumption(player);
        
        // 处理太阳盔甲的效果
            if (isWearingFullSunArmor) {
                // 检查是否是黑夜（Minecraft时间：0-11999是白天，12000-23999是黑夜）
                long worldTime = player.level().getDayTime() % 24000;
                if (worldTime >= 12000 && worldTime <= 23999 && com.asia.korea.KOR.villageHeroEnabled()) {
                    // 给玩家添加无限的发光效果
                    net.minecraft.world.effect.MobEffectInstance glowEffect = new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.GLOWING,
                            Integer.MAX_VALUE, // 持续时间：最大值（几乎无限）
                            0, // 等级
                            true, // 是否可见
                            false, // 是否显示粒子
                            true // 是否可被移除
                    );
                    player.addEffect(glowEffect);
                } else {
                    // 如果是白天或发光效果开关已关闭，移除发光效果
                    player.removeEffect(net.minecraft.world.effect.MobEffects.GLOWING);
                }
                
                // 添加村庄英雄效果（如果开关已开启）
                if (com.asia.korea.KOR.gratitudeModeEnabled() && com.asia.korea.KOR.villageHeroEnabled()) {
                    // 给玩家添加无限的村庄英雄效果（满级）
                    net.minecraft.world.effect.MobEffectInstance villageHeroEffect = new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE,
                            Integer.MAX_VALUE, // 持续时间：最大值（几乎无限）
                            5, // 等级：5级（满级）
                            true, // 是否可见
                            false, // 是否显示粒子
                            true // 是否可被移除
                    );
                    player.addEffect(villageHeroEffect);
                } else {
                    // 如果开关已关闭，移除村庄英雄效果
                    player.removeEffect(net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE);
                }
                
                // 处理玩家脚下的光源方块
                spawnLightBlockAtPlayerFeet(player);
            } else {
                // 如果没有穿全套盔甲，移除发光效果和光源方块
                player.removeEffect(net.minecraft.world.effect.MobEffects.GLOWING);
                player.removeEffect(net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE);
                // 移除玩家之前生成的光源方块
                removePlayerLightBlock(player);
            }

        // 检查是否需要触发生物文案（延迟2秒）
        if (shouldTriggerMobText && lastTextPlayer != null && lastJumpingMob != null && com.asia.korea.KOR.textTriggerEnabled()) {
            if (player.level().getGameTime() - lastPlayerTextTime >= 40) { // 40刻 = 2秒
                triggerMobText(lastTextPlayer, lastJumpingMob);
            }
        }
        
        // 检查是否需要触发敌对生物死亡文本（延迟0.5秒）
        if (shouldTriggerDeathText && lastMonsterDeathPlayer != null && lastMonsterDeathPos != null && com.asia.korea.KOR.textTriggerEnabled()) {
            if (player.level().getGameTime() - lastMonsterDeathTime >= 10) { // 10刻 = 0.5秒
                triggerDeathText(lastMonsterDeathPlayer, lastMonsterDeathPos);
            }
        }

        // 处理空输套装对铁傀儡的吸引效果
        boolean isWearingFullAirDropArmor = isPlayerWearingFullAirDropArmor(player);
        if (isWearingFullAirDropArmor) {
            // 找到附近的铁傀儡
            player.level().getEntitiesOfClass(IronGolem.class, player.getBoundingBox().inflate(20.0D)).forEach(ironGolem -> {
                if (ironGolem.isNoAi() || ironGolem.isDeadOrDying()) {
                    return;
                }
                // 让铁傀儡将玩家设为目标
                ironGolem.setTarget(player);
            });
        } else {
            // 玩家没有穿全套空输套装，清除铁傀儡的仇恨
            player.level().getEntitiesOfClass(IronGolem.class, player.getBoundingBox().inflate(20.0D)).forEach(ironGolem -> {
                if (ironGolem.isNoAi() || ironGolem.isDeadOrDying()) {
                    return;
                }
                // 如果铁傀儡的目标是当前玩家，清除目标
                if (ironGolem.getTarget() == player) {
                    ironGolem.setTarget(null);
                    ironGolem.setLastHurtByMob(null);
                }
            });
        }

        if (isWearingFullSunArmor && com.asia.korea.KOR.gratitudeModeEnabled()) {
            // 给猪添加发光效果
            player.level().getEntitiesOfClass(Pig.class, player.getBoundingBox().inflate(15.0D)).forEach(pig -> {
                if (com.asia.korea.KOR.villageHeroEnabled()) {
                    pig.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.GLOWING,
                            20 * 20, // 20秒
                            0,
                            false,
                            false
                    ));
                }
                
                // 猪周围的中立生物跳跃逻辑
                if (com.asia.korea.KOR.gratitudeModeEnabled() && com.asia.korea.KOR.mobJumpToPlayerEnabled()) {
                    pig.level().getEntitiesOfClass(Mob.class, pig.getBoundingBox().inflate(15.0D)).forEach(nearbyMob -> {
                        if (nearbyMob != pig && nearbyMob.getType().getCategory() == net.minecraft.world.entity.MobCategory.CREATURE) {
                            // 计算朝向猪的方向
                            double dx = pig.getX() - nearbyMob.getX();
                            double dz = pig.getZ() - nearbyMob.getZ();
                            
                            // 计算距离
                            double distance = Math.sqrt(dx * dx + dz * dz);
                            
                            // 如果距离合适，让生物朝向猪跳跃
                            if (distance > 1.0 && distance < 10.0 && nearbyMob.onGround()) {
                                // 让生物朝向猪
                                nearbyMob.getLookControl().setLookAt(pig);
                                
                                // 计算跳跃方向
                                double jumpX = dx / distance * 0.3;
                                double jumpZ = dz / distance * 0.3;
                                
                                // 应用跳跃
                                nearbyMob.setDeltaMovement(jumpX, 0.5, jumpZ);
                                nearbyMob.hasImpulse = true;
                            }
                        }
                    });
                }
            });
            // 重置当前tick的跳跃状态
            hasMobJumpedThisTick = false;
            
            // 使用数组来存储最后跳跃的生物（解决lambda变量修改限制）
            Mob[] lastJumpedMob = new Mob[1];
            
            // 先处理生物跳跃的逻辑 - 将半径改为10格
            player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(10.0D)).forEach(mob -> {
                if (mob.isNoAi() || mob.isDeadOrDying()) {
                    return;
                }

                // 检查生物是否友好
                if (isFriendlyMob(mob)) {
                    // 检查是否在冷却期内
                    int entityId = mob.getId();
                    int cooldown = mobJumpCooldowns.getOrDefault(entityId, 0);
                    
                    if (cooldown <= 0 && mob.onGround()) {
                        // 让生物朝向玩家
                        mob.getLookControl().setLookAt(player);
                        
                        double jumpX = mob.getDeltaMovement().x;
                        double jumpZ = mob.getDeltaMovement().z;
                        
                        // 如果开启了生物朝向玩家跳跃
                        if (com.asia.korea.KOR.gratitudeModeEnabled() && com.asia.korea.KOR.mobJumpToPlayerEnabled() && !(mob instanceof Pig)) {
                            // 计算朝向玩家的方向
                            double dx = player.getX() - mob.getX();
                            double dz = player.getZ() - mob.getZ();
                            
                            // 计算距离
                            double distance = Math.sqrt(dx * dx + dz * dz);
                            
                            // 如果距离太近，不需要调整方向
                            if (distance > 2.0) {
                                // 归一化方向向量
                                dx /= distance;
                                dz /= distance;
                                
                                // 设置水平方向速度 - 再次降低速度到0.05，让生物更慢地靠近玩家
                                jumpX = dx * 0.05;
                                jumpZ = dz * 0.05;
                            }
                            
                            // 确保生物始终面向玩家
                            float targetYRot = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;
                            mob.setYRot(targetYRot);
                            mob.setYHeadRot(targetYRot);
                        }
                        
                        // 修复生物跳跃：使用setDeltaMovement直接设置速度
                        mob.setDeltaMovement(
                                jumpX,
                                0.42, // 恢复原跳跃高度
                                jumpZ
                        );
                        // 设置冷却时间
                        mobJumpCooldowns.put(entityId, JUMP_COOLDOWN);
                        
                        // 记录最后跳跃的生物
                        lastJumpedMob[0] = mob;
                        
                        // 标记当前tick有生物跳跃
                        hasMobJumpedThisTick = true;
                        
                        // 当这个生物跳跃时，让其他附近的友好生物也跳起来
                        makeNearbyFriendlyMobsJump(mob, player);
                    } else {
                        // 减少冷却时间
                        mobJumpCooldowns.put(entityId, cooldown - 1);
                    }
                }
            });
            
            // 只有当生物真正跳跃时才触发文案
            if (hasMobJumpedThisTick && !hadJumpingMobLastTick) {
                // 检查是否刚穿上装备，如果是则不播放文案
                long equipTime = playerEquipTime.getOrDefault(playerId, 0L);
                long currentTime = player.level().getGameTime();
                boolean justEquipped = (currentTime - equipTime) < EQUIP_COOLDOWN;
                
                if (!justEquipped && lastJumpedMob[0] != null && com.asia.korea.KOR.textTriggerEnabled()) {
                    triggerRandomText(player, lastJumpedMob[0]);
                }
                hadJumpingMobLastTick = true;
            } else if (!hasMobJumpedThisTick) {
                // 如果没有生物跳跃，重置状态
                hadJumpingMobLastTick = false;
            }
            
            // 定时播放音乐
            if (KOR.gratitudeModeEnabled() && KOR.musicPlayEnabled()) {
                SunArmorMusicHandler.playRandomMusic(player);
            }
            
            // 处理敌对生物 - 只有在开关开启时才执行
            if (com.asia.korea.KOR.gratitudeModeEnabled() && com.asia.korea.KOR.hostileMobDeathEnabled()) {
                player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, player.getBoundingBox().inflate(10.0D)).forEach(mob -> {
                    // 检查是否为敌对生物
                    if (mob instanceof net.minecraft.world.entity.monster.Monster && mob.isAlive()) {
                        // 排除铁傀儡、末影龙、凋零、坚守者、雨姐
                        if (mob instanceof net.minecraft.world.entity.animal.IronGolem) {
                            return;
                        }
                        if (mob instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon) {
                            return;
                        }
                        if (mob instanceof net.minecraft.world.entity.boss.wither.WitherBoss) {
                            return;
                        }
                        if (mob instanceof com.asia.korea.entity.NortheastRainSister) {
                            return;
                        }
                        // 检查是否为坚守者
                        boolean isWarden = false;
                        try {
                            // 直接使用导入的Warden类
                            isWarden = mob instanceof Warden;
                        } catch (Exception e) {
                            // 如果失败，使用反射作为备选
                            try {
                                Class<?> wardenClass = Class.forName("net.minecraft.world.entity.monster.warden.Warden");
                                isWarden = wardenClass.isInstance(mob);
                            } catch (ClassNotFoundException ex) {
                                // 如果找不到Warden类，假设不是
                            }
                        }
                        
                        if (isWarden) {
                            // 检查坚守者是否在冷却期内
                            UUID wardenUUID = mob.getUUID();
                            long currentTime = player.level().getGameTime();
                            long lastHurtTime = wardenHurtCooldowns.getOrDefault(wardenUUID, 0L);
                            
                            if (currentTime - lastHurtTime >= WARDEN_HURT_COOLDOWN) {
                                // 坚守者扣300滴血
                                mob.hurt(mob.damageSources().generic(), 300.0F);
                                // 记录坚守者受伤时间
                                wardenHurtCooldowns.put(wardenUUID, currentTime);
                                // 记录坚守者受伤信息，触发死亡文本
                                shouldTriggerDeathText = true;
                                lastMonsterDeathTime = player.level().getGameTime();
                                lastMonsterDeathPlayer = player;
                                lastMonsterDeathPos = mob.blockPosition();
                                lastMonsterDeathMob = mob;
                            }
                        } else {
                            // 其他敌对生物受到足够伤害而死亡，这样会触发正常的物品掉落
                            // 使用mob.damageSources().playerAttack(player)来模拟玩家攻击，确保掉落正常物品
                            net.minecraft.world.damagesource.DamageSource damageSource = mob.damageSources().playerAttack(player);
                            mob.hurt(damageSource, mob.getMaxHealth() * 2);
                            // 记录死亡信息，触发死亡文本
                            shouldTriggerDeathText = true;
                            lastMonsterDeathTime = player.level().getGameTime();
                            lastMonsterDeathPlayer = player;
                            lastMonsterDeathPos = mob.blockPosition();
                            lastMonsterDeathMob = mob;
                        }
                    }
                });
            }

        } else {
            // 如果玩家没有穿全套太阳套装或恩情模式关闭，清空冷却时间
            mobJumpCooldowns.clear();
        }
        
        // 处理混合套装效果（一半太阳套 + 一半空输套）- 独立于太阳套和恩情模式
        if (isPlayerWearingMixedArmor(player)) {
            handleMixedArmorEffect(player);
            // 更新装备状态记录
            String currentArmorState = getPlayerArmorState(player);
            lastPlayerArmorState.put(playerId, currentArmorState);
        } else {
            // 检查装备状态是否变化
            String currentArmorState = getPlayerArmorState(player);
            String lastState = lastPlayerArmorState.getOrDefault(playerId, "");
            
            if (!currentArmorState.equals(lastState)) {
                // 装备状态变化，归还武器并放下手
                returnConfiscatedWeapons(player);
                lowerAllMobsHands(player);
                // 更新装备状态
                lastPlayerArmorState.put(playerId, currentArmorState);
            }
            
            // 如果玩家没有穿混合套装，清空混合套装的跳跃冷却
            mixedArmorMobJumpCooldowns.clear();
        }
    }

    /**
     * 检查生物是否为友好生物（除了猪和脱北者）
     */
    private static boolean isFriendlyMob(Mob mob) {
        // 排除猪和脱北者，其他友好生物可以跳跃
        if (mob instanceof Pig) {
            return false;
        }
        // 排除脱北者，它们不应该跳跃和跟随玩家
        if (mob instanceof com.asia.korea.entity.NorthKoreanRefugee) {
            return false;
        }
        return mob instanceof Animal || mob instanceof Villager;
    }

    /**
     * 检查玩家是否穿着全套太阳套装
     */
    private static boolean isPlayerWearingFullSunArmor(Player player) {
        return isSunArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)) &&
               isSunArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)) &&
               isSunArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS)) &&
               isSunArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET));
    }

    /**
     * 当一个友好生物跳跃时，让其他附近的友好生物也跳起来
     */
    private static void makeNearbyFriendlyMobsJump(Mob jumpingMob, Player player) {
        // 查找跳跃生物周围10格内的所有生物
        jumpingMob.level().getEntitiesOfClass(Mob.class, jumpingMob.getBoundingBox().inflate(10.0D)).forEach(nearbyMob -> {
            // 排除跳跃的生物本身、猪、以及非友好生物
            if (nearbyMob == jumpingMob || nearbyMob instanceof Pig || !isFriendlyMob(nearbyMob)) {
                return;
            }
            
            if (nearbyMob.isNoAi() || nearbyMob.isDeadOrDying()) {
                return;
            }
            
            // 检查是否在冷却期内
            int entityId = nearbyMob.getId();
            int cooldown = mobJumpCooldowns.getOrDefault(entityId, 0);
            
            // 允许这些附近的生物立即跳跃，不需要等待冷却
            if (cooldown <= 0 && nearbyMob.onGround()) {
                // 让附近的生物朝向玩家
                nearbyMob.getLookControl().setLookAt(player);
                
                double jumpX = nearbyMob.getDeltaMovement().x;
                double jumpZ = nearbyMob.getDeltaMovement().z;
                
                // 如果开启了生物朝向玩家跳跃
                if (com.asia.korea.KOR.gratitudeModeEnabled() && com.asia.korea.KOR.mobJumpToPlayerEnabled()) {
                    // 计算朝向玩家的方向
                    double dx = player.getX() - nearbyMob.getX();
                    double dz = player.getZ() - nearbyMob.getZ();
                    
                    // 计算距离
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    
                    // 如果距离太近，不需要调整方向
                    if (distance > 2.0) {
                        // 归一化方向向量
                        dx /= distance;
                        dz /= distance;
                        
                        // 设置水平方向速度 - 再次降低速度到0.05，让生物更慢地靠近玩家
                        jumpX = dx * 0.05;
                        jumpZ = dz * 0.05;
                    }
                    
                    // 确保生物始终面向玩家
                    float targetYRot = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;
                    nearbyMob.setYRot(targetYRot);
                    nearbyMob.setYHeadRot(targetYRot);
                }
                
                // 修复生物跳跃：使用setDeltaMovement直接设置速度
                nearbyMob.setDeltaMovement(
                        jumpX,
                        0.42, // 恢复原跳跃高度
                        jumpZ
                );
                // 设置冷却时间，避免过于频繁跳跃
                mobJumpCooldowns.put(entityId, JUMP_COOLDOWN);
            }
        });
    }
    
    /**
     * 检查物品是否为太阳套装部件
     */
    private static boolean isSunArmorPiece(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof SunItem;
    }
    
    /**
     * 触发随机文案
     */
    private static void triggerRandomText(Player player, Mob jumpingMob) {
        // 检查文案触发是否开启
        if (!com.asia.korea.KOR.gratitudeModeEnabled()) {
            return;
        }
        
        // 检查间隔时间
        if (player.level().getGameTime() - lastTextTriggerTime < TEXT_TRIGGER_INTERVAL) {
            return;
        }
        
        // 随机选择一条文案
        String randomText = TEXT_LIST.get(random.nextInt(TEXT_LIST.size()));
        
        // 替换文案中的占位符
        randomText = randomText.replace("[玩家名字]", player.getScoreboardName() + ":");
        
        // 在聊天栏显示文案 - 格式为 <[玩家名字]>: [文案内容]
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("<" + player.getScoreboardName() + ">: " + randomText),
                false // 不显示在操作栏，显示在聊天栏
        );
        
        // 更新上次触发文案的时间
        lastTextTriggerTime = player.level().getGameTime();
        
        // 设置延迟触发生物文案（2秒后）
        shouldTriggerMobText = true;
        lastPlayerTextTime = player.level().getGameTime();
        lastTextPlayer = player;
        lastJumpingMob = jumpingMob;
    }
    
    /**
     * 触发生物文案
     */
    private static void triggerMobText(Player player, Mob jumpingMob) {
        // 检查文案触发是否开启
        if (!com.asia.korea.KOR.gratitudeModeEnabled()) {
            // 重置延迟触发状态
            shouldTriggerMobText = false;
            lastTextPlayer = null;
            lastJumpingMob = null;
            return;
        }
        
        // 随机选择一条生物文案
        String mobText = MOB_TEXT_LIST.get(random.nextInt(MOB_TEXT_LIST.size()));
        
        // 替换文案中的占位符 - 玩家名字后面不加冒号
        mobText = mobText.replace("[玩家名字]", player.getScoreboardName());
        
        // 获取生物名称
        String mobName = jumpingMob.getDisplayName().getString();
        
        // 在聊天栏显示文案 - 格式为 <[生物名字]>: [文案内容]
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("<" + mobName + ">: " + mobText),
                false // 不显示在操作栏，显示在聊天栏
        );
        
        // 重置延迟触发状态
        shouldTriggerMobText = false;
        lastTextPlayer = null;
        lastJumpingMob = null;
    }
    
    /**
     * 触发敌对生物死亡文本
     */
    private static void triggerDeathText(Player player, BlockPos deathPos) {
        // 检查文案触发是否开启
        if (!com.asia.korea.KOR.gratitudeModeEnabled()) {
            // 重置延迟触发状态
            shouldTriggerDeathText = false;
            lastMonsterDeathPlayer = null;
            lastMonsterDeathPos = null;
            lastMonsterDeathMob = null;
            return;
        }
        
        // 检查是否有存储的敌对生物
        if (lastMonsterDeathMob == null) {
            return;
        }
        
        // 随机选择一条文本：50%概率从死亡文本列表，50%概率从玩家文本列表
        String deathText;
        boolean isPlayerText = random.nextBoolean();
        
        if (isPlayerText && !PLAYER_TEXT_LIST.isEmpty()) {
            // 从玩家文本列表中随机选择
            deathText = PLAYER_TEXT_LIST.get(random.nextInt(PLAYER_TEXT_LIST.size()));
        } else {
            // 从死亡文本列表中随机选择
            deathText = DEATH_TEXT_LIST.get(random.nextInt(DEATH_TEXT_LIST.size()));
        }
        
        // 获取敌对生物的显示名称
        String mobName = lastMonsterDeathMob.getDisplayName().getString();
        
        // 检查文本是否包含生物名字占位符
        // 替换占位符
        if (isPlayerText) {
            // 玩家触发的文本：将<[玩家名字]>替换为<玩家名字>
            deathText = deathText.replace("<[玩家名字]>:", "<" + player.getScoreboardName() + ">:");
        } else {
            // 检查文本是否包含生物名字占位符
            boolean hasMobName = deathText.contains("<[生物名字]>");
            // 生物触发的文本：替换为单纯的玩家名字，不添加冒号
            deathText = deathText.replace("[玩家名字]", player.getScoreboardName());
            
            // 如果文本包含生物名字占位符，直接替换
            if (hasMobName) {
                deathText = deathText.replace("<[生物名字]>:", "<" + mobName + ">:");
            } else {
                // 否则，添加生物名字前缀
                deathText = "<" + mobName + ": " + deathText;
            }
        }
        
        // 在聊天栏显示死亡文本
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(deathText),
                false // 显示在聊天栏，而不是操作栏
        );
        
        // 重置延迟触发状态
        shouldTriggerDeathText = false;
        lastMonsterDeathPlayer = null;
        lastMonsterDeathPos = null;
        lastMonsterDeathMob = null;
    }
    
    /**
     * 移除玩家之前生成的光源方块
     */
    private static void removePlayerLightBlock(Player player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }
        
        // 获取世界
        net.minecraft.world.level.Level level = player.level();
        
        // 检查是否有之前生成的光源方块位置
        if (lastLightBlockPositions.containsKey(player.getUUID())) {
            BlockPos lastPos = lastLightBlockPositions.get(player.getUUID());
            
            // 检查该位置是否是光源方块
            net.minecraft.world.level.block.state.BlockState lastState = level.getBlockState(lastPos);
            if (lastState.getBlock() instanceof net.minecraft.world.level.block.LightBlock) {
                // 将光源方块替换为空气
                level.setBlock(lastPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
            }
            
            // 从地图中移除该位置
            lastLightBlockPositions.remove(player.getUUID());
        }
    }
    
    /**
     * 处理玩家重生事件，确保重生后立即重新应用盔甲效果
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        boolean isWearingFullSunArmor = isPlayerWearingFullSunArmor(player);
        UUID playerId = player.getUUID();
        
        if (isWearingFullSunArmor) {
            // 重置装备时间，避免重生后立即触发音乐
            playerEquipTime.put(playerId, player.level().getGameTime());
            
            // 检查是否是黑夜
            long worldTime = player.level().getDayTime() % 24000;
            if (worldTime >= 12000 && worldTime <= 23999 && com.asia.korea.KOR.villageHeroEnabled()) {
                // 给玩家添加无限的发光效果
                net.minecraft.world.effect.MobEffectInstance glowEffect = new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.GLOWING,
                        Integer.MAX_VALUE,
                        0,
                        true,
                        false,
                        true
                );
                player.addEffect(glowEffect);
            }
            
            // 添加村庄英雄效果
            if (com.asia.korea.KOR.gratitudeModeEnabled() && com.asia.korea.KOR.villageHeroEnabled()) {
                net.minecraft.world.effect.MobEffectInstance villageHeroEffect = new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE,
                        Integer.MAX_VALUE,
                        5,
                        true,
                        false,
                        true
                );
                player.addEffect(villageHeroEffect);
            }
            
            // 处理玩家脚下的光源方块
            spawnLightBlockAtPlayerFeet(player);
        } else {
            // 如果没有穿装备，移除记录
            playerEquipTime.remove(playerId);
        }
    }
    
    /**
     * 处理玩家登录事件，重置音乐状态
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 重置音乐状态，确保重新进入世界时音乐可以正常播放
        SunArmorMusicHandler.resetMusicState();
        // 清理装备时间记录
        playerEquipTime.clear();
    }
    
    /**
     * 处理玩家退出事件，清理状态
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        // 清理相关状态
        SunArmorMusicHandler.resetMusicState();
        // 清理装备时间记录
        playerEquipTime.clear();
    }
    
    /**
     * 在玩家脚下生成光源方块
     */
    private static void spawnLightBlockAtPlayerFeet(Player player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }
        
        // 获取玩家当前位置上方1格的位置
        BlockPos playerPos = player.blockPosition();
        BlockPos feetPos = playerPos.above();
        
        // 获取世界
        net.minecraft.world.level.Level level = player.level();
        
        // 检查玩家是否移动了（位置变化）
        if (lastLightBlockPositions.containsKey(player.getUUID())) {
            BlockPos lastPos = lastLightBlockPositions.get(player.getUUID());
            if (!lastPos.equals(feetPos)) {
                // 玩家移动了，移除之前的光源方块
                removePlayerLightBlock(player);
            }
        }
        
        // 获取当前方块状态
        net.minecraft.world.level.block.state.BlockState currentState = level.getBlockState(feetPos);
        net.minecraft.world.level.block.Block currentBlock = currentState.getBlock();
        
        // 如果已经是光源方块，不需要替换
        if (currentBlock instanceof net.minecraft.world.level.block.LightBlock) {
            lastLightBlockPositions.put(player.getUUID(), feetPos);
            return;
        }
        
        // 只在空气方块生成光源方块，避免替换流体和植物
        if (currentState.isAir()) {
            // 获取光源方块
            net.minecraft.world.level.block.Block lightBlock = net.minecraft.world.level.block.Blocks.LIGHT;
            
            // 在玩家脚下生成光源方块
            level.setBlock(feetPos, lightBlock.defaultBlockState(), 2);
            
            // 更新玩家上次生成光源方块的位置
            lastLightBlockPositions.put(player.getUUID(), feetPos);
        }
    }
    
    // 用于跟踪玩家的食物使用状态和使用进度
    private static final Map<UUID, Integer> playerFoodUseProgress = new HashMap<>();
    private static final Map<UUID, Boolean> playerFoodConsumed = new HashMap<>();
    
    // 用于跟踪玩家是否因食用韩式炸鸡而死亡
    private static final Map<UUID, Boolean> playerDiedFromFriedChicken = new HashMap<>();
    
    /**
     * 检查玩家是否穿着全套空输套装
     */
    private static boolean isPlayerWearingFullAirDropArmor(Player player) {
        return isAirDropArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)) &&
               isAirDropArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)) &&
               isAirDropArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS)) &&
               isAirDropArmorPiece(player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET));
    }
    
    /**
     * 检查玩家是否穿着一半太阳套和一半空输套
     */
    private static boolean isPlayerWearingMixedArmor(Player player) {
        int sunArmorCount = 0;
        int airDropArmorCount = 0;
        
        for (net.minecraft.world.entity.EquipmentSlot slot : new net.minecraft.world.entity.EquipmentSlot[]{
            net.minecraft.world.entity.EquipmentSlot.HEAD,
            net.minecraft.world.entity.EquipmentSlot.CHEST,
            net.minecraft.world.entity.EquipmentSlot.LEGS,
            net.minecraft.world.entity.EquipmentSlot.FEET
        }) {
            ItemStack armorItem = player.getItemBySlot(slot);
            if (!armorItem.isEmpty()) {
                if (isSunArmorPiece(armorItem)) {
                    sunArmorCount++;
                } else if (isAirDropArmorPiece(armorItem)) {
                    airDropArmorCount++;
                }
            }
        }
        
        // 检查是否正好一半太阳套和一半空输套（各2件）
        return sunArmorCount == 2 && airDropArmorCount == 2;
    }
    
    /**
     * 检查物品是否为空输套装部件
     */
    private static boolean isAirDropArmorPiece(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AirDropItem;
    }
    
    /**
     * 处理玩家攻击实体事件
     */
    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        net.minecraft.world.entity.Entity targetEntity = event.getTarget();
        
        // 检查玩家是否穿着全套空输套装
        if (isPlayerWearingFullAirDropArmor(player) && targetEntity != null) {
            // 检查被攻击的实体是否是掠夺者、卫道士、女巫或劫掠兽
            if (targetEntity instanceof Pillager ||
                targetEntity instanceof Vindicator ||
                targetEntity instanceof Witch ||
                targetEntity instanceof Ravager) {
                // 阻止生物反击：将其攻击目标设置为null
                if (targetEntity instanceof Mob mob) {
                    mob.setTarget(null);
                    // 不要设置lastHurtByMob为null，这会导致卫道士AI崩溃
                }
            }
        }
    }
    
    /**
     * 处理生物攻击事件
     */
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        // 检查攻击目标是否是玩家
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            // 检查玩家是否穿着全套空输套装或混合套装
            if (isPlayerWearingFullAirDropArmor(player) || isPlayerWearingMixedArmor(player)) {
                // 检查攻击者是否是掠夺者、卫道士、女巫或劫掠兽
                net.minecraft.world.entity.Entity attacker = event.getSource().getEntity();
                
                // 处理直接攻击
                if (attacker != null && (attacker instanceof Pillager ||
                    attacker instanceof Vindicator ||
                    attacker instanceof Witch ||
                    attacker instanceof Ravager ||
                    attacker instanceof Evoker ||
                    attacker instanceof Vex)) {
                    event.setCanceled(true);
                    // 确保生物不再将玩家作为目标
                    if (attacker instanceof Mob mob) {
                        mob.setTarget(null);
                        // 不要设置lastHurtByMob为null，这会导致卫道士AI崩溃
                    }
                    return;
                }
                
                // 处理投射物攻击（如箭矢和药水）
                if (event.getSource() != null) {
                    net.minecraft.world.entity.Entity projectile = event.getSource().getDirectEntity();
                    net.minecraft.world.entity.Entity owner = event.getSource().getEntity();
                    
                    // 检查投射物是否来自掠夺者或女巫
                    if (owner != null && (owner instanceof Pillager ||
                        owner instanceof Witch ||
                        owner instanceof Evoker)) {
                        // 取消攻击
                        event.setCanceled(true);
                        
                        // 反弹投射物（如果是箭矢或药水）
                        if (projectile != null && projectile.isAlive()) {
                            // 计算反弹方向
                            Vec3 direction = player.getLookAngle();
                            projectile.setDeltaMovement(direction.scale(1.5));
                        }
                        
                        // 确保生物不再将玩家作为目标
                        if (owner instanceof Mob mob) {
                            mob.setTarget(null);
                            // 不要设置lastHurtByMob为null，这会导致卫道士AI崩溃
                        }
                        return;
                    }
                }
            }
        }
        
        // 检查雨姐是否攻击村民
        if (event.getEntity() instanceof Villager) {
            net.minecraft.world.entity.Entity attacker = event.getSource().getEntity();
            if (attacker instanceof com.asia.korea.entity.NortheastRainSister) {
                com.asia.korea.entity.NortheastRainSister rainSister = (com.asia.korea.entity.NortheastRainSister) attacker;
                
                // 检查附近是否有穿着太阳套装的玩家
                net.minecraft.world.level.Level level = rainSister.level();
                for (Player player : level.getEntitiesOfClass(Player.class, rainSister.getBoundingBox().inflate(32.0D))) {
                    if (isPlayerWearingFullSunArmor(player)) {
                        // 检查冷却时间
                        UUID rainSisterId = rainSister.getUUID();
                        long currentTime = level.getGameTime();
                        long lastHurtTime = rainSisterHurtCooldowns.getOrDefault(rainSisterId, 0L);
                        
                        if (currentTime - lastHurtTime >= 160) {
                            // 对雨姐造成55点伤害
                            rainSister.hurt(rainSister.damageSources().generic(), 55.0F);
                            rainSisterHurtCooldowns.put(rainSisterId, currentTime);
                        }
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * 处理生物目标选择事件
     */
    @SubscribeEvent
    public static void onMobGaze(net.minecraftforge.event.entity.living.LivingChangeTargetEvent event) {
        // 检查新目标是否是玩家
        if (event.getNewTarget() instanceof Player) {
            Player player = (Player) event.getNewTarget();
            
            // 检查玩家是否穿着全套空输套装或混合套装
            if (isPlayerWearingFullAirDropArmor(player) || isPlayerWearingMixedArmor(player)) {
                // 检查攻击者是否是掠夺者、卫道士、女巫或劫掠兽
                net.minecraft.world.entity.Entity attacker = event.getEntity();
                
                if (attacker != null && (attacker instanceof Pillager ||
                    attacker instanceof Vindicator ||
                    attacker instanceof Witch ||
                    attacker instanceof Ravager ||
                    attacker instanceof Evoker ||
                    attacker instanceof Vex)) {
                    // 取消目标选择
                    event.setCanceled(true);
                    // 确保生物不再将玩家作为目标
                    if (attacker instanceof Mob mob) {
                        mob.setTarget(null);
                        // 不要设置lastHurtByMob为null，这会导致卫道士AI崩溃
                    }
                }
            }
        }
    }
    
    /**
     * 检测玩家是否食用了韩式炸鸡
     */
    private static void checkPlayerFoodConsumption(Player player) {
        UUID playerId = player.getUUID();
        
        // 检查玩家是否正在使用物品
            if (player.isUsingItem()) {
                ItemStack stack = player.getUseItem();
                if (stack.getItem() == KOR.KOREAN_FRIED_CHICKEN.get() || stack.getItem() == KOR.KOREAN_FIRE_NOODLES.get()) {
                    // 计算物品使用进度（剩余使用时间）
                    int remainingTicks = player.getUseItemRemainingTicks();
                    
                    // 记录使用进度
                    playerFoodUseProgress.put(playerId, remainingTicks);
                    playerFoodConsumed.put(playerId, false); // 重置消耗标记
                }
            } else {
                // 玩家停止使用物品
                if (playerFoodUseProgress.containsKey(playerId)) {
                    // 检查是否已经处理过这个物品
                    if (!playerFoodConsumed.getOrDefault(playerId, false)) {
                        // 获取最后记录的使用进度
                        int lastRemainingTicks = playerFoodUseProgress.get(playerId);
                        
                        // 如果剩余时间少于5个刻（0.25秒），认为玩家吃完了食物
                        if (lastRemainingTicks < 5) {
                            // 检查玩家当前手持的物品（如果有的话）
                            ItemStack mainHandStack = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                            ItemStack offHandStack = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                            
                            // 检查是否是韩式炸鸡
                            if (mainHandStack.getItem() == KOR.KOREAN_FRIED_CHICKEN.get() || offHandStack.getItem() == KOR.KOREAN_FRIED_CHICKEN.get()) {
                                // 玩家真正食用了韩式炸鸡
                                handleFriedChickenConsumption(player);
                            } 
                            // 检查是否是韩式火鸡面
                            else if (mainHandStack.getItem() == KOR.KOREAN_FIRE_NOODLES.get() || offHandStack.getItem() == KOR.KOREAN_FIRE_NOODLES.get()) {
                                // 玩家真正食用了韩式火鸡面
                                handleFireNoodlesConsumption(player);
                            }
                        }
                        
                        // 标记为已处理，避免重复触发
                        playerFoodConsumed.put(playerId, true);
                    }
                    
                    // 清除跟踪信息
                    playerFoodUseProgress.remove(playerId);
                    playerFoodConsumed.remove(playerId);
                }
            }
    }
    
    /**
     * 处理玩家食用韩式炸鸡后的效果
     */
    private static void handleFriedChickenConsumption(Player player) {
        boolean isWearingSunArmor = isPlayerWearingFullSunArmor(player);
        boolean isWearingAirDropArmor = isPlayerWearingFullAirDropArmor(player);
        
        // 如果穿着太阳套装，显示批判性品尝消息
        if (isWearingSunArmor) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("<" + player.getScoreboardName() + ">: 这是批判性的品尝"),
                    false // 显示在聊天栏
            );
        }
        
        // 如果穿着空输套装，让玩家死亡
        if (isWearingAirDropArmor) {
            // 设置死亡标志
            playerDiedFromFriedChicken.put(player.getUUID(), true);
            // 设置客户端死亡标志
            playerDiedFromSpecialFood = true;
            
            // 直接设置生命值为0，而不是调用kill()，这样可以避免生成默认死亡消息
            player.setHealth(0.0F);
            
            // 手动广播自定义死亡消息给所有玩家
            String deathMessage = player.getScoreboardName() + "因卡卡给的餐补太多，油水过于足，翻肠子而亡";
            broadcastMessageToAllPlayers(player, deathMessage);
        }
    }
    
    /**
     * 处理玩家食用韩式火鸡面后的效果
     */
    private static void handleFireNoodlesConsumption(Player player) {
        boolean isWearingSunArmor = isPlayerWearingFullSunArmor(player);
        
        // 如果穿着太阳套装，让玩家死亡
        if (isWearingSunArmor) {
            // 显示死亡文案
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("吃这玩意？我看你是相思了"),
                    false // 显示在聊天栏
            );
            
            // 手动广播自定义死亡消息给所有玩家
            String deathMessage = player.getScoreboardName() + "被将军给雷霆了";
            broadcastMessageToAllPlayers(player, deathMessage);
            
            // 设置客户端死亡标志
            playerDiedFromSpecialFood = true;
            
            // 直接设置生命值为0，而不是调用kill()，这样可以避免生成默认死亡消息
            player.setHealth(0.0F);
        }
    }
    
    /**
     * 向服务器所有玩家广播消息
     */
    private static void broadcastMessageToAllPlayers(Player sourcePlayer, String message) {
        if (sourcePlayer.level().isClientSide()) {
            return;
        }
        
        // 广播消息给所有玩家
        for (Player player : sourcePlayer.level().players()) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(message),
                    false // 显示在聊天栏
            );
        }
    }
    
    /**
     * 处理实体死亡事件，自定义死亡消息
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerId = player.getUUID();
            
            // 检查玩家是否因食用韩式炸鸡而死亡
            if (playerDiedFromFriedChicken.getOrDefault(playerId, false)) {
                // 清除标志
                playerDiedFromFriedChicken.remove(playerId);
            }
        }
    }
    
    // 用于跟踪玩家是否因食用韩式炸鸡或火鸡面而死亡的客户端标志
    private static boolean playerDiedFromSpecialFood = false;
    
    // 客户端事件处理类
    @Mod.EventBusSubscriber(modid = "kor", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        
        // 使用反射来修改死亡屏幕的消息
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof DeathScreen) {
                DeathScreen deathScreen = (DeathScreen) minecraft.screen;
                try {
                    // 尝试不同的字段名称，因为在不同版本中可能会有变化
                    Field messageField = null;
                    
                    // 尝试Minecraft 1.19.2的字段名称
                    try {
                        messageField = DeathScreen.class.getDeclaredField("f_169996_"); // 死亡消息字段
                    } catch (NoSuchFieldException e1) {
                        // 尝试其他可能的字段名称
                        try {
                            messageField = DeathScreen.class.getDeclaredField("f_96953_"); // 旧版本字段名称
                        } catch (NoSuchFieldException e2) {
                            // 遍历所有字段，找到Component类型的字段
                            for (Field field : DeathScreen.class.getDeclaredFields()) {
                                if (field.getType().equals(net.minecraft.network.chat.Component.class)) {
                                    messageField = field;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (messageField != null) {
                        messageField.setAccessible(true);
                        // 只有当玩家因食用特殊食物而死亡时才修改消息
                        if (playerDiedFromSpecialFood) {
                            // 检查当前玩家是否穿着太阳套装
                            Player player = minecraft.player;
                            if (player != null) {
                                boolean isWearingSunArmor = isPlayerWearingFullSunArmor(player);
                                if (isWearingSunArmor) {
                                    // 如果穿着太阳套装，显示韩式火鸡面的死亡消息
                                    messageField.set(deathScreen, net.minecraft.network.chat.Component.literal("吃这玩意？我看你是相思了"));
                                } else {
                                    // 否则显示韩式炸鸡的死亡消息
                                    messageField.set(deathScreen, net.minecraft.network.chat.Component.literal("你因卡卡给的餐补太多，油水过于足，翻肠子而亡"));
                                }
                            }
                            // 重置标志，确保下次死亡时不会受到影响
                            playerDiedFromSpecialFood = false;
                        }
                        // 如果不是因特殊食物死亡，保持默认死亡消息不变
                    }
                } catch (Exception e) {
                    // 如果反射失败，忽略错误
                }
            }
        }
        
        // 在玩家离开游戏时清除标志
        @SubscribeEvent
        public static void onPlayerDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
            // 客户端不需要处理这个标志
        }
        
        // 监听聊天消息事件，过滤掉系统默认的死亡消息
        @SubscribeEvent
        public static void onClientChatReceived(ClientChatReceivedEvent event) {
            if (event.getMessage().getString().contains("被杀死了")) {
                // 检查是否是玩家因食用韩式炸鸡而死亡的情况
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    // 取消系统默认的死亡消息
                    event.setCanceled(true);
                }
            }
        }
    }
    
    /**
     * 处理混合套装效果（一半太阳套 + 一半空输套）
     */
    private static void handleMixedArmorEffect(Player player) {
        // 获取玩家附近的生物
        player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(10.0D)).forEach(mob -> {
            // 检查是否为掠夺者、卫道士、女巫、劫掠兽或唤魔者
            if (mob instanceof Pillager || mob instanceof Vindicator || mob instanceof Witch || mob instanceof Ravager || mob instanceof Evoker) {
                // 确保生物不再将玩家作为目标
                mob.setTarget(null);
                
                // 检查是否在冷却期内
                int entityId = mob.getId();
                int cooldown = mixedArmorMobJumpCooldowns.getOrDefault(entityId, 0);
                
                // 掠夺者和卫道士会举手跳跃，双手晃动
                if ((mob instanceof Pillager || mob instanceof Vindicator) && cooldown <= 0 && mob.onGround()) {
                    // 让生物朝向玩家
                    mob.getLookControl().setLookAt(player);
                    
                    // 只没收卫道士的武器，不没收掠夺者的弩
                    if (mob instanceof Vindicator) {
                        ItemStack mainHandItem = mob.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                        if (!mainHandItem.isEmpty()) {
                            confiscatedWeapons.put(entityId, mainHandItem.copy());
                            mob.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, net.minecraft.world.item.ItemStack.EMPTY);
                        }
                    }
                    
                    // 设置aggressive状态以触发举手动画
                    if (mob instanceof Pillager pillager) {
                        pillager.setAggressive(true);
                    } else if (mob instanceof Vindicator vindicator) {
                        vindicator.setAggressive(true);
                    }
                    
                    // 让生物跳跃
                    mob.setDeltaMovement(
                            0.0,
                            0.42,
                            0.0
                    );
                    
                    // 设置冷却时间
                    mixedArmorMobJumpCooldowns.put(entityId, MIXED_ARMOR_JUMP_COOLDOWN);
                } else {
                    // 减少冷却时间
                    if (cooldown > 0) {
                        mixedArmorMobJumpCooldowns.put(entityId, cooldown - 1);
                    }
                }
            }
        });
    }
    
    /**
     * 归还被没收的武器
     */
    private static void returnConfiscatedWeapons(Player player) {
        if (confiscatedWeapons.isEmpty()) {
            return;
        }
        
        // 获取玩家附近的生物
        player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(20.0D)).forEach(mob -> {
            int entityId = mob.getId();
            if (confiscatedWeapons.containsKey(entityId)) {
                ItemStack weapon = confiscatedWeapons.get(entityId);
                if (weapon != null && !weapon.isEmpty()) {
                    // 归还武器
                    mob.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, weapon);
                }
                // 移除记录
                confiscatedWeapons.remove(entityId);
            }
        });
    }
    
    /**
     * 获取玩家当前装备状态
     */
    private static String getPlayerArmorState(Player player) {
        StringBuilder state = new StringBuilder();
        for (net.minecraft.world.entity.EquipmentSlot slot : new net.minecraft.world.entity.EquipmentSlot[]{
            net.minecraft.world.entity.EquipmentSlot.HEAD,
            net.minecraft.world.entity.EquipmentSlot.CHEST,
            net.minecraft.world.entity.EquipmentSlot.LEGS,
            net.minecraft.world.entity.EquipmentSlot.FEET
        }) {
            ItemStack armorItem = player.getItemBySlot(slot);
            if (!armorItem.isEmpty()) {
                if (isSunArmorPiece(armorItem)) {
                    state.append("S");
                } else if (isAirDropArmorPiece(armorItem)) {
                    state.append("A");
                } else {
                    state.append("O");
                }
            } else {
                state.append("N");
            }
        }
        return state.toString();
    }
    
    /**
     * 放下所有生物的手
     */
    private static void lowerAllMobsHands(Player player) {
        player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(20.0D)).forEach(mob -> {
            if (mob instanceof Pillager pillager) {
                pillager.setAggressive(false);
            } else if (mob instanceof Vindicator vindicator) {
                vindicator.setAggressive(false);
            }
        });
    }
}