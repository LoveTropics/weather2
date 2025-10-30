package weather2.ltcompat;

import weather2.LoveTropicsIntegration;
import weather2.Weather;
import weather2.datatypes.PrecipitationType;

public final class ClientWeatherIntegration {
    private static ClientWeatherValues instance;

    private ClientWeatherIntegration() {
    }

    public static ClientWeatherValues get() {
        if (instance == null) {
            if (Weather.isLoveTropicsWeatherInstalled()) {
                instance = LoveTropicsIntegration.getClientWeatherValues();
            } else {
                instance = ClientWeatherValues.DEFAULT;
            }
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public interface ClientWeatherValues {
        ClientWeatherValues DEFAULT = new ClientWeatherValues() {
            @Override
            public float getRainAmount() {
                return 0;
            }

            @Override
            public float getVanillaRainAmount() {
                return 0;
            }

            @Override
            public PrecipitationType getPrecipitationType() {
                return PrecipitationType.VALUES[0];
            }

            @Override
            public float getWindSpeed() {
                return 0;
            }

            @Override
            public boolean isHeatwave() {
                return false;
            }

            @Override
            public boolean isSandstorm() {
                return false;
            }

            @Override
            public boolean isSnowstorm() {
                return false;
            }

            @Override
            public boolean hasWeather() {
                return false;
            }
        };

        float getRainAmount();

        float getVanillaRainAmount();

        PrecipitationType getPrecipitationType();

        float getWindSpeed();

        boolean isHeatwave();

        boolean isSandstorm();

        boolean isSnowstorm();

        boolean hasWeather();
    }
}
