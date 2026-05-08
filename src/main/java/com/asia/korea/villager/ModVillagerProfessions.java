package com.asia.korea.villager;

import com.asia.korea.KOR;
import com.asia.korea.KOR;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;

import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ImmutableSet;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = KOR.MODID)
public class ModVillagerProfessions {
    private static final Map<String, ProfessionPoiType> POI_TYPES = new HashMap<>();
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, KOR.MODID);
    
    // 注册泡菜师职业
    public static final RegistryObject<VillagerProfession> KIMCHI_MAKER = registerProfession(
        "kimchi_maker",
        () -> KOR.KIMCHI_JAR_BLOCK.get(),
        () -> net.minecraft.sounds.SoundEvents.VILLAGER_WORK_BUTCHER
    );

    private static RegistryObject<VillagerProfession> registerProfession(String name, Supplier<Block> block, Supplier<SoundEvent> soundEvent) {
        POI_TYPES.put(name, new ProfessionPoiType(block, null));
        return PROFESSIONS.register(name, () -> {
            Predicate<Holder<PoiType>> poiPredicate = poiTypeHolder -> 
                (POI_TYPES.get(name).poiType != null) && (poiTypeHolder.get() == POI_TYPES.get(name).poiType.get());
            
            return new VillagerProfession(
                KOR.MODID + ":" + name,
                poiPredicate,
                poiPredicate,
                ImmutableSet.of(),
                ImmutableSet.of(),
                soundEvent.get()
            );
        });
    }

    @SubscribeEvent
    public static void registerProfessionPointsOfInterest(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.POI_TYPES, registerHelper -> {
            for (Map.Entry<String, ProfessionPoiType> entry : POI_TYPES.entrySet()) {
                Block block = entry.getValue().block.get();
                String name = entry.getKey();
                
                // 检查方块是否已经被用作其他POI类型
                Optional<Holder<PoiType>> existingCheck = PoiTypes.forState(block.defaultBlockState());
                if (existingCheck.isPresent()) {
                    // 使用System.out.println代替LOGGER
                    System.out.println("WARNING: Skipping villager profession " + name + " that uses POI block " + block + " that is already in use by " + existingCheck);
                    continue;
                }
                
                // 创建新的POI类型
                PoiType poiType = new PoiType(
                    ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates()),
                    1,
                    1
                );
                
                // 注册POI类型
                registerHelper.register(name, poiType);
                
                // 获取POI类型的Holder
                entry.getValue().poiType = ForgeRegistries.POI_TYPES.getHolder(poiType).orElse(null);
            }
        });
    }

    private static class ProfessionPoiType {
        final Supplier<Block> block;
        Holder<PoiType> poiType;

        ProfessionPoiType(Supplier<Block> block, Holder<PoiType> poiType) {
            this.block = block;
            this.poiType = poiType;
        }
    }
}
