package com.asia.korea.events;

import com.asia.korea.KOR;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = KOR.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VindicatorEquipmentEvent {

    // 跟踪卫道士是否手持钢棍
    private static final Map<UUID, Boolean> vindicatorHoldingSteelRod = new HashMap<>();
    // 跟踪卫道士是否已经有速度效果
    private static final Map<UUID, Boolean> vindicatorHasSpeed = new HashMap<>();

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // 检查生成的实体是否是卫道士，并且只在服务器端执行
        if (event.getEntity() instanceof Vindicator vindicator && !event.getLevel().isClientSide) {
            // 替换卫道士的主手武器为钢棍
            ItemStack steelRod = new ItemStack(KOR.STEEL_ROD_SWORD.get());
            vindicator.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, steelRod);
            
            // 初始化卫道士的状态
            vindicatorHoldingSteelRod.put(vindicator.getUUID(), true);
            vindicatorHasSpeed.put(vindicator.getUUID(), false);
        }
    }
    
    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        // 检查是否是卫道士，并且只在服务器端执行
        if (event.getEntity() instanceof Vindicator vindicator && !event.getEntity().level().isClientSide) {
            // 只处理主手装备变化
            if (event.getSlot() == net.minecraft.world.entity.EquipmentSlot.MAINHAND) {
                UUID uuid = vindicator.getUUID();
                
                // 检查新装备是否是钢棍
                boolean hasSteelRod = !event.getTo().isEmpty() && event.getTo().getItem() == KOR.STEEL_ROD_SWORD.get();
                
                // 更新卫道士的手持状态
                vindicatorHoldingSteelRod.put(uuid, hasSteelRod);
                
                // 处理速度效果
                if (hasSteelRod && !vindicatorHasSpeed.getOrDefault(uuid, false)) {
                    // 给卫道士添加速度效果
                    vindicator.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3, false, false));
                    vindicatorHasSpeed.put(uuid, true);
                } else if (!hasSteelRod && vindicatorHasSpeed.getOrDefault(uuid, false)) {
                    // 清除卫道士的速度效果
                    vindicator.removeEffect(MobEffects.MOVEMENT_SPEED);
                    vindicatorHasSpeed.put(uuid, false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        // 如果忠诚模式未开启，不处理
        if (!KOR.loyaltyModeEnabled()) {
            return;
        }
        
        // 获取服务器并遍历所有维度
        event.getServer().getAllLevels().forEach(level -> {
            // 遍历所有卫道士实体
            level.getEntitiesOfClass(Vindicator.class, net.minecraft.world.phys.AABB.ofSize(level.getSharedSpawnPos().getCenter(), 10000, 10000, 10000)).forEach(vindicator -> {
                UUID uuid = vindicator.getUUID();
                boolean hasSteelRod = false;
                
                // 更严格的装备检查
                ItemStack mainHandItem = vindicator.getMainHandItem();
                if (!mainHandItem.isEmpty() && mainHandItem.getItem() == KOR.STEEL_ROD_SWORD.get()) {
                    hasSteelRod = true;
                }
                
                // 更新手持状态
                vindicatorHoldingSteelRod.put(uuid, hasSteelRod);
                
                // 处理速度效果
                if (hasSteelRod && !vindicatorHasSpeed.getOrDefault(uuid, false)) {
                    // 给卫道士添加速度效果
                    vindicator.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3, false, false));
                    vindicatorHasSpeed.put(uuid, true);
                } else if (!hasSteelRod && vindicatorHasSpeed.getOrDefault(uuid, false)) {
                    // 清除卫道士的速度效果
                    vindicator.removeEffect(MobEffects.MOVEMENT_SPEED);
                    vindicatorHasSpeed.put(uuid, false);
                }
            });
        });
    }
}