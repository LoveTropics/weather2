package com.lovetropics.weather;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.lovetropics.games.common.core.game.weather.WeatherControllerManager;

public class LTWeather
{
    public static boolean initProperNeededForWorld = true;

    public void init(ModContainer modContainer) {
        // Register the setup method for modloading
        modContainer.getEventBus().addListener(this::setup);
        NeoForge.EVENT_BUS.addListener(this::serverStop);
        modContainer.getEventBus().addListener(this::registerPackets);

        if (FMLEnvironment.getDist().isClient()) {
            NeoForge.EVENT_BUS.register(ClientWeather.class);
        }

        NeoForge.EVENT_BUS.register(this);
    }

    public void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToClient(PacketWeatherStateFromServer.TYPE, PacketWeatherStateFromServer.STREAM_CODEC, (packet, ctx) ->
                ctx.enqueueWork(() -> packet.handle(ClientWeather.getPlayer())));
    }

    private void setup(final FMLCommonSetupEvent event) {
        WeatherControllerManager.setFactory(ServerWeatherController::new);
    }


    @SubscribeEvent
    public void serverStop(ServerStoppedEvent event) {
        initProperNeededForWorld = true;
    }
}
