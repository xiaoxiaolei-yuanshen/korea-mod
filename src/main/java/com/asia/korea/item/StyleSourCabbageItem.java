package com.asia.korea.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.asia.korea.KOR;
import com.asia.korea.entity.NortheastRainSister;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

@Mod.EventBusSubscriber(modid = KOR.MODID)
public class StyleSourCabbageItem extends Item {
    
    public StyleSourCabbageItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }
        
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hitResult.getBlockPos();
            
            if (level.getBlockState(pos).getBlock() == KOR.SOUR_CABBAGE_BLOCK.get()) {
                summonBoss(level, pos);
                
                if (!player.isCreative()) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
                
                return InteractionResultHolder.success(stack);
            }
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    private static void summonBoss(Level level, BlockPos pos) {
        // 播放召唤音效
        net.minecraft.sounds.SoundEvent soundEvent = net.minecraft.sounds.SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation("kor", "act_on_behalf"));
        level.playSound(null, pos, soundEvent, SoundSource.HOSTILE, 1.0F, 1.0F);
        
        // 移除被点击的酸菜块
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        
        BlockPos spawnPos = pos.above();
        NortheastRainSister boss = KOR.NORTHEAST_RAIN_SISTER.get().create(level);
        if (boss != null) {
            boss.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0F, 0.0F);
            boss.setPersistenceRequired();
            level.addFreshEntity(boss);
        }
    }
}
