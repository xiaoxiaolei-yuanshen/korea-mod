package com.asia.korea.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemUtils;

public class SpicyKimchiItem extends Item {
    
    public SpicyKimchiItem() {
        super(new Item.Properties()
            .food(new net.minecraft.world.food.FoodProperties.Builder()
                .nutrition(8) // 4个鸡腿（8点饥饿值）
                .saturationMod(0.6F)
                .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 400, 0), 1.0F) // 20秒饥饿1效果
                .build())
        );
    }
    
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 32; // 食用时间
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
}