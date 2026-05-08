package com.asia.korea.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.asia.korea.entity.PurpleEggEntity;

@OnlyIn(Dist.CLIENT)
public class PurpleEggRenderer extends ThrownItemRenderer<PurpleEggEntity> {
    public PurpleEggRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
