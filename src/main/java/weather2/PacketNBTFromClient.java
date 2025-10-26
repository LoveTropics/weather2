package weather2;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record PacketNBTFromClient(CompoundTag nbt) implements PacketBase
{
    public static final CustomPacketPayload.Type<PacketNBTFromClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Weather.MODID, "nbt_server"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketNBTFromClient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, PacketNBTFromClient::nbt,
            PacketNBTFromClient::new);

    public PacketNBTFromClient(RegistryFriendlyByteBuf buf)
    {
        this(buf.readNbt());
    }


    public void write(FriendlyByteBuf buf)
    {
        buf.writeNbt(nbt);
    }


    public void handle(Player player)
    {
        try {
            if (player instanceof ServerPlayer) {

				String packetCommand = nbt.getStringOr("packetCommand", "");
				String command = nbt.getStringOr("command", "");

                Weather.dbg("Weather2 packet command from client: " + packetCommand + " - " + command);

                if (packetCommand.equals("WeatherData")) {

                    if (command.equals("syncFull")) {
                        ServerTickHandler.playerClientRequestsFullSync((ServerPlayer) player);
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	/*@Override
	public ResourceLocation id() {
		return WatutMod.PACKET_ID_NBT_FROM_SERVER;
	}*/

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
