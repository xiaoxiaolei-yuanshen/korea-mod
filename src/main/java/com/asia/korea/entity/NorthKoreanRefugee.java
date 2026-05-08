package com.asia.korea.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.asia.korea.KOR;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.AABB;
import java.util.List;
import com.asia.korea.item.SunItem;

public class NorthKoreanRefugee extends PathfinderMob {
    // 静态变量来跟踪击杀计数（全局共享）
    private static int globalKillCount = 0;
    
    public NorthKoreanRefugee(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(0.6F);
    }

    @Override
    protected void registerGoals() {
        // 对于Mob类，我们需要从头设置所有目标
        
        // 基本生存目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 受攻击时的反击目标
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        
        // 移动和观察目标
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F, 1.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        // 尝试使用Mob类的createMobAttributes()方法作为基础
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D) // 默认速度
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }
    
    // 检查物品是否是太阳套装部件
    private boolean isSunArmorPiece(ItemStack stack) {
        return stack.getItem() instanceof SunItem;
    }
    
    // 检查附近是否有玩家穿着全套太阳套装
    private boolean hasNearbyPlayerWithFullSunArmor() {
        if (this.level().isClientSide) {
            return false;
        }
        
        List<Player> players = this.level().getEntitiesOfClass(Player.class, new AABB(this.blockPosition()).inflate(32.0D));
        
        for (Player player : players) {
            // 检查玩家是否穿着全套太阳套装
            if (isSunArmorPiece(player.getItemBySlot(EquipmentSlot.HEAD)) &&
                isSunArmorPiece(player.getItemBySlot(EquipmentSlot.CHEST)) &&
                isSunArmorPiece(player.getItemBySlot(EquipmentSlot.LEGS)) &&
                isSunArmorPiece(player.getItemBySlot(EquipmentSlot.FEET))) {
                return true;
            }
        }
        return false;
    }
    
    // 记录当前的移动速度设置，避免频繁更新
    private boolean isRunning = false;
    
    @Override
    public void tick() {
        super.tick();
        
        // 检查附近是否有玩家穿着全套太阳套装
        boolean hasSunArmorPlayer = hasNearbyPlayerWithFullSunArmor();
        
        // 根据是否有太阳套装玩家调整移动行为
        if (hasSunArmorPlayer && !isRunning) {
            // 当有太阳套装玩家时，使用跑步速度
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1.0D);
            
            // 更新移动目标
            this.goalSelector.removeGoal(new WaterAvoidingRandomStrollGoal(this, 0.6D));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
            
            isRunning = true;
        } else if (!hasSunArmorPlayer && isRunning) {
            // 当没有太阳套装玩家时，恢复默认漫步速度
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
            
            // 更新移动目标
            this.goalSelector.removeGoal(new WaterAvoidingRandomStrollGoal(this, 1.0D));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
            
            isRunning = false;
        }
    }

    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    public boolean isInvulnerableTo(net.minecraft.world.damagesource.DamageSource source) {
        // 确保脱北者不是无敌的
        return false;
    }
    
    @Override
    public boolean canBeLeashed(net.minecraft.world.entity.player.Player player) {
        return false;
    }
    
    @Override
    public boolean isLeashed() {
        return false;
    }
    
    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect) {
        return true;
    }
    
    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        // 检查伤害来源是否是生物攻击
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            // 获取攻击者的实体类型名称
            String attackerType = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(attacker.getType()).toString();
            
            // 检查攻击者是否是卫道士、掠夺者、女巫、劫掠兽、唤魔者或恼鬼
            if (attackerType.contains("evoker") || attackerType.contains("vindicator") || 
                attackerType.contains("pillager") || attackerType.contains("ravager") || 
                attackerType.contains("witch") || attackerType.contains("vex")) {
                // 这些生物的攻击对脱北者无效
                return false;
            }
        }
        
        // 其他伤害来源正常处理
        return super.hurt(source, amount);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) {
        super.checkFallDamage(y, onGround, state, pos);
    }
    
    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        // 调用父类die方法来确保正常的死亡处理
        super.die(damageSource);
        
        // 如果不是客户端世界，添加自定义掉落物
        if (!this.level().isClientSide) {
            // 检查是否是玩家击杀的
            net.minecraft.world.entity.LivingEntity killer = damageSource.getEntity() instanceof net.minecraft.world.entity.LivingEntity ? 
                (net.minecraft.world.entity.LivingEntity) damageSource.getEntity() : null;
            
            if (killer != null) {
                // 增加全局击杀计数
                globalKillCount++;
                int killCount = globalKillCount;
                
                // 特殊掉落逻辑
                boolean specialDrop = false;
                
                // 检查是否达到40只的阈值
                if (killCount == 40) {
                    // 杀死40只时只会掉落3个下界合金碎片，不会掉落其他物品
                    this.spawnAtLocation(net.minecraft.world.item.Items.NETHERITE_SCRAP, 3);
                    // 重置计数
                    globalKillCount = 0;
                    specialDrop = true;
                } 
                // 检查是否达到20只的阈值
                else if (killCount == 20) {
                    // 每杀20只时的特殊掉落
                    double specialDropRandom = this.random.nextDouble();
                    if (specialDropRandom < 0.15) { // 15%概率掉落下界合金碎片（降低概率）
                        this.spawnAtLocation(net.minecraft.world.item.Items.NETHERITE_SCRAP, 1);
                    } else if (specialDropRandom < 0.55) { // 40%概率掉落钻石块
                        this.spawnAtLocation(net.minecraft.world.item.Items.DIAMOND_BLOCK, 1);
                    } else { // 40%概率掉落锻造模板
                        // 只使用下界合金升级锻造模板（在1.20.1中可用的版本）
                        this.spawnAtLocation(net.minecraft.world.item.Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1);
                    }
                    specialDrop = true;
                }
                
                // 如果没有触发特殊掉落，生成普通掉落物
                if (!specialDrop) {
                    // 创建可能的掉落物列表，使用加权概率
                    // 格式：[物品, 权重值]
                    java.util.List<?> weightedDrops = java.util.Arrays.asList(
                        java.util.Arrays.asList(net.minecraft.world.item.Items.IRON_INGOT, 40),   // 铁锭，最高概率
                        java.util.Arrays.asList(net.minecraft.world.item.Items.GOLD_INGOT, 15),  // 金锭，权重降低（与绿宝石互换）
                        java.util.Arrays.asList(net.minecraft.world.item.Items.EMERALD, 25),     // 绿宝石，权重增加（与金锭互换）
                        java.util.Arrays.asList(net.minecraft.world.item.Items.DIAMOND, 10)      // 钻石，低概率
                        // 移除了下界合金碎片的普通掉落，只在特殊掉落中出现
                    );
                    
                    // 计算总权重
                    int totalWeight = 0;
                    for (Object entry : weightedDrops) {
                        totalWeight += (int) ((java.util.List<?>) entry).get(1);
                    }
                    
                    // 根据权重随机选择一个物品
                    net.minecraft.world.item.Item droppedItem = null;
                    int randomWeight = this.random.nextInt(totalWeight) + 1;
                    int currentWeight = 0;
                    
                    for (Object entry : weightedDrops) {
                        java.util.List<?> dropEntry = (java.util.List<?>) entry;
                        currentWeight += (int) dropEntry.get(1);
                        if (randomWeight <= currentWeight) {
                            droppedItem = (net.minecraft.world.item.Item) dropEntry.get(0);
                            break;
                        }
                    }
                    
                    // 调整数量概率：60%概率2个，40%概率3个
                    int dropCount;
                    double countRandom = this.random.nextDouble();
                    if (countRandom < 0.6) {
                        dropCount = 2;  // 60%概率掉落2个
                    } else {
                        dropCount = 3;  // 40%概率掉落3个
                    }
                    
                    // 创建掉落物堆
                    net.minecraft.world.item.ItemStack dropStack = new net.minecraft.world.item.ItemStack(droppedItem, dropCount);
                    
                    // 生成掉落物
                    this.spawnAtLocation(dropStack);
                }
            }
        }
    }
    
    // 由于脱北者继承自Villager类，需要确保它不会触发村民声望系统
    // 我们将通过事件监听器来处理这个问题

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    // 确保脱北者不影响玩家的声誉
    @Override
    public boolean isPersistenceRequired() {
        return true;
    }
    
    @Override
    public boolean isBaby() {
        return false;
    }
}
