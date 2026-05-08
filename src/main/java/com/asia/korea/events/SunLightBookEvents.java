package com.asia.korea.events;

import com.asia.korea.item.SunLightBook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "kor")
public class SunLightBookEvents {
    
    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        Entity targetEntity = event.getTarget();
        
        if (player == null || targetEntity == null) {
            return;
        }
        
        ItemStack mainHandItem = player.getMainHandItem();
        
        if (mainHandItem.getItem() instanceof SunLightBook && targetEntity instanceof LivingEntity livingEntity) {
            if (livingEntity.isAlive()) {
                applySunLightEffects(livingEntity, player, mainHandItem);
                mainHandItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(net.minecraft.world.InteractionHand.MAIN_HAND));
            }
        }
    }
    
    private static void applySunLightEffects(LivingEntity entity, Player player, ItemStack stack) {
        int BURN_DURATION = 200;
        int SLOW_DURATION = 200;
        int BURN_DAMAGE = 5;
        int fireAspectLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
        int totalDamage = BURN_DAMAGE + fireAspectLevel;
        
        net.minecraft.world.effect.MobEffectInstance burnEffect = new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.GLOWING,
                BURN_DURATION,
                0,
                false,
                true
        );
        entity.addEffect(burnEffect);
        
        net.minecraft.world.effect.MobEffectInstance slowEffect = new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                SLOW_DURATION,
                2,
                false,
                true
        );
        entity.addEffect(slowEffect);
        
        entity.setSecondsOnFire(BURN_DURATION / 20);
        
        entity.hurt(entity.level().damageSources().indirectMagic(player, player), totalDamage);
    }
}
