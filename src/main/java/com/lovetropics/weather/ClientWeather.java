package com.lovetropics.weather;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.lovetropics.games.common.core.game.weather.PrecipitationType;
import org.lovetropics.games.common.core.game.weather.WeatherState;

public final class ClientWeather {
	private static ClientWeather instance = new ClientWeather();

	//10 seconds to transition precipitation fully
	private static final float LERP_RATE = 1F / 10F / 20F;

	private final WeatherState state = new WeatherState();

	private WeatherState lerpState = this.state;

	public ClientWeather() {

	}

	public static ClientWeather get() {
		return instance;
	}

	@SubscribeEvent
	public static void onWorldLoad(LevelEvent.Load event) {
		if (event.getLevel().isClientSide()) {
			reset();
		}
	}

	public static void reset() {
		instance = new ClientWeather();
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event) {
		instance.tick();
	}

	public void onUpdateWeather(WeatherState state) {
		this.lerpState = state;
		//System.out.println("state.rainAmount: " + state.rainAmount);
	}

	public void tick() {
		this.state.precipitationType = this.lerpState.precipitationType;
		this.state.heatwave = this.lerpState.heatwave;
		this.state.sandstorm = this.lerpState.sandstorm;
		this.state.snowstorm = this.lerpState.snowstorm;

		this.state.rainAmount = this.state.rainAmount + (this.state.rainAmount < this.lerpState.rainAmount ? LERP_RATE : -LERP_RATE);
		this.state.windSpeed = this.state.windSpeed + (this.state.windSpeed < this.lerpState.windSpeed ? LERP_RATE : -LERP_RATE);
	}

	public float getRainAmount() {
		return this.state.rainAmount;
	}

	public float getVanillaRainAmount() {
		//have vanilla rain get to max saturation by the time rainAmount hits 0.33, rest of range is used for extra particle effects elsewhere
		return Math.min(this.state.rainAmount * 3F, 1F);
	}

	public PrecipitationType getPrecipitationType() {
		return this.state.precipitationType;
	}

	public float getWindSpeed() {
		return this.state.windSpeed;
	}

	public boolean isHeatwave() {
		return this.state.heatwave;
	}

	public boolean isSandstorm() {
		return this.state.sandstorm != null;
	}

	public boolean isSnowstorm() {
		return this.state.snowstorm != null;
	}

	public boolean hasWeather() {
		return this.state.hasWeather();
	}

	public static Player getPlayer()
	{
		return Minecraft.getInstance().player;
	}
}
