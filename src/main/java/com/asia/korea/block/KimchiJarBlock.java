package com.asia.korea.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import javax.annotation.Nullable;
import java.util.List;

public class KimchiJarBlock extends Block implements EntityBlock {
    
    public KimchiJarBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(0.5f)
                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                .noOcclusion()
                .isViewBlocking((state, getter, pos) -> false)
        );
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip.kor.kimchi_jar.line1"));
        tooltip.add(Component.translatable("tooltip.kor.kimchi_jar.line2"));
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new com.asia.korea.blockentity.KimchiJarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, stt, be) -> {
            if (be instanceof com.asia.korea.blockentity.KimchiJarBlockEntity kimchiJarBlockEntity) {
                kimchiJarBlockEntity.craftItem();
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide() && player instanceof ServerPlayer) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof com.asia.korea.blockentity.KimchiJarBlockEntity kimchiJarEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, kimchiJarEntity, pos);
            } else {
                throw new IllegalStateException("BlockEntity missing!");
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide());
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);
        return entity instanceof MenuProvider ? (MenuProvider) entity : null;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof com.asia.korea.blockentity.KimchiJarBlockEntity) {
                ((com.asia.korea.blockentity.KimchiJarBlockEntity) blockEntity).drops();
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }
    
    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return 0;
    }
}