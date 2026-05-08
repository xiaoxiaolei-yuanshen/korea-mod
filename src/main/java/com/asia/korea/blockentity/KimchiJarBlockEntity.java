package com.asia.korea.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.asia.korea.KOR;
import com.asia.korea.menu.KimchiJarMenu;
import org.jetbrains.annotations.Nullable;

public class KimchiJarBlockEntity extends BlockEntity implements MenuProvider {
    // 使用ItemStack数组直接管理物品槽位
    private final ItemStack[] inventory = new ItemStack[3];
    public final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100; // 5秒 = 100游戏刻
    // 添加燃料时间支持
    private int fuelTime = 0;
    private int maxFuelTime = 0;

    public KimchiJarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntities.KIMCHI_JAR.get(), pPos, pBlockState);
        // 初始化物品槽位为空
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = ItemStack.EMPTY;
        }
        
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> KimchiJarBlockEntity.this.progress;
                    case 1 -> KimchiJarBlockEntity.this.maxProgress;
                    case 2 -> KimchiJarBlockEntity.this.fuelTime;
                    case 3 -> KimchiJarBlockEntity.this.maxFuelTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> KimchiJarBlockEntity.this.progress = pValue;
                    case 1 -> KimchiJarBlockEntity.this.maxProgress = pValue;
                    case 2 -> KimchiJarBlockEntity.this.fuelTime = pValue;
                    case 3 -> KimchiJarBlockEntity.this.maxFuelTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.kor.kimchi_jar");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new KimchiJarMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    // 基本的物品槽位访问方法
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        inventory[slot] = stack;
        setChanged();
    }

    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ItemStack.EMPTY;
        if (!inventory[slot].isEmpty()) {
            result = inventory[slot].split(amount);
            if (inventory[slot].isEmpty()) {
                inventory[slot] = ItemStack.EMPTY;
            }
            setChanged();
            
            // 当食物槽（第一个槽位）或燃料槽（第二个槽位）为空时，熄灭火焰
            if (inventory[0].isEmpty() || inventory[1].isEmpty()) {
                resetFuel();
            }
        }
        return result;
    }
    
    public void resetFuel() {
        this.fuelTime = 0;
        this.maxFuelTime = 0;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        // 保存每个物品槽位
        for (int i = 0; i < inventory.length; i++) {
            if (!inventory[i].isEmpty()) {
                tag.put("item" + i, inventory[i].serializeNBT());
            }
        }
        tag.putInt("kimchi_jar.progress", progress);
        tag.putInt("kimchi_jar.fuelTime", fuelTime);
        tag.putInt("kimchi_jar.maxFuelTime", maxFuelTime);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // 加载每个物品槽位
        for (int i = 0; i < inventory.length; i++) {
            if (tag.contains("item" + i)) {
                inventory[i] = ItemStack.of(tag.getCompound("item" + i));
            } else {
                inventory[i] = ItemStack.EMPTY;
            }
        }
        progress = tag.getInt("kimchi_jar.progress");
        fuelTime = tag.getInt("kimchi_jar.fuelTime");
        maxFuelTime = tag.getInt("kimchi_jar.maxFuelTime");
    }

    public void drops() {
        SimpleContainer simpleContainer = new SimpleContainer(inventory.length);
        for (int i = 0; i < inventory.length; i++) {
            simpleContainer.setItem(i, inventory[i]);
        }
        Containers.dropContents(this.level, this.worldPosition, simpleContainer);
    }

    public void craftItem() {
        if (hasRecipe()) {
            // 自动设置燃料时间，不需要消耗额外的燃料
            if (fuelTime == 0) {
                maxFuelTime = maxProgress; // 燃料时间等于制作时间
                fuelTime = maxFuelTime;
            }
            
            increaseCraftingProgress();
            decreaseFuelTime();
            setChanged();

            if (hasProgressFinished()) {
                // 确保craft方法被调用
                craft();
                // 重置进度和燃料
                resetProgress();
                resetFuel();
            }
        } else {
            resetProgress();
            resetFuel();
        }
    }
    
    // 不再需要hasFuel方法，因为我们现在自动设置燃料时间
    
    private void decreaseFuelTime() {
        fuelTime--;
    }

    private void craft() {
        // 检查输入物品是否正确
        if (inventory[0].getItem() == Items.SEA_PICKLE && inventory[1].getItem() == KOR.HOT_SAUCE.get()) {
            // 消耗一个海泡菜
            if (inventory[0].getCount() > 1) {
                inventory[0].shrink(1);
            } else {
                inventory[0] = ItemStack.EMPTY;
            }
            
            // 消耗一个辣酱
            if (inventory[1].getCount() > 1) {
                inventory[1].shrink(1);
            } else {
                inventory[1] = ItemStack.EMPTY;
            }
            
            // 生产一个泡菜
            if (inventory[2].isEmpty()) {
                inventory[2] = new ItemStack(KOR.SPICY_KIMCHI.get());
            } else {
                // 如果槽位已有物品，尝试合并
                ItemStack kimchiStack = new ItemStack(KOR.SPICY_KIMCHI.get());
                if (ItemStack.isSameItemSameTags(inventory[2], kimchiStack)) {
                    inventory[2].grow(1);
                } else {
                    // 如果输出槽被其他物品占据，尝试放入燃料槽
                    if (inventory[1].isEmpty()) {
                        inventory[1] = new ItemStack(KOR.SPICY_KIMCHI.get());
                    }
                }
            }
            
            // 总是返还空瓶子，不管是什么模式
            // 返还一个空瓶子
            if (inventory[1].isEmpty()) {
                inventory[1] = new ItemStack(Items.GLASS_BOTTLE);
            } else {
                // 如果槽位已有物品，尝试合并
                ItemStack bottleStack = new ItemStack(Items.GLASS_BOTTLE);
                if (ItemStack.isSameItemSameTags(inventory[1], bottleStack)) {
                    inventory[1].grow(1);
                } else {
                    // 如果燃料槽有其他物品，尝试放入食物槽
                    if (inventory[0].isEmpty()) {
                        inventory[0] = new ItemStack(Items.GLASS_BOTTLE);
                    }
                    // 注意：如果所有槽位都已满，瓶子将不会被添加，但泡菜会正常生成
                }
            }
            
            setChanged();
        } else if (inventory[0].getItem() == KOR.SPICY_KIMCHI.get() && inventory[1].getItem() == Items.ROTTEN_FLESH) {
            // 消耗一个泡菜
            if (inventory[0].getCount() > 1) {
                inventory[0].shrink(1);
            } else {
                inventory[0] = ItemStack.EMPTY;
            }
            
            // 消耗一个腐肉
            if (inventory[1].getCount() > 1) {
                inventory[1].shrink(1);
            } else {
                inventory[1] = ItemStack.EMPTY;
            }
            
            // 生产一个酸菜
            if (inventory[2].isEmpty()) {
                inventory[2] = new ItemStack(KOR.SOUR_CABBAGE.get());
            } else {
                // 如果槽位已有物品，尝试合并
                ItemStack cabbageStack = new ItemStack(KOR.SOUR_CABBAGE.get());
                if (ItemStack.isSameItemSameTags(inventory[2], cabbageStack)) {
                    inventory[2].grow(1);
                } else {
                    // 如果输出槽被其他物品占据，尝试放入燃料槽
                    if (inventory[1].isEmpty()) {
                        inventory[1] = new ItemStack(KOR.SOUR_CABBAGE.get());
                    }
                }
            }
            
            setChanged();
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean hasProgressFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        // 检查是否有正确的配方：海泡菜+辣酱 或 泡菜+腐肉
        return (inventory[0].getItem() == Items.SEA_PICKLE && inventory[0].getCount() >= 1 && 
               inventory[1].getItem() == KOR.HOT_SAUCE.get() && inventory[1].getCount() >= 1) ||
               (inventory[0].getItem() == KOR.SPICY_KIMCHI.get() && inventory[0].getCount() >= 1 && 
               inventory[1].getItem() == Items.ROTTEN_FLESH && inventory[1].getCount() >= 1);
    }
}