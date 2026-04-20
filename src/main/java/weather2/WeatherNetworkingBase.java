package weather2;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class WeatherNetworkingBase {

    //for forge only, maybe fabric
    public static final Identifier NBT_PACKET_ID = Identifier.fromNamespaceAndPath(Weather.MODID, "nbt");

    private static WeatherNetworkingBase instance;

    public static WeatherNetworkingBase instance() {
        return instance;
    }

    public WeatherNetworkingBase() {
        instance = this;
    }

    public abstract void clientSendToServer(CompoundTag data);

    public abstract void serverSendToClientAll(CompoundTag data);

    public abstract void serverSendToClientPlayer(CompoundTag data, Player player);

    public abstract void serverSendToClientNear(CompoundTag data, Vec3 pos, double dist, Level level);

    public abstract void serverSendToClientsInDimension(CompoundTag data, Level level);

}

