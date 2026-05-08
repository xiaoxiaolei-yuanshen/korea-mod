package com.asia.korea.events;

import com.asia.korea.KOR;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "kor")
public class WoodStickWoodAnvilRecipe {
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        
        if (left.isEmpty() || right.isEmpty()) {
            return;
        }
        
        // 检查是否是木棍和附魔书的组合
        boolean isStickLeft = left.getItem() == Items.STICK;
        boolean isEnchantedBookRight = right.getItem() == Items.ENCHANTED_BOOK;
        boolean isEnchantedBookLeft = left.getItem() == Items.ENCHANTED_BOOK;
        boolean isStickRight = right.getItem() == Items.STICK;
        
        if ((isStickLeft && isEnchantedBookRight) || (isEnchantedBookLeft && isStickRight)) {
            // 获取附魔书
            ItemStack enchantedBook = isEnchantedBookLeft ? left : right;
            
            // 检查是否有棍母附魔
            int gunMuLevel = EnchantmentHelper.getItemEnchantmentLevel(KOR.GUN_MU.get(), enchantedBook);
            
            if (gunMuLevel > 0) {
                // 创建木棍木物品
                ItemStack result = new ItemStack(KOR.WOOD_STICK_WOOD.get());
                
                // 设置输出
                event.setOutput(result);
                event.setCost(1);
                event.setMaterialCost(1);
                
                // 取消默认行为
                event.setCanceled(true);
            }
        }
    }
}
