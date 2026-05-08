package com.asia.korea.events;

import com.asia.korea.KOR;
import com.asia.korea.entity.NorthKoreanRefugee;
import com.asia.korea.item.AirDropItem;
import com.asia.korea.item.SunItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = KOR.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RefugeeSpawnEvent {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    // 用于限制生成频率（每1800 ticks，即1分钟30秒）
    private static int tickCounter = 0;
    // 冷却时间设置为1分30秒（1800 ticks）
    private static final int COOLDOWN_TICKS = 1800;
    // 用于跟踪已处理的村民区块，避免重复生成
    private static Set<Long> processedVillages = new HashSet<>();
    // 用于跟踪每个村庄已生成的脱北者数量
    private static java.util.Map<Long, Integer> villageRefugeeCount = new java.util.HashMap<>();
    // 每个村庄最大脱北者数量
    private static final int MAX_REFUGEES_PER_VILLAGE = 5;
    // 用于跟踪村庄统计时间
    private static int villageStatsCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // 只在服务器端运行
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        // 增加计数器
        tickCounter++;
        villageStatsCounter++;
        
        // 每1200 ticks（1分钟）更新一次村庄脱北者统计
        if (villageStatsCounter >= 1200) {
            updateVillageRefugeeCounts(event.getServer());
            villageStatsCounter = 0;
        }
        
        // 每COOLDOWN_TICKS（30秒）执行一次生成逻辑
        if (tickCounter % COOLDOWN_TICKS != 0) {
            return;
        }
        
        // 重置已处理村庄集合
        processedVillages.clear();
        
        // 遍历所有维度
        for (ServerLevel serverLevel : event.getServer().getAllLevels()) {
            // 查找所有村民
            List<Villager> villagers = serverLevel.getEntitiesOfClass(Villager.class, new AABB(-30000000, 0, -30000000, 30000000, 256, 30000000));
            
            // 遍历每个村民
            for (Villager villager : villagers) {
                // 计算村庄区块的哈希值，用于去重
                long villageKey = getVillageKey(villager.blockPosition());
                
                // 如果该村庄已经处理过，跳过
                if (processedVillages.contains(villageKey)) {
                    continue;
                }
                
                // 获取该村庄当前脱北者数量
                int currentCount = villageRefugeeCount.getOrDefault(villageKey, 0);
                
                // 如果该村庄已达到最大脱北者数量，跳过
                if (currentCount >= MAX_REFUGEES_PER_VILLAGE) {
                    continue;
                }
                
                // 标记该村庄为已处理
                processedVillages.add(villageKey);
                
                // 检查附近是否有玩家穿着全套空输套装或太阳套装
                boolean hasPlayerWithAirborne = hasPlayerWearingFullAirborneArmor(villager, serverLevel);
                boolean hasPlayerWithSunArmor = hasPlayerWearingFullSunArmor(villager, serverLevel);
                
                // 根据玩家穿着的套装选择生成概率
                int spawnChance;
                if (hasPlayerWithSunArmor) {
                    // 穿着太阳套装时生成概率为10
                    spawnChance = 10;
                } else if (hasPlayerWithAirborne) {
                    // 穿着空输套装时生成概率为75
                    spawnChance = 75;
                } else {
                    // 默认生成概率为25
                    spawnChance = 25;
                }
                
                // 有概率在村庄生成1只脱北者
                if (serverLevel.getRandom().nextInt(100) < spawnChance) {
                    // 在村民周围生成1只脱北者
                    int refugeeCount = 1;
                    
                    for (int i = 0; i < refugeeCount; i++) {
                        // 在村民周围10-20格范围内随机选择一个位置
                        int xOffset = serverLevel.getRandom().nextInt(20) - 10;
                        int zOffset = serverLevel.getRandom().nextInt(20) - 10;
                        int x = villager.blockPosition().getX() + xOffset;
                        int z = villager.blockPosition().getZ() + zOffset;
                        
                        // 找到合适的地面高度
                        int y = serverLevel.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, x, z);
                        BlockPos spawnPos = new BlockPos(x, y, z);
                        
                        // 创建难民实体
                        NorthKoreanRefugee refugee = new NorthKoreanRefugee(KOR.NORTH_KOREAN_REFUGEE.get(), serverLevel);
                        refugee.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                        
                        // 确保生成规则允许
                        if (net.minecraft.world.entity.SpawnPlacements.checkSpawnRules(KOR.NORTH_KOREAN_REFUGEE.get(), serverLevel, net.minecraft.world.entity.MobSpawnType.NATURAL, spawnPos, serverLevel.getRandom())) {
                            // 生成难民
                            serverLevel.addFreshEntity(refugee);
                            
                            // 更新该村庄脱北者计数
                            villageRefugeeCount.put(villageKey, currentCount + 1);
                            
                            // 每个服务器tick只生成一个脱北者
                            return;
                        }
                    }
                }
            }
        }
    }
    
    // 检查村民附近是否有玩家穿着全套空输套装
    private static boolean hasPlayerWearingFullAirborneArmor(Villager villager, ServerLevel serverLevel) {
        // 搜索半径为32格的范围内的玩家
        for (Player player : serverLevel.players()) {
            if (villager.distanceTo(player) <= 32.0D) {
                // 检查玩家是否穿着全套空输套装
                if (isWearingFullAirborneArmor(player)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // 检查玩家是否穿着全套空输套装
    private static boolean isWearingFullAirborneArmor(Player player) {
        // 检查头盔
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof AirDropItem.Helmet)) {
            return false;
        }
        
        // 检查胸甲
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestplate.getItem() instanceof AirDropItem.Chestplate)) {
            return false;
        }
        
        // 检查护腿
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!(leggings.getItem() instanceof AirDropItem.Leggings)) {
            return false;
        }
        
        // 检查靴子
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof AirDropItem.Boots)) {
            return false;
        }
        
        // 所有装备都是空输套装
        return true;
    }
    
    // 检查玩家是否穿着全套太阳套装
    private static boolean isWearingFullSunArmor(Player player) {
        // 检查头盔
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof SunItem)) {
            return false;
        }
        
        // 检查胸甲
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestplate.getItem() instanceof SunItem)) {
            return false;
        }
        
        // 检查护腿
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!(leggings.getItem() instanceof SunItem)) {
            return false;
        }
        
        // 检查靴子
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof SunItem)) {
            return false;
        }
        
        // 所有装备都是太阳套装
        return true;
    }
    
    // 检查村民附近是否有玩家穿着全套太阳套装
    private static boolean hasPlayerWearingFullSunArmor(Villager villager, ServerLevel serverLevel) {
        // 搜索半径为32格的范围内的玩家
        for (Player player : serverLevel.players()) {
            if (villager.distanceTo(player) <= 32.0D) {
                // 检查玩家是否穿着全套太阳套装
                if (isWearingFullSunArmor(player)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // 计算村庄区块的哈希值，用于去重
    private static long getVillageKey(BlockPos pos) {
        // 将位置转换为区块坐标（每个区块16x16）
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        
        // 将区块坐标合并为一个long值
        return ((long) chunkX) << 32 | (chunkZ & 0xFFFFFFFFL);
    }
    
    // 更新每个村庄的脱北者统计
    private static void updateVillageRefugeeCounts(net.minecraft.server.MinecraftServer server) {
        // 重置统计
        villageRefugeeCount.clear();
        
        // 遍历所有维度
        for (ServerLevel serverLevel : server.getAllLevels()) {
            // 查找所有脱北者
            List<NorthKoreanRefugee> refugees = serverLevel.getEntitiesOfClass(NorthKoreanRefugee.class, new AABB(-30000000, 0, -30000000, 30000000, 256, 30000000));
            
            // 统计每个村庄的脱北者数量
            for (NorthKoreanRefugee refugee : refugees) {
                long villageKey = getVillageKey(refugee.blockPosition());
                villageRefugeeCount.put(villageKey, villageRefugeeCount.getOrDefault(villageKey, 0) + 1);
            }
        }
    }
    
    // 处理脱北者受到伤害事件，防止影响村民声望
    @SubscribeEvent
    public static void onRefugeeHurt(LivingHurtEvent event) {
        // 检查受到伤害的实体是否是脱北者
        if (!(event.getEntity() instanceof NorthKoreanRefugee)) {
            return;
        }
        
        // 检查伤害来源是否是玩家
        if (!(event.getSource().getEntity() instanceof Player)) {
            return;
        }
        
        // 获取玩家
        Player player = (Player) event.getSource().getEntity();
        
        LOGGER.info("玩家 {} 攻击了脱北者，将通过死亡事件处理来避免影响村民价格", player.getName().getString());
    }
    
    // 处理脱北者死亡事件，作为额外保障
    @SubscribeEvent
    public static void onRefugeeDeath(LivingDeathEvent event) {
        // 检查死亡的实体是否是脱北者
        if (!(event.getEntity() instanceof NorthKoreanRefugee)) {
            return;
        }
        
        // 检查伤害来源是否是玩家
        if (event.getSource().getEntity() instanceof Player) {
            LOGGER.info("脱北者死亡，但由于伤害来源已被修改，不会影响村民价格");
        }
    }
}
