package com.asia.korea.jei;

import com.asia.korea.KOR;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class KimchiJarRecipeCategory implements IRecipeCategory<KimchiJarRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(KOR.MODID, "textures/gui/kimchi_jar.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final int animationDuration = 500; // 15秒 = 300游戏刻

    public KimchiJarRecipeCategory(IGuiHelper guiHelper) {
        // 创建背景，截取GUI的配方显示部分
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, 176, 85);
        // 创建图标
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(KOR.KIMCHI_JAR_BLOCK.get()));
        // 设置标题
        this.title = Component.translatable("block.kor.kimchi_jar");
    }

    @Override
    public RecipeType<KimchiJarRecipe> getRecipeType() {
        return JEIKimchiPlugin.KIMCHI_JAR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KimchiJarRecipe recipe, IFocusGroup focuses) {
        // 添加输入槽1：食物槽 (x:56, y:17)
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 17)
                .addItemStack(recipe.getInput1());

        // 添加输入槽2：燃料槽 (x:56, y:53)
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 53)
                .addItemStack(recipe.getInput2());

        // 添加输出槽：主要产物 (x:116, y:35)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35)
                .addItemStack(recipe.getOutput());

        // 移除玻璃瓶副产物的显示，只保留酸菜配方的副产物（如果有的话）
        // 检查是否是酸菜配方（输出是酸菜且有副产物）
        boolean isSourCabbageRecipe = recipe.getOutput().getItem() == KOR.SOUR_CABBAGE.get();
        if (isSourCabbageRecipe && !recipe.getSecondaryOutput().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 35)
                    .addItemStack(recipe.getSecondaryOutput());
        }
    }

    @Override
    public void draw(KimchiJarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // 获取当前时间作为动画帧索引
        long currentTime = System.currentTimeMillis();
        
        // 绘制动态火焰
        drawFlame(guiGraphics, currentTime);
        
        // 绘制动态进度条
        drawProgressArrow(guiGraphics, currentTime);
    }

    /**
     * 绘制静态火焰效果
     */
    private void drawFlame(GuiGraphics guiGraphics, long currentTime) {
        // 直接使用静态火焰纹理（不使用动画）
        int flameX = 176;
        int flameY = 0; // 火焰的第一帧位置
        int flameWidth = 14;
        int flameHeight = 14;
        
        // 绘制静态火焰到指定的Y坐标位置
        guiGraphics.blit(BACKGROUND_TEXTURE, 56, 35, flameX, flameY, flameWidth, flameHeight);
    }

    /**
     * 绘制动态进度条
     */
    private void drawProgressArrow(GuiGraphics guiGraphics, long currentTime) {
        // 计算进度：每动画周期完成一次循环
        int progress = (int)((currentTime % animationDuration) * 24 / animationDuration);
        if (progress < 0) progress = 0;
        if (progress > 24) progress = 24;
        
        // 使用熔炉的进度条纹理坐标
        int arrowX = 176;
        int arrowY = 14; // 熔炉进度条在纹理中的位置
        int arrowHeight = 17;
        
        // 绘制进度条到正确位置
        guiGraphics.blit(BACKGROUND_TEXTURE, 80, 35, arrowX, arrowY, progress, arrowHeight);
    }
}