package weather2;

import com.lovetropics.weather.ClientWeather;
import com.lovetropics.weather.TypeBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.tropicraft.core.common.entity.underdasea.SharkEntity;
import weather2.datatypes.PrecipitationType;
import weather2.datatypes.StormState;
import weather2.ltcompat.ClientWeatherIntegration;
import weather2.ltcompat.ServerWeatherIntegration;

public class LoveTropicsIntegration {
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
            return PrecipitationType.VALUES[TypeBridge.getPrecipitationTypeOrdinal(ClientWeather.get())];
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
            return TypeBridge.getWindSpeed(level);
        }

        @Override
        public StormState getSandstormForEverywhere(ServerLevel level) {
            Tuple<Integer, Integer> data = TypeBridge.getSandstormData(level);
            return data != null ? new StormState(data.getA(), data.getB()) : null;
        }

        @Override
        public StormState getSnowstormForEverywhere(ServerLevel level) {
            Tuple<Integer, Integer> data = TypeBridge.getSnowstormData(level);
            return data != null ? new StormState(data.getA(), data.getB()) : null;
        }
    }

    public static ServerWeatherIntegration.ServerWeatherValues getServerWeatherValues() {
        return LoveTropicsServerWeatherValues.INSTANCE;
    }

    public static ClientWeatherIntegration.ClientWeatherValues getClientWeatherValues() {
        return LoveTropicsClientWeatherValues.INSTANCE;
    }

    public static boolean isShark(Entity entity) {
        return entity instanceof SharkEntity;
    }
}
