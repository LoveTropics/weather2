package weather2;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import weather2.weathersystem.storm.LightningBoltWeatherNew;

@Mod(Weather.MODID)
public class EntityRegistry {

    /*@ObjectHolder(Weather.MODID + ":lightning_bolt")
    public static EntityType<LightningBoltWeatherNew> lightning_bolt;*/

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Weather.MODID);

    public static void init(ModContainer container) {
        ENTITY_TYPES.register(container.getEventBus());
    }

    public static final DeferredHolder<EntityType<?>, EntityType<LightningBoltWeatherNew>> LIGHTNING_BOLT = ENTITY_TYPES.register("lightning_bolt", () -> EntityType.Builder
            .<LightningBoltWeatherNew>of(LightningBoltWeatherNew::new, MobCategory.MISC)
            .noSave()
            .sized(0.0F, 0.0F)
            .clientTrackingRange(16)
            .updateInterval(Integer.MAX_VALUE)
		.build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Weather.MODID, "gateway"))));
    /*
    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> e) {
        IForgeRegistry<EntityType<?>> r = e.getRegistry();
        r.register(
                EntityType.Builder.of(LightningBoltWeatherNew::new, MobCategory.MISC)
                        .noSave()
                        .sized(0.0F, 0.0F)
                        .clientTrackingRange(16)
                        .updateInterval(Integer.MAX_VALUE)
                        .build("lightning_bolt")
                        .setRegistryName("lightning_bolt"));
    }*/

}
