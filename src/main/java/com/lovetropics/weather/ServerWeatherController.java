package com.lovetropics.weather;

import org.lovetropics.games.common.core.game.weather.PrecipitationType;
import org.lovetropics.games.common.core.game.weather.StormState;
import org.lovetropics.games.common.core.game.weather.WeatherController;
import org.lovetropics.games.common.core.game.weather.WeatherState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;


public final class ServerWeatherController implements WeatherController {
	public static final int UPDATE_INTERVAL = 20;

	private final ServerLevel level;

	private int ticks;
	private boolean dirty;

	private final WeatherState state = new WeatherState();

	public ServerWeatherController(ServerLevel world) {
		this.level = world;
	}

	@Override
	public void onPlayerJoin(ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new PacketWeatherStateFromServer(state));
	}

	@Override
	public void tick() {
		if (this.dirty && this.ticks++ % UPDATE_INTERVAL == 0) {
			this.dirty = false;
			PacketDistributor.sendToPlayersInDimension(level, new PacketWeatherStateFromServer(state));
		}
	}

	@Override
	public void setRain(float amount, PrecipitationType type) {
		if (amount != this.state.rainAmount || type != this.state.precipitationType) {
			this.state.rainAmount = amount;
			this.state.precipitationType = type;
			this.dirty = true;
		}
	}

	@Override
	public void setWind(float speed) {
		if (speed != this.state.windSpeed) {
			this.state.windSpeed = speed;
			this.dirty = true;
		}
	}

	@Override
	public void setHeatwave(boolean heatwave) {
		if (heatwave != this.state.heatwave) {
			this.state.heatwave = heatwave;
			this.dirty = true;
		}
	}

	@Override
	public void setSandstorm(int buildupTickRate, int maxStackable) {
		this.state.sandstorm = new StormState(buildupTickRate, maxStackable);
		this.dirty = true;
	}

	@Override
	public void clearSandstorm() {
		if (this.state.sandstorm != null) {
			this.state.sandstorm = null;
			this.dirty = true;
		}
	}

	@Override
	public void setSnowstorm(int buildupTickRate, int maxStackable) {
		this.state.snowstorm = new StormState(buildupTickRate, maxStackable);
		this.dirty = true;
	}

	@Override
	public void clearSnowstorm() {
		if (this.state.snowstorm != null) {
			this.state.snowstorm = null;
			this.dirty = true;
		}
	}

	@Override
	public float getRainAmount() {
		return this.state.rainAmount;
	}

	@Override
	public PrecipitationType getPrecipitationType() {
		return this.state.precipitationType;
	}

	@Override
	public float getWindSpeed() {
		return this.state.windSpeed;
	}

	@Override
	public boolean isHeatwave() {
		return this.state.heatwave;
	}

	@Nullable
	@Override
	public StormState getSandstorm() {
		return this.state.sandstorm;
	}

	@Nullable
	@Override
	public StormState getSnowstorm() {
		return this.state.snowstorm;
	}
}
