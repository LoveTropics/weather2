package weather2.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import weather2.ServerTickHandler;
import weather2.weathersystem.WeatherManagerServer;
import weather2.weathersystem.storm.StormObject;

import java.util.Optional;

public class TornadoAPI {

    public static boolean spawnTornado(ServerLevel level, Tornado tornado) {
        WeatherManagerServer wm = ServerTickHandler.getWeatherManagerFor(level);
        if (wm == null) {
            return false;
        }

        StormObject stormObject = new StormObject(wm);
        Optional<PlayerController> playerController = tornado.playerController();
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
            stormObject.setupTornadoAwayFromPlayersAimAtPlayers();
        }

        tornado.entitySpawnSettings().ifPresent(stormObject::setNadoEntitySpawnSettings);
        stormObject.setBaby(tornado.isBaby());
        stormObject.isFirenado = tornado.isFireNado();
        stormObject.levelCurIntensityStage = tornado.startStage();
        stormObject.levelStormIntensityMax = tornado.maxStage();

        wm.addStormObject(stormObject);
        wm.syncStormNew(stormObject);
        return true;
    }
}
