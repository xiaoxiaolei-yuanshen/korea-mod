package com.asia.korea.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item;

public class StickWeapon extends SwordItem {
    public StickWeapon() {
        super(new Tier() {
            @Override
            public int getUses() {
                return 86;
            }

            @Override
            public float getSpeed() {
                return 2.0F;
            }

            @Override
            public float getAttackDamageBonus() {
                return 3.5F; // 剑的总攻击力 = 3（基础） + 3.5（此值）= 6.5点
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public int getEnchantmentValue() {
                return 5;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(Items.OAK_PLANKS));
            }
        }, 3, -3.4F, new Item.Properties());
    }
}