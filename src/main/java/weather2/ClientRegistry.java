package weather2;

import extendedrenderer.particle.entity.EntityRotFX;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import weather2.client.entity.model.AnemometerModel;
import weather2.client.entity.model.WindTurbineModel;
import weather2.client.entity.model.WindVaneModel;
import weather2.client.entity.render.LightningBoltWeatherNewRenderer;
import weather2.client.tile.AnemometerEntityRenderer;
import weather2.client.tile.WindTurbineEntityRenderer;
import weather2.client.tile.WindVaneEntityRenderer;
import weather2.util.WeatherUtilSound;

@EventBusSubscriber(Dist.CLIENT)
public class ClientRegistry {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        WeatherUtilSound.init();
        EntityRenderers.register(EntityRegistry.LIGHTNING_BOLT.get(), render -> new LightningBoltWeatherNewRenderer(render));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerBlockEntityRenderer(WeatherBlocks.BLOCK_ENTITY_ANEMOMETER.get(), AnemometerEntityRenderer::new);
        e.registerBlockEntityRenderer(WeatherBlocks.BLOCK_ENTITY_WIND_VANE.get(), WindVaneEntityRenderer::new);
        e.registerBlockEntityRenderer(WeatherBlocks.BLOCK_ENTITY_WIND_TURBINE.get(), WindTurbineEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WindVaneModel.LAYER_LOCATION, WindVaneModel::createBodyLayer);
        event.registerLayerDefinition(AnemometerModel.LAYER_LOCATION, AnemometerModel::createBodyLayer);
        event.registerLayerDefinition(WindTurbineModel.LAYER_LOCATION, WindTurbineModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerCustomPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(EntityRotFX.TRANSLUCENT_PARTICLE_NO_CULL_PIPELINE);
        event.registerPipeline(EntityRotFX.OPAQUE_PARTICLE_BLOCK_PIPELINE);
    }
}
