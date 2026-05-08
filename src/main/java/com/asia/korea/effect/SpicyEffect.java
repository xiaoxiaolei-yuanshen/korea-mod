package com.asia.korea.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class SpicyEffect extends MobEffect {
    
    public SpicyEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF6B35); // 使用橙红色作为效果颜色
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 实现缓慢扣血逻辑，每次扣0.5点血，保留半颗星
        if (entity.getHealth() > 1.0F) {
            float damage = 0.5F * (amplifier + 1);
            entity.hurt(entity.damageSources().magic(), damage);
        }
        
        // 生成橙红色粒子效果
        Level level = entity.level();
        RandomSource random = level.random;
        
        // 在实体周围生成粒子
        for (int i = 0; i < 5; i++) {
            double x = entity.getX() + (random.nextDouble() - 0.5) * entity.getBbWidth() * 2.0;
            double y = entity.getY() + random.nextDouble() * entity.getBbHeight();
            double z = entity.getZ() + (random.nextDouble() - 0.5) * entity.getBbWidth() * 2.0;
            
            // 使用橙红色的粒子
            level.addParticle(
                ParticleTypes.ENTITY_EFFECT, 
                x, y, z, 
                1.0, 0.41, 0.21 // 橙红色的RGB值
            );
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每20刻（1秒）触发一次效果，实现缓慢扣血
        return duration % 20 == 0;
    }
}