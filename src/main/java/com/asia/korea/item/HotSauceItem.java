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
import net.minecraft.world.item.Items;
import com.asia.korea.KOR;

public class HotSauceItem extends Item {
    
    public HotSauceItem() {
        super(new Item.Properties());
    }
    
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 28; // 饮用时间
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity) {
        // 调用父类方法完成食用
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // 手动应用所有效果
        entity.addEffect(new MobEffectInstance(KOR.SPICY_EFFECT.get(), 200, 1)); // 10秒辣效果，等级1
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 500, 0)); // 25秒抗性提升
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 0)); // 10秒2颗金心
        entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1800, 0)); // 1分半夜视效果
        
        // 只有玩家可以获得玻璃瓶
        if (entity instanceof Player player) {
            // 仅在生存模式和冒险模式下返还瓶子（创造模式下instabuild为true）
            if (!player.getAbilities().instabuild) {
                // 给玩家一个玻璃瓶
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                
                if (!player.getInventory().add(bottle)) {
                    // 如果物品栏满了，将瓶子掉落
                    player.drop(bottle, false);
                }
            }
        }
        
        return result;
    }
}