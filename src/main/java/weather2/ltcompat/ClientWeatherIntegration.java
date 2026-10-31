package weather2.ltcompat;

import weather2.LoveTropicsIntegration;
import weather2.datatypes.PrecipitationType;

public final class ClientWeatherIntegration {
    private ClientWeatherIntegration() {
    }

    public static ClientWeatherValues get() {
        return LoveTropicsIntegration.getClientWeatherValues();
    }

    public interface ClientWeatherValues {
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
