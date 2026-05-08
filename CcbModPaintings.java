
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.star_rail.ccb.init;

import net.star_rail.ccb.CcbMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.entity.decoration.PaintingVariant;

public class CcbModPaintings {
	public static final DeferredRegister<PaintingVariant> REGISTRY = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, CcbMod.MODID);
	public static final RegistryObject<PaintingVariant> JIANGJUNHUA = REGISTRY.register("jiangjunhua", () -> new PaintingVariant(16, 16));
}
