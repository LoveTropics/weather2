package weather2.ltcompat;

import net.minecraft.server.level.ServerLevel;
import weather2.LoveTropicsIntegration;
import weather2.Weather;
import weather2.datatypes.StormState;

public final class ServerWeatherIntegration {
    private static ServerWeatherValues instance;

    private ServerWeatherIntegration() {
    }

    public static ServerWeatherValues get() {
        if (instance == null) {
            if (Weather.isLoveTropicsWeatherInstalled()) {
                instance = LoveTropicsIntegration.getServerWeatherValues();
            } else {
                instance = ServerWeatherValues.DEFAULT;
            }
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public interface ServerWeatherValues {
        ServerWeatherValues DEFAULT = new ServerWeatherValues() {
            @Override
            public float getWindSpeed(ServerLevel level) {
                return 0;
            }

            @Override
            public StormState getSandstormForEverywhere(ServerLevel level) {
                return null;
            }

            @Override
            public StormState getSnowstormForEverywhere(ServerLevel level) {
                return null;
            }
        };

        float getWindSpeed(ServerLevel level);

        StormState getSandstormForEverywhere(ServerLevel level);

        StormState getSnowstormForEverywhere(ServerLevel level);
    }
}
