package weather2.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import weather2.ClientTickHandler;
import weather2.ServerTickHandler;
import weather2.weathersystem.WeatherManager;

public class WindReader {
    public static float getWindAngle(Level world) {
        return getWindAngle(world, null);
    }

    public static float getWindAngle(Level world, Vec3 pos) {
        WeatherManager weather = getWeatherManagerFor(world);
        return weather != null ? weather.getWindManager().getWindAngle(pos) : 0;
    }

    public static float getWindSpeed(Level world) {
        return getWindSpeed(world, null);
    }

    public static float getWindSpeed(Level world, @Nullable BlockPos pos) {
        return getWindSpeed(world, pos, 1);
    }

    public static float getWindSpeed(Level world, @Nullable BlockPos pos, float extraHeightAmpMax) {
        WeatherManager weather = getWeatherManagerFor(world);
        return weather != null ? weather.getWindManager().getWindSpeed(pos, extraHeightAmpMax) : 0;
    }

    public static float getWindSpeedCached(Level world, @Nullable BlockPos pos, float extraHeightAmpMax) {
        WeatherManager weather = getWeatherManagerFor(world);
        return weather != null ? weather.getWindManager().getCachedWindSpeedForHeight(pos, extraHeightAmpMax) : 0;
    }

    public static WeatherManager getWeatherManagerFor(Level world) {
        if (world.isClientSide()) {
            return getWeatherManagerClient();
        } else {
            return ServerTickHandler.getWeatherManagerFor((world.dimension()));
        }
    }

    private static WeatherManager getWeatherManagerClient() {
        return ClientTickHandler.weatherManager;
    }
}
