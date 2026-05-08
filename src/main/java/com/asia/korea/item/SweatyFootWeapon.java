package com.asia.korea.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;

public class SweatyFootWeapon extends SwordItem {
    public SweatyFootWeapon() {
        super(new Tier() {
            @Override
            public int getUses() {
                return 250;
            }

            @Override
            public float getSpeed() {
                return 0.0F;
            }

            @Override
            public float getAttackDamageBonus() {
                return 5.0F;
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public int getEnchantmentValue() {
                return 10;
            }

            @Override
            public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
                return net.minecraft.world.item.crafting.Ingredient.EMPTY;
            }
        }, 3, -2.4F, new Item.Properties());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
        
        // 随机选择一个额外效果，不会同时获得
        float random = target.getRandom().nextFloat();
        
        if (random < 0.25) {
            // 25% 概率添加反胃效果
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        } else if (random < 0.40) {
            // 15% 概率添加失明效果
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
        }
        
        return super.hurtEnemy(stack, target, attacker);
    }
}