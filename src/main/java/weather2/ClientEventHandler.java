package weather2;

import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import weather2.client.SceneEnhancer;
import weather2.config.ConfigDebug;
import weather2.util.WeatherUtilEntity;
import weather2.weathersystem.WeatherManagerClient;
import weather2.weathersystem.wind.WindManager;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void worldRenderAfterWeather(RenderLevelStageEvent.AfterWeather event) {
        ClientTickHandler.getClientWeather();
    }

    @SubscribeEvent
    public static void worldRenderAfterParticles(RenderLevelStageEvent.AfterParticles event) {
        if (ConfigDebug.Particle_engine_render) {
            //System.out.println("dsf " + event.getPartialTick().getGameTimeDeltaTicks());
            ClientTickHandler.particleManagerExtended().render(event.getCamera(), event.getPartialTick().getGameTimeDeltaTicks(), event.getLevelRenderer().renderBuffers.bufferSource(), event.getFrustum(), type -> true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFogColors(ViewportEvent.ComputeFogColor event) {
        SceneEnhancer.getFogAdjuster().onFogColors(event);

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFogRender(ViewportEvent.RenderFog event) {
        SceneEnhancer.getFogAdjuster().onFogRender(event);
    }

    @SubscribeEvent
    public static void renderTick(RenderLevelStageEvent.AfterLevel event) {
        //TODO: 1.21 verify this is good enough instead of RenderTickEvent via forge
        SceneEnhancer.renderTick(event);
    }

    public static void onClientPlayerUpdate(EntityTickEvent.Pre event) {

        Entity ent = event.getEntity();
        WeatherManagerClient weatherMan = ClientTickHandler.weatherManager;
        if (weatherMan == null) return;
        WindManager windMan = weatherMan.getWindManager();
        if (windMan == null) return;

        ClientWeatherProxy weather = ClientWeatherProxy.get();
        if (weather.isSnowstorm() || weather.isSandstorm()) {
            if (ent.onGround() && !ent.isSpectator() && !WeatherUtilEntity.isPlayerSheltered(ent)/* && ent.world.getGameTime() % 20 == 0*/) {

                float playerSpeed = (float) Math.sqrt(ent.getDeltaMovement().x * ent.getDeltaMovement().x + ent.getDeltaMovement().z * ent.getDeltaMovement().z);

                if (playerSpeed > 0.02F && playerSpeed < 0.3F) {

                    //System.out.println("playerSpeed: " + playerSpeed);

                    /**
                     * Calculate the players angle from motion, compare it against wind
                     * under 90 means theyre moving with the wind, above 90 means against the wind, 90 means perpendicular to it
                     * scale wind assistance / resistance to wind based on dist from 0 to 90 or 90 to 180
                     */

                    float playerAngle = -(float) (Math.toDegrees(Math.atan2(ent.getDeltaMovement().x, ent.getDeltaMovement().z)));
                    int phi = (int) (Math.abs(windMan.getWindAngle(ent.position()) - playerAngle) % 360);
                    float diffAngle = phi > 180 ? 360 - phi : phi;
                    //System.out.println("diffAngle: " + diffAngle);
                    if (diffAngle < 90) {
                        float assistRate = 1F - (diffAngle / 90F);
                        float assist = 1F + (0.12F * assistRate);
                        //System.out.println("assist: " + assist);
                        ent.setDeltaMovement(ent.getDeltaMovement().x * assist, ent.getDeltaMovement().y, ent.getDeltaMovement().z * assist);
                    } else if (diffAngle >= 90) {
                        float dampenRate = ((diffAngle - 90F) / 90F);
                        float dampen = 1F - (0.12F * dampenRate);
                        //System.out.println("dampen: " + dampen);
                        if (dampen != 0) {
                            ent.setDeltaMovement(ent.getDeltaMovement().x * dampen, ent.getDeltaMovement().y, ent.getDeltaMovement().z * dampen);
                        }
                    }
                }
            }
        }
    }
}
