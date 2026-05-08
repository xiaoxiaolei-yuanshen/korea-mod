package com.asia.korea.client.renderer;

import com.asia.korea.entity.NorthKoreanRefugee;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.VillagerModel;
import com.mojang.blaze3d.vertex.PoseStack;

public class NorthKoreanRefugeeRenderer extends MobRenderer<NorthKoreanRefugee, VillagerModel<NorthKoreanRefugee>> {
    public NorthKoreanRefugeeRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    protected void scale(NorthKoreanRefugee entity, PoseStack poseStack, float f) {
        // 使用村民的标准缩放
        poseStack.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    public ResourceLocation getTextureLocation(NorthKoreanRefugee entity) {
        // 使用村民的默认纹理
        return new ResourceLocation("textures/entity/villager/villager.png");
    }
}