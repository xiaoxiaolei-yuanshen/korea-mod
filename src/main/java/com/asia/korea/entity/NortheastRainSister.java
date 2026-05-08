package com.asia.korea.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;
import com.asia.korea.KOR;

public class NortheastRainSister extends Monster {
    private static final EntityDataAccessor<Integer> DATA_BOSS_PHASE = SynchedEntityData.defineId(NortheastRainSister.class, EntityDataSerializers.INT);
    
    private ServerBossEvent bossEvent;
    private int attackCooldown = 0;
    private int specialAttackCooldown = 0;
    private int bossPhase = 1;
    
    public NortheastRainSister(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 500;
        this.setPersistenceRequired();
        this.setCanPickUpLoot(false);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BOSS_PHASE, 1);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0F, 1.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.npc.Villager.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            if (this.bossEvent == null && this.isAlive()) {
                this.bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
                this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            }
            
            if (this.bossEvent != null) {
                this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
                
                // 定期更新Boss血条的玩家列表
                if (this.tickCount % 20 == 0) {
                    this.bossEvent.removeAllPlayers();
                    for (net.minecraft.server.level.ServerPlayer player : ((net.minecraft.server.level.ServerLevel) this.level()).getPlayers(p -> p.distanceToSqr(this) <= 48.0D * 48.0D)) {
                        this.bossEvent.addPlayer(player);
                    }
                }
            }
            
            if (attackCooldown > 0) {
                attackCooldown--;
            }
            
            if (specialAttackCooldown > 0) {
                specialAttackCooldown--;
            }
            
            updateBossPhase();
            performSpecialAttacks();
        }
    }
    
    private void updateBossPhase() {
        double currentHealth = this.getHealth();
        int newPhase = 1;
        
        if (currentHealth <= 100.0D) {
            newPhase = 3;
        } else if (currentHealth <= 200.0D) {
            newPhase = 2;
        }
        
        if (newPhase != this.bossPhase) {
            this.bossPhase = newPhase;
            this.entityData.set(DATA_BOSS_PHASE, newPhase);
            
            AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttribute != null) {
                speedAttribute.removeModifier(UUID.fromString("8e5f3a9e-8f4c-4a9e-9d8e-4c7e8f5a8b2d"));
                double speedBonus = 0.1D * newPhase;
                speedAttribute.addPermanentModifier(new AttributeModifier(
                    UUID.fromString("8e5f3a9e-8f4c-4a9e-9d8e-4c7e8f5a8b2d"),
                    "boss_phase_speed_bonus",
                    speedBonus,
                    AttributeModifier.Operation.ADDITION
                ));
            }
        }
    }
    

    
    private void performSpecialAttacks() {
        if (specialAttackCooldown > 0) {
            return;
        }
        
        LivingEntity target = this.getTarget();
        if (target == null || !this.hasLineOfSight(target)) {
            return;
        }
        
        double distance = this.distanceTo(target);
        
        if (this.bossPhase == 2 && distance < 15.0D) {
            performRangedAttack(target);
            specialAttackCooldown = 180;
        } else if (this.bossPhase == 3) {
            performAreaAttack();
            specialAttackCooldown = 400;
        }
    }
    
    private void performAreaAttack() {
        AABB attackBox = this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D);
        java.util.List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, attackBox);
        
        for (LivingEntity entity : nearbyEntities) {
            if (entity != this) {
                entity.setSecondsOnFire(10);
                entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                    200,
                    1,
                    false,
                    true
                ));
                entity.hurt(this.damageSources().inFire(), 4.0F);
            }
        }
        
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.8F);
    }
    
    private void performRangedAttack(LivingEntity target) {
        double dx = target.getX() - this.getX();
        double dy = target.getY() - this.getY() - 1.0D;
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance > 0) {
            double speed = 0.5D;
            double vx = (dx / distance) * speed;
            double vy = (dy / distance) * speed + 0.1D;
            double vz = (dz / distance) * speed;
            
            net.minecraft.world.entity.projectile.SmallFireball fireball = new net.minecraft.world.entity.projectile.SmallFireball(this.level(), this, vx, vy, vz);
            fireball.setPos(this.getX(), this.getY() + 1.5D, this.getZ());
            this.level().addFreshEntity(fireball);
        }
        
        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.8F);
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
            return false;
        }
        
        boolean result = super.hurt(source, amount);
        
        if (result && this.bossEvent != null) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
        
        return result;
    }
    
    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        
        if (this.bossEvent != null) {
            this.bossEvent.setProgress(0.0F);
            this.bossEvent.removeAllPlayers();
            this.bossEvent = null;
        }
        
        // 100% 掉落大汗脚武器
        this.spawnAtLocation(KOR.SWEATY_FOOT_WEAPON.get(), 1);
        
        // 随机掉落额外物品
        float random = this.getRandom().nextFloat();
        
        if (random < 0.30) {
            // 30% 概率掉落1个钻石
            this.spawnAtLocation(Items.DIAMOND, 1);
        } else if (random < 0.40) {
            // 10% 概率掉落1个附魔金苹果
            this.spawnAtLocation(Items.ENCHANTED_GOLDEN_APPLE, 1);
        } else if (random < 0.45) {
            // 5% 概率掉落1个不死图腾
            this.spawnAtLocation(Items.TOTEM_OF_UNDYING, 1);
        } else if (random < 0.46) {
            // 1% 概率掉落3个下界合金碎片
            this.spawnAtLocation(Items.NETHERITE_SCRAP, 3);
        }
    }
    
    public boolean canBeLeashed(Player player) {
        return false;
    }
    
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
    
    public boolean isPersistenceRequired() {
        return true;
    }
    
    public MobType getMobType() {
        return MobType.ILLAGER;
    }
    
    protected void populateDefaultEquipmentSlots() {
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
    }
    
    public boolean isPushable() {
        return false;
    }
    
    public int getBossPhase() {
        return this.entityData.get(DATA_BOSS_PHASE);
    }
    
    public ServerBossEvent getBossEvent() {
        return this.bossEvent;
    }
}