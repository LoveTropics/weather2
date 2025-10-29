package weather2.weathersystem.wind;

import com.corosus.coroutil.util.CoroUtilBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import weather2.weathersystem.WeatherManager;
import weather2.weathersystem.storm.StormObject;

public class WindManagerClient extends WindManager {
    public WindManagerClient(WeatherManager parManager) {
        super(parManager);
    }

    public void tickClient() {
		Player entP = Minecraft.getInstance().player;

		if (windTimeEvent > 0) {
			windTimeEvent--;
			if (windTimeEvent == 0) {
				windTimeGust = 0;
			}
		}

		//event data
		if (entP != null) {

			if (entP != null && entP.level().getGameTime() % 5 == 0) {
				cachedWindSpeedClient = getWindSpeedPositional(entP.blockPosition(), 1, false);
			}

			if (manager.getWorld().getGameTime() % 20 == 0) {
				float maxDist = 512;
				StormObject so = manager.getClosestStorm(new Vec3(entP.getX(), StormObject.layers.get(0), entP.getZ()), maxDist, StormObject.STATE_HIGHWIND);

				if (so != null) {

					windOriginEvent = CoroUtilBlock.blockPos(so.posGround.x, so.posGround.y, so.posGround.z);

					setWindTimeEvent(80);

					//player pos aiming at storm
					double var11 = so.posGround.x - entP.getX();
					double var15 = so.posGround.z - entP.getZ();
					float yaw = -((float) Math.atan2(var11, var15)) * 180.0F / (float) Math.PI;

					windAngleEvent = yaw;
					double dist = entP.position().distanceTo(so.posGround);
					windSpeedEvent = getEventSpeedFactor(dist, maxDist);
				}
			}
		}
	}
}
