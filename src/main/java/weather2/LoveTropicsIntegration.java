package weather2;

import com.lovetropics.weather.ClientWeather;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.lovetropics.games.common.core.game.weather.WeatherController;
import org.lovetropics.games.common.core.game.weather.WeatherControllerManager;
import weather2.datatypes.PrecipitationType;
import weather2.datatypes.StormState;
import weather2.ltcompat.ClientWeatherIntegration;
import weather2.ltcompat.ServerWeatherIntegration;

public class LoveTropicsIntegration {

    public static final Identifier HAMMERHEAD_SHARK = Identifier.fromNamespaceAndPath("tropicraft", "hammerhead");
    public static final DeferredHolder<EntityType<?>, EntityType<?>> HAMMER_HEAD_SHARK = DeferredHolder.create(Registries.ENTITY_TYPE, HAMMERHEAD_SHARK);

    public static final class LoveTropicsClientWeatherValues implements ClientWeatherIntegration.ClientWeatherValues {
        public static final ClientWeatherIntegration.ClientWeatherValues INSTANCE = new LoveTropicsClientWeatherValues();

        private LoveTropicsClientWeatherValues() {
        }

        public float getRainAmount() {
            return ClientWeather.get().getRainAmount();
        }

        public float getVanillaRainAmount() {
            return ClientWeather.get().getVanillaRainAmount();
        }

        public PrecipitationType getPrecipitationType() {
            return PrecipitationType.VALUES[ClientWeather.get().getPrecipitationType().ordinal()];
        }

        public float getWindSpeed() {
            return ClientWeather.get().getWindSpeed();
        }

        public boolean isHeatwave() {
            return ClientWeather.get().isHeatwave();
        }

        public boolean isSandstorm() {
            return ClientWeather.get().isSandstorm();
        }

        public boolean isSnowstorm() {
            return ClientWeather.get().isSnowstorm();
        }

        public boolean hasWeather() {
            return ClientWeather.get().hasWeather();
        }
    }

    public static final class LoveTropicsServerWeatherValues implements ServerWeatherIntegration.ServerWeatherValues {
        public static final LoveTropicsServerWeatherValues INSTANCE = new LoveTropicsServerWeatherValues();

        private LoveTropicsServerWeatherValues() {
        }

        @Override
        public float getWindSpeed(ServerLevel level) {
            WeatherController weatherController = WeatherControllerManager.forWorld(level);
            return weatherController.getWindSpeed();
        }

        @Override
        public StormState getSandstormForEverywhere(ServerLevel level) {
            WeatherController controller = WeatherControllerManager.forWorld(level);
            Pair<Integer, Integer> data = controller != null && controller.getSandstorm() != null ? new Pair<>(controller.getSandstorm().buildupTickRate(), controller.getSandstorm().maxStackable()) : null;
            return data != null ? new StormState(data.getFirst(), data.getSecond()) : null;
        }

        @Override
        public StormState getSnowstormForEverywhere(ServerLevel level) {
            WeatherController controller = WeatherControllerManager.forWorld(level);
            Pair<Integer, Integer> data = controller != null && controller.getSnowstorm() != null ? new Pair<>(controller.getSnowstorm().buildupTickRate(), controller.getSnowstorm().maxStackable()) : null;
            return data != null ? new StormState(data.getFirst(), data.getSecond()) : null;
        }
    }

    public static ServerWeatherIntegration.ServerWeatherValues getServerWeatherValues() {
        return LoveTropicsServerWeatherValues.INSTANCE;
    }

    public static ClientWeatherIntegration.ClientWeatherValues getClientWeatherValues() {
        return LoveTropicsClientWeatherValues.INSTANCE;
    }

    public static EntityType<?> getSharkEntityType() {
        return HAMMER_HEAD_SHARK.isBound() ? HAMMER_HEAD_SHARK.value() : EntityTypes.DOLPHIN;
    }
}
