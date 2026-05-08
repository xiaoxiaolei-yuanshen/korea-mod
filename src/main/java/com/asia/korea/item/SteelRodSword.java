package com.asia.korea.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class SteelRodSword extends SwordItem {
    public SteelRodSword() {
        super(new Tier() {
            @Override
            public int getUses() {
                return 1530;
            }

            @Override
            public float getSpeed() {
                return 4.0F;
            }

            @Override
            public float getAttackDamageBonus() {
                return 7.0F; // 剑的总攻击力 = 3（基础） + 7（此值）= 10点
            }

            @Override
            public int getLevel() {
                return 2;
            }

            @Override
            public int getEnchantmentValue() {
                return 10;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(Items.IRON_INGOT));
            }
        }, 3, -2.4F, new Item.Properties());
    }
}
