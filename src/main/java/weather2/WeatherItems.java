package weather2;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import weather2.item.WeatherItem;

public class WeatherItems {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Weather.MODID);

    public static final DeferredItem<WeatherItem> WEATHER_ITEM = ITEMS.registerItem(WeatherBlocks.WEATHER_ITEM, WeatherItem::new);
    public static final DeferredItem<BlockItem> BLOCK_DEFLECTOR_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_DEFLECTOR);
    public static final DeferredItem<BlockItem> BLOCK_TORNADO_SIREN_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_TORNADO_SIREN);
    public static final DeferredItem<BlockItem> BLOCK_TORNADO_SENSOR_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_TORNADO_SENSOR);
    public static final DeferredItem<BlockItem> BLOCK_SAND_LAYER_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_SAND_LAYER);
    public static final DeferredItem<BlockItem> BLOCK_FORECAST_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_FORECAST);
    public static final DeferredItem<BlockItem> BLOCK_ANEMOMETER_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_ANEMOMETER);
    public static final DeferredItem<BlockItem> BLOCK_WIND_VANE_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_WIND_VANE);
    public static final DeferredItem<BlockItem> BLOCK_WIND_TURBINE_ITEM = ITEMS.registerSimpleBlockItem(WeatherBlocks.BLOCK_WIND_TURBINE);

    public static void registerHandlers(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
