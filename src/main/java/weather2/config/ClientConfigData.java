package weather2.config;

import net.minecraft.nbt.CompoundTag;

/**
 * Used for anything that needs to be used on both client and server side, to avoid config mismatch between dedicated server and clients
 */
public class ClientConfigData {

    public boolean overcastMode = false;
    public boolean Storm_Tornado_grabPlayer = true;
    public boolean Storm_Tornado_grabPlayersOnly = false;
    public boolean Storm_Tornado_grabMobs = true;
    public boolean Storm_Tornado_grabAnimals = true;
    public boolean Storm_Tornado_grabItems = false;
    public boolean Storm_Tornado_grabVillagers = true;
    public boolean Aesthetic_Only_Mode = false;

    /**
     * For client side
     *
     * @param nbt
     */
    public void readNBT(CompoundTag nbt) {
		overcastMode = nbt.getBooleanOr("overcastMode", false);
		Storm_Tornado_grabPlayer = nbt.getBooleanOr("Storm_Tornado_grabPlayer", true);
		Storm_Tornado_grabPlayersOnly = nbt.getBooleanOr("Storm_Tornado_grabPlayersOnly", false);
		Storm_Tornado_grabMobs = nbt.getBooleanOr("Storm_Tornado_grabMobs", true);
		Storm_Tornado_grabAnimals = nbt.getBooleanOr("Storm_Tornado_grabAnimals", true);
		Storm_Tornado_grabVillagers = nbt.getBooleanOr("Storm_Tornado_grabVillagers", true);
		Storm_Tornado_grabItems = nbt.getBooleanOr("Storm_Tornado_grabItems", false);
		Aesthetic_Only_Mode = nbt.getBooleanOr("Aesthetic_Only_Mode", false);
    }

    /**
     * For server side
     *
     * @param data
     */
    public static void writeNBT(CompoundTag data) {

        data.putBoolean("overcastMode", ConfigMisc.overcastMode);
        data.putBoolean("Storm_Tornado_grabPlayer", ConfigTornado.Storm_Tornado_grabPlayer);
        data.putBoolean("Storm_Tornado_grabPlayersOnly", ConfigTornado.Storm_Tornado_grabPlayersOnly);
        data.putBoolean("Storm_Tornado_grabMobs", ConfigTornado.Storm_Tornado_grabMobs);
        data.putBoolean("Storm_Tornado_grabAnimals", ConfigTornado.Storm_Tornado_grabAnimals);
        data.putBoolean("Storm_Tornado_grabVillagers", ConfigTornado.Storm_Tornado_grabVillagers);
        data.putBoolean("Storm_Tornado_grabItems", ConfigTornado.Storm_Tornado_grabItems);
        data.putBoolean("Aesthetic_Only_Mode", ConfigMisc.Aesthetic_Only_Mode);


    }

}
