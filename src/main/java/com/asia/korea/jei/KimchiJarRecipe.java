package com.asia.korea.jei;

import net.minecraft.world.item.ItemStack;

public class KimchiJarRecipe {
    private final ItemStack input1;
    private final ItemStack input2;
    private final ItemStack output;
    private final ItemStack secondaryOutput;

    public KimchiJarRecipe(ItemStack input1, ItemStack input2, ItemStack output, ItemStack secondaryOutput) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
    }

    public ItemStack getInput1() {
        return input1;
    }

    public ItemStack getInput2() {
        return input2;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput;
    }
}