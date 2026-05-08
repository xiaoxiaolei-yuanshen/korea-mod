package com.asia.korea.init;

import com.asia.korea.KOR;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class Paintings {
    public static final DeferredRegister<PaintingVariant> REGISTRY = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, KOR.MODID);
    
    // 注册将军画像（1x1尺寸，16x16像素）
    public static final RegistryObject<PaintingVariant> DAJIANGJUNHUA = REGISTRY.register("dajiangjunhua", () -> new PaintingVariant(16, 16));
    public static final RegistryObject<PaintingVariant> ERJIANGJUNHUA = REGISTRY.register("erjiangjunhua", () -> new PaintingVariant(16, 16));
    public static final RegistryObject<PaintingVariant> SANJIANGJUNHUA = REGISTRY.register("sanjiangjunhua", () -> new PaintingVariant(16, 16));
}