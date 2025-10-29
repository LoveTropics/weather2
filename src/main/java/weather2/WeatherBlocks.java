package weather2;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import weather2.block.AnemometerBlock;
import weather2.block.DeflectorBlock;
import weather2.block.ForecastBlock;
import weather2.block.SandLayerBlock;
import weather2.block.SensorBlock;
import weather2.block.SirenBlock;
import weather2.block.WindTurbineBlock;
import weather2.block.WindVaneBlock;
import weather2.blockentity.AnemometerBlockEntity;
import weather2.blockentity.DeflectorBlockEntity;
import weather2.blockentity.SensorBlockEntity;
import weather2.blockentity.SirenBlockEntity;
import weather2.blockentity.WindTurbineBlockEntity;
import weather2.blockentity.WindVaneBlockEntity;

import java.util.function.Supplier;

@Mod(Weather.MODID)
public class WeatherBlocks {

    public static final String SAND_LAYER = "sand_layer";
    public static final String DEFLECTOR = "weather_deflector";
    public static final String TORNADO_SENSOR = "tornado_sensor";
    public static final String TORNADO_SIREN = "tornado_siren";
    public static final String WEATHER_MACHINE = "weather_machine";

    public static final String WEATHER_FORECAST = "weather_forecast";
    public static final String WIND_VANE = "wind_vane";
    public static final String ANEMOMETER = "anemometer";
    public static final String TORNADO_SIREN_MANUAL = "tornado_siren_manual";

    public static final String SAND_LAYER_PLACEABLE = "sand_layer_placeable";
    public static final String WEATHER_ITEM = "weather_item";
    public static final String POCKET_SAND = "pocket_sand";
    public static final String WIND_TURBINE = "wind_turbine";

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Weather.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Weather.MODID);

    public static final DeferredBlock<SandLayerBlock> BLOCK_SAND_LAYER = BLOCKS.registerBlock(SAND_LAYER, SandLayerBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).mapColor(MapColor.SAND).strength(0.1F).sound(SoundType.SAND));
    public static final DeferredBlock<DeflectorBlock> BLOCK_DEFLECTOR = BLOCKS.registerBlock(DEFLECTOR, DeflectorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).mapColor(MapColor.STONE).strength(0.5F, 6F).sound(SoundType.STONE));
    public static final DeferredBlock<ForecastBlock> BLOCK_FORECAST = BLOCKS.registerBlock(WEATHER_FORECAST, ForecastBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 6F).sound(SoundType.STONE));
    public static final DeferredBlock<SensorBlock> BLOCK_TORNADO_SENSOR = BLOCKS.registerBlock(TORNADO_SENSOR, SensorBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 6F).sound(SoundType.STONE));
    public static final DeferredBlock<AnemometerBlock> BLOCK_ANEMOMETER = BLOCKS.registerBlock(ANEMOMETER, AnemometerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 6F).sound(SoundType.STONE));
    public static final DeferredBlock<WindVaneBlock> BLOCK_WIND_VANE = BLOCKS.registerBlock(WIND_VANE, WindVaneBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 6F).sound(SoundType.STONE));
    public static final DeferredBlock<SirenBlock> BLOCK_TORNADO_SIREN = BLOCKS.registerBlock(TORNADO_SIREN, SirenBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 6F).sound(SoundType.STONE));
    public static final DeferredBlock<WindTurbineBlock> BLOCK_WIND_TURBINE = BLOCKS.registerBlock(WIND_TURBINE, WindTurbineBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 6F).sound(SoundType.STONE));
    //public static final Supplier<WeatherMachineBlock> BLOCK_WEATHER_MACHINE = BLOCKS.registerBlock(WEATHER_MACHINE, WeatherMachineBlock::new, BlockBehaviour.Properties.of(Material.STONE).strength(0.5F, 6F).sound(SoundType.STONE));

    @SuppressWarnings("ConstantConditions")
    public static final Supplier<BlockEntityType<DeflectorBlockEntity>> BLOCK_ENTITY_DEFLECTOR = BLOCK_ENTITIES.register(DEFLECTOR, () ->
		new BlockEntityType<>(DeflectorBlockEntity::new, BLOCK_DEFLECTOR.get()));

    @SuppressWarnings("ConstantConditions")
    public static final Supplier<BlockEntityType<SirenBlockEntity>> BLOCK_ENTITY_TORNADO_SIREN = BLOCK_ENTITIES.register(TORNADO_SIREN, () ->
		new BlockEntityType<>(SirenBlockEntity::new, BLOCK_TORNADO_SIREN.get()));

    public static final Supplier<BlockEntityType<SensorBlockEntity>> BLOCK_ENTITY_TORNADO_SENSOR = BLOCK_ENTITIES.register(TORNADO_SENSOR, () ->
		new BlockEntityType<>(SensorBlockEntity::new, BLOCK_TORNADO_SENSOR.get()));

    public static final Supplier<BlockEntityType<AnemometerBlockEntity>> BLOCK_ENTITY_ANEMOMETER = BLOCK_ENTITIES.register(ANEMOMETER, () ->
		new BlockEntityType<>(AnemometerBlockEntity::new, BLOCK_ANEMOMETER.get()));

    public static final Supplier<BlockEntityType<WindVaneBlockEntity>> BLOCK_ENTITY_WIND_VANE = BLOCK_ENTITIES.register(WIND_VANE, () ->
		new BlockEntityType<>(WindVaneBlockEntity::new, BLOCK_WIND_VANE.get()));

    public static final Supplier<BlockEntityType<WindTurbineBlockEntity>> BLOCK_ENTITY_WIND_TURBINE = BLOCK_ENTITIES.register(WIND_TURBINE, () ->
		new BlockEntityType<>(WindTurbineBlockEntity::new, BLOCK_WIND_TURBINE.get()));

    /*public static final Supplier<BlockEntityType<WeatherMachineBlockEntity>> BLOCK_ENTITY_WEATHER_MACHINE = BLOCK_ENTITIES.register(WEATHER_MACHINE, () ->
            BlockEntityType.Builder.of(WeatherMachineBlockEntity::new, BLOCK_WEATHER_MACHINE.get()).build(null));*/

    public static void registerHandlers(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

}
