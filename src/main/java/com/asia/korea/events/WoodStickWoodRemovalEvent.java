package com.asia.korea.events;

import com.asia.korea.KOR;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "kor")
public class WoodStickWoodRemovalEvent {
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        Player player = event.player;
        
        if (player.level().isClientSide) {
            return;
        }
        
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            
            if (stack.getItem() == KOR.WOOD_STICK_WOOD.get()) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
    }
}
