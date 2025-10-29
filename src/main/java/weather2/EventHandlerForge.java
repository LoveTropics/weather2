package weather2;

import com.corosus.coroutil.util.CULog;
import com.corosus.coroutil.util.CoroUtilCompatibility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import weather2.command.CommandWeather2Client;
import weather2.util.WeatherUtilBlock;

@EventBusSubscriber
public class EventHandlerForge {

	@SubscribeEvent
    public static void onEntityLivingUpdate(EntityTickEvent.Pre event) {
		Entity ent = event.getEntity();
		if (ent.level().isClientSide && (ent instanceof Player && ((Player) ent).isLocalPlayer())) {
            ClientEventHandler.onClientPlayerUpdate(event);
		}
        /*if (!ent.level.isClientSide && ent instanceof Player) {
            onServerPlayerUpdate(event);
        }*/
	}

	@SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
		CompoundTag tag = event.getOriginal().getPersistentData();
		CompoundTag tag2 = event.getEntity().getPersistentData();
		tag2.putLong("lastSandstormTime", tag.getLongOr("lastSandstormTime", 0));
		tag2.putLong("lastStormDeadlyTime", tag.getLongOr("lastStormDeadlyTime", 0));
	}

	public void onServerPlayerUpdate(EntityTickEvent.Pre event) {
		Level level = event.getEntity().level();
		if (level.getGameTime() % 40 == 0) {
			Entity ent = event.getEntity();
			Biome bgb = level.getBiome(WeatherUtilBlock.getPrecipitationHeightSafe(level, new BlockPos(Mth.floor(ent.position().x), 0, Mth.floor(ent.position().z)))).value();
			float biomeTemp = CoroUtilCompatibility.getAdjustedTemperature(ent.level(), bgb, new BlockPos(Mth.floor(ent.position().x), Mth.floor(ent.position().y), Mth.floor(ent.position().z)));
			CULog.dbg("biomeTemp: " + biomeTemp);
		}

	}

	@SubscribeEvent
    public static void registerCommandsClient(RegisterClientCommandsEvent event) {
		CommandWeather2Client.register(event.getDispatcher());
	}
}
