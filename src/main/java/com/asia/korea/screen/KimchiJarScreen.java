package com.asia.korea.screen;

import com.asia.korea.menu.KimchiJarMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KimchiJarScreen extends AbstractContainerScreen<KimchiJarMenu> {
    // 使用用户自己的纹理，它是基于熔炉纹理修改的
    private static final ResourceLocation TEXTURE = new ResourceLocation("kor", "textures/gui/kimchi_jar.png");

    public KimchiJarScreen(KimchiJarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // 渲染GUI背景
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // 只有当有燃料时才渲染火焰
        int fuelProgress = menu.getScaledFuelProgress();
        if (fuelProgress > 0) {
            guiGraphics.blit(TEXTURE, x + 56, y + 36 + 12 - fuelProgress / 2, 176, 12 - fuelProgress / 2, 14, fuelProgress / 2 + 1);
        }
        
        // 渲染进度条 - 完全复制熔炉的实现
        int progress = menu.getScaledProgress();
        guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 14, progress + 1, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        // 渲染标题和背包标签
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752);
    }
}