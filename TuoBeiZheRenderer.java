
package net.star_rail.ccb.client.renderer;

import net.star_rail.ccb.entity.TuoBeiZheEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.VillagerModel;

import com.mojang.blaze3d.vertex.PoseStack;

public class TuoBeiZheRenderer extends MobRenderer<TuoBeiZheEntity, VillagerModel<TuoBeiZheEntity>> {
	public TuoBeiZheRenderer(EntityRendererProvider.Context context) {
		super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
	}

	@Override
	protected void scale(TuoBeiZheEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(0.9375f, 0.9375f, 0.9375f);
	}

	@Override
	public ResourceLocation getTextureLocation(TuoBeiZheEntity entity) {
		return new ResourceLocation("ccb:textures/entities/villager.png");
	}
}
