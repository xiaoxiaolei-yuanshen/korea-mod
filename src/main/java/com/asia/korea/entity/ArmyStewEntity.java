package com.asia.korea.entity;

import com.asia.korea.KOR;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ArmyStewEntity extends ThrowableItemProjectile {
    public ArmyStewEntity(EntityType<? extends ArmyStewEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ArmyStewEntity(Level level, double x, double y, double z) {
        super(KOR.ARMY_STEW_ENTITY.get(), x, y, z, level);
    }

    public ArmyStewEntity(LivingEntity livingEntity, Level level) {
        super(KOR.ARMY_STEW_ENTITY.get(), livingEntity, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            // 造成2点伤害
            DamageSource damageSource;
            if (this.getOwner() instanceof Player player) {
                damageSource = livingEntity.damageSources().playerAttack(player);
            } else if (this.getOwner() instanceof LivingEntity livingOwner) {
                damageSource = livingEntity.damageSources().mobAttack(livingOwner);
            } else {
                damageSource = livingEntity.damageSources().generic();
            }
            livingEntity.hurt(damageSource, 2.0F);
            
            // 添加辣效果（火焰效果）和失明效果，持续10秒
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0)); // 10秒 = 200刻
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0)); // 用中毒效果模拟辣效果
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
        return KOR.ARMY_STEW.get();
    }
}