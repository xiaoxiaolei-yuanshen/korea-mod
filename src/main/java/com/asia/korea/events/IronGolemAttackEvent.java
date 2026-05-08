package com.asia.korea.events;

import com.asia.korea.KOR;
import com.asia.korea.entity.NortheastRainSister;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KOR.MODID)
public class IronGolemAttackEvent {
    
    @SubscribeEvent
    public static void onIronGolemSpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        if (event.getEntity() instanceof IronGolem) {
            IronGolem golem = (IronGolem) event.getEntity();
            
            golem.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(golem, NortheastRainSister.class, true));
        }
    }
}
