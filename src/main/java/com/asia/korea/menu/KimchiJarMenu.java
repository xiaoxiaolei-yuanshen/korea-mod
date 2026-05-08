package com.asia.korea.menu;

import com.asia.korea.KOR;
import com.asia.korea.blockentity.KimchiJarBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.Nullable;

public class KimchiJarMenu extends AbstractContainerMenu {
    public final KimchiJarBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public KimchiJarMenu(int pContainerId, Inventory inv, KimchiJarBlockEntity entity, ContainerData data) {
        super(ModMenus.KIMCHI_JAR_MENU.get(), pContainerId);
        this.blockEntity = entity;
        this.level = inv.player.level();
        this.data = data;

        // 添加物品槽位 - 使用自定义Slot类
        addSlot(new SimpleSlot(entity, 0, 56, 17));
        addSlot(new SimpleSlot(entity, 1, 56, 53));
        addSlot(new SimpleSlot(entity, 2, 116, 35));

        // 添加玩家背包槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 添加玩家快捷栏槽位
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
        }

        // 添加数据跟踪器
        addDataSlots(data);
    }

    public KimchiJarMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, getBlockEntity(inv, extraData));
    }

    private static KimchiJarBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf extraData) {
        BlockEntity entity = inv.player.level().getBlockEntity(extraData.readBlockPos());
        if (entity instanceof KimchiJarBlockEntity) {
            return (KimchiJarBlockEntity) entity;
        }
        throw new IllegalStateException("Block entity is not a KimchiJarBlockEntity!");
    }
    
    private KimchiJarMenu(int pContainerId, Inventory inv, KimchiJarBlockEntity entity) {
        this(pContainerId, inv, entity, entity.data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, KOR.KIMCHI_JAR_BLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 3) {
                // 从容器槽位移动到玩家背包
                if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                
                // 检查容器槽位是否为空
                if (blockEntity.getStackInSlot(0).isEmpty() || blockEntity.getStackInSlot(1).isEmpty()) {
                    // 当食物槽或燃料槽为空时，熄灭火焰
                    // 通过直接修改容器数据来熄灭火焰
                    blockEntity.resetFuel();
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                // 从玩家背包移动到输入槽位
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
    
    public int getScaledFuelProgress() {
        int fuelTime = this.data.get(2);
        int maxFuelTime = this.data.get(3);
        int fuelBurnSize = 24; // 熔炉使用24作为完整的燃料燃烧时间
        
        return maxFuelTime != 0 && fuelTime != 0 ? fuelTime * fuelBurnSize / maxFuelTime : 0;
    }

    public boolean isCrafting() {
        return this.data.get(0) > 0;
    }

    // 自定义槽位类，直接与区块实体交互
    private static class SimpleSlot extends Slot {
        private final KimchiJarBlockEntity blockEntity;
        private final int slotIndex;

        public SimpleSlot(KimchiJarBlockEntity blockEntity, int slotIndex, int x, int y) {
            super(new SimpleContainer(1), slotIndex, x, y);
            this.blockEntity = blockEntity;
            this.slotIndex = slotIndex;
        }

        @Override
        public ItemStack getItem() {
            return blockEntity.getStackInSlot(slotIndex);
        }

        @Override
        public void set(ItemStack stack) {
            blockEntity.setStackInSlot(slotIndex, stack);
        }

        @Override
        public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {
            blockEntity.setStackInSlot(slotIndex, newStack);
        }

        @Override
        public ItemStack remove(int amount) {
            return blockEntity.removeItem(slotIndex, amount);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // 可以添加物品放置规则
            return true;
        }
    }
}