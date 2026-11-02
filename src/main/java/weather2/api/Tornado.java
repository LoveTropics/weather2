package weather2.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import weather2.ServerTickHandler;
import weather2.weathersystem.WeatherManagerServer;
import weather2.weathersystem.storm.StormObject;

import java.util.Optional;

public record Tornado(
        Optional<PlayerController> playerController,
        boolean isBaby,
        boolean isFireNado,
        Optional<NadoEntitySpawnSettings> entitySpawnSettings,
        int startStage,
        int maxStage,
        float speed,
        Optional<Vec3> spawnLocation,
        Optional<Vec3> aimLocation,
        Optional<Entity> entityTarget
) {

    public boolean spawnTornado(ServerLevel level) {
        WeatherManagerServer wm = ServerTickHandler.getWeatherManagerFor(level);
        if (wm == null) {
            return false;
        }

        StormObject stormObject = new StormObject(wm);
        if (playerController.isPresent()) {
            PlayerController pc = playerController.get();
            Player player = level.getPlayerByUUID(pc.player());
            if (player == null) {
                return false;
            }
            stormObject.setupStorm(player);
            stormObject.setupPlayerControlledTornado(player);
            stormObject.setPlayerControlledTimeLeft(pc.lengthInTicks());
        } else {
            stormObject.setupStorm(null);

            Vec3 targetLocation = aimLocation.orElse(stormObject.getLegacyTargetLocation());
            Vec3 locationToSpawn = spawnLocation.orElse(stormObject.getLegacySpawnLocation(targetLocation));
            stormObject.initPositions(locationToSpawn);
            stormObject.aimAtCoords(targetLocation);
            entityTarget.ifPresent(stormObject::setEntityTarget);
        }

        entitySpawnSettings.ifPresent(stormObject::setNadoEntitySpawnSettings);
        stormObject.setBaby(isBaby);
        stormObject.setLtSpeed(speed);
        stormObject.isFirenado = isFireNado;
        stormObject.levelCurIntensityStage = startStage;
        stormObject.levelStormIntensityMax = maxStage;

        wm.addStormObject(stormObject);
        wm.syncStormNew(stormObject);
        return true;
    }


}
