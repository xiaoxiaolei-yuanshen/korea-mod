package com.asia.korea.client.renderer;

import com.asia.korea.entity.NortheastRainSister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.VillagerModel;
import com.mojang.blaze3d.vertex.PoseStack;

public class NortheastRainSisterRenderer extends MobRenderer<NortheastRainSister, VillagerModel<NortheastRainSister>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("kor", "textures/entity/villager/type/yu_jie.png");
    
    public NortheastRainSisterRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    protected void scale(NortheastRainSister entity, PoseStack poseStack, float f) {
        poseStack.scale(1.2f, 1.2f, 1.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(NortheastRainSister entity) {
        return TEXTURE;
    }
}