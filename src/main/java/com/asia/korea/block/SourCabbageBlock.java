package com.asia.korea.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.List;
import java.util.Random;

public class SourCabbageBlock extends Block {
    
    public SourCabbageBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(0.0F) // 与干海带块相同的硬度
                .sound(net.minecraft.world.level.block.SoundType.MOSS_CARPET) // 使用类似的声音
        );
    }
    
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        
        // 检查是否是空手破坏
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        boolean isSilkTouch = tool != null && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
        
        // 如果使用了精准采集，掉落方块本身
        if (isSilkTouch) {
            return List.of(new ItemStack(this));
        }
        
        // 如果使用了工具，掉落方块本身
        if (tool != null && !tool.isEmpty()) {
            return List.of(new ItemStack(this));
        }
        
        // 空手破坏或使用不正确的工具时，掉落2-4个酸菜
        Random random = new Random();
        int dropCount;
        
        // 使用配置文件中的概率值
        double dropFourChance = com.asia.korea.Config.sourCabbageBlockDropFourChance;
        
        // 根据配置的概率掉落4个酸菜
        if (random.nextFloat() < dropFourChance) {
            dropCount = 4;
        } else {
            // 其他情况随机掉落2-3个酸菜
            dropCount = 2 + random.nextInt(2);
        }
        
        // 获取酸菜物品
        ItemStack sourCabbage = new ItemStack(com.asia.korea.KOR.SOUR_CABBAGE.get());
        sourCabbage.setCount(dropCount);
        
        return List.of(sourCabbage);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 阻止右键交互，确保玩家可以正常放置和破坏
        return InteractionResult.PASS;
    }
}
