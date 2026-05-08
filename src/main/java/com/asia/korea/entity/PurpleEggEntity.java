package com.asia.korea.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import com.asia.korea.KOR;

// 基于雪球的实现方式重新制作紫蛋实体
public class PurpleEggEntity extends ThrowableItemProjectile {
    public PurpleEggEntity(EntityType<? extends PurpleEggEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PurpleEggEntity(Level level, double x, double y, double z) {
        super(KOR.PURPLE_EGG_ENTITY.get(), x, y, z, level);
    }

    public PurpleEggEntity(LivingEntity livingEntity, Level level) {
        super(KOR.PURPLE_EGG_ENTITY.get(), livingEntity, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            // 记录伤害前的生命值
            float healthBefore = livingEntity.getHealth();
            
            // 使用实体自己的damageSources方法获取伤害源，确保触发正常的死亡机制
            DamageSource damageSource;
            // 根据所有者类型选择合适的伤害源
            if (this.getOwner() instanceof Player player) {
                // 如果所有者是玩家，使用玩家攻击伤害源
                damageSource = livingEntity.damageSources().playerAttack(player);
            } else if (this.getOwner() instanceof LivingEntity livingOwner) {
                // 如果所有者是其他生物，使用生物攻击伤害源
                damageSource = livingEntity.damageSources().mobAttack(livingOwner);
            } else {
                // 否则使用通用伤害源
                damageSource = livingEntity.damageSources().generic();
            }
            // 造成100点伤害
            livingEntity.hurt(damageSource, 100.0F);
            
            // 检查生物是否被杀死
            if (livingEntity.isDeadOrDying() && healthBefore > 0) {
                // 生物被杀死，播放万岁音效
                float volume = 1.0F;
                float pitch = 1.0F;
                
                // 随机选择播放wansui01或wansui02音效
                if (this.random.nextBoolean()) {
                    // 播放wansui01音效
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                        KOR.WANSUI01.get(), SoundSource.PLAYERS, volume, pitch);
                } else {
                    // 播放wansui02音效
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                        KOR.WANSUI02.get(), SoundSource.PLAYERS, volume, pitch);
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), 
                    this.getX(), this.getY(), this.getZ(), 
                    ((double)this.random.nextFloat() - 0.5D) * 0.08D, 
                    ((double)this.random.nextFloat() - 0.5D) * 0.08D, 
                    ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.5F, 
                0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
        }
    }

    @Override
    protected Item getDefaultItem() {
        return KOR.PURPLE_EGG.get();
    }
}
