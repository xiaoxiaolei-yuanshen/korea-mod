package com.asia.korea.blockentity;

import com.asia.korea.KOR;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, KOR.MODID);

    public static final RegistryObject<BlockEntityType<KimchiJarBlockEntity>> KIMCHI_JAR = BLOCK_ENTITIES.register("kimchi_jar",
            () -> BlockEntityType.Builder.of(KimchiJarBlockEntity::new, KOR.KIMCHI_JAR_BLOCK.get()).build(null));
}