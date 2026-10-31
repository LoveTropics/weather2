package weather2.ltcompat;

import net.minecraft.server.level.ServerLevel;
import weather2.LoveTropicsIntegration;
import weather2.datatypes.StormState;

public final class ServerWeatherIntegration {
    private ServerWeatherIntegration() {
    }

    public static ServerWeatherValues get() {
        return LoveTropicsIntegration.getServerWeatherValues();
    }

    public interface ServerWeatherValues {
        float getWindSpeed(ServerLevel level);

        StormState getSandstormForEverywhere(ServerLevel level);

        StormState getSnowstormForEverywhere(ServerLevel level);
    }
}
