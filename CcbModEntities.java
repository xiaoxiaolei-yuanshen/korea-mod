
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.star_rail.ccb.init;

import net.star_rail.ccb.entity.Kannisi999Entity;
import net.star_rail.ccb.CcbMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CcbModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CcbMod.MODID);
	public static final RegistryObject<EntityType<Kannisi999Entity>> KANNISI_999 = register("kannisi_999",
			EntityType.Builder.<Kannisi999Entity>of(Kannisi999Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(Kannisi999Entity::new)

					.sized(0.4f, 1.8f));
	public static final RegistryObject<EntityType<TuoBeiZheEntity>> TUO_BEI_ZHE = register("tuo_bei_zhe",
			EntityType.Builder.<TuoBeiZheEntity>of(TuoBeiZheEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TuoBeiZheEntity::new)

					.sized(0.6f, 1.95f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			Kannisi999Entity.init();
			TuoBeiZheEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(KANNISI_999.get(), Kannisi999Entity.createAttributes().build());
		event.put(TUO_BEI_ZHE.get(), TuoBeiZheEntity.createAttributes().build());
	}
}
