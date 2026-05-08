package com.asia.korea.events;

import com.asia.korea.KOR;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "kor")
public class GunMuEnchantmentEvents {
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            ItemStack mainHandItem = player.getMainHandItem();
            
            if (mainHandItem.isEmpty()) {
                return;
            }
            
            int gunMuLevel = EnchantmentHelper.getItemEnchantmentLevel(KOR.GUN_MU.get(), mainHandItem);
            
            if (gunMuLevel > 0) {
                float damageBonus = gunMuLevel * 1.5F;
                event.setAmount(event.getAmount() + damageBonus);
            }
        }
    }
}
