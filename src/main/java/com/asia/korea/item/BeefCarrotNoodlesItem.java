package com.asia.korea.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemUtils;

public class BeefCarrotNoodlesItem extends Item {
    
    public BeefCarrotNoodlesItem() {
        super(new Item.Properties()
            .food(new net.minecraft.world.food.FoodProperties.Builder()
                .nutrition(11) // 牛肉（8点）加胡萝卜（3点）总和（11点饥饿值）
                .saturationMod(1.0F)
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