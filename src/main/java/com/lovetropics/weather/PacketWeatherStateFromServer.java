package com.lovetropics.weather;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.lovetropics.games.common.core.game.weather.WeatherState;
import weather2.Weather;

public record PacketWeatherStateFromServer(WeatherState weatherState) implements CustomPacketPayload {
    public static final Type<PacketWeatherStateFromServer> TYPE = new Type<>(Weather.id("weather_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketWeatherStateFromServer> STREAM_CODEC = StreamCodec.ofMember(PacketWeatherStateFromServer::serialize, PacketWeatherStateFromServer::deserialize);

    private void serialize(RegistryFriendlyByteBuf buffer) {
        weatherState.serialize(buffer);
    }

    private static PacketWeatherStateFromServer deserialize(RegistryFriendlyByteBuf buffer) {
        WeatherState weather = new WeatherState();
        weather.deserialize(buffer);
        return new PacketWeatherStateFromServer(weather);
    }

    public void handle(Player player)
    {
        try {
            ClientWeather.get().onUpdateWeather(weatherState);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
