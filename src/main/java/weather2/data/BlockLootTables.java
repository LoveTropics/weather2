package weather2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import weather2.Weather;
import weather2.WeatherBlocks;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockLootTables extends BlockLootSubProvider {

    public BlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(WeatherBlocks.BLOCK_WIND_TURBINE.get());
        dropSelf(WeatherBlocks.BLOCK_WIND_VANE.get());
        dropSelf(WeatherBlocks.BLOCK_ANEMOMETER.get());
        dropSelf(WeatherBlocks.BLOCK_DEFLECTOR.get());
        dropSelf(WeatherBlocks.BLOCK_FORECAST.get());
        dropSelf(WeatherBlocks.BLOCK_TORNADO_SENSOR.get());
        dropSelf(WeatherBlocks.BLOCK_SAND_LAYER.get());
        dropSelf(WeatherBlocks.BLOCK_TORNADO_SIREN.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Weather.MODID)).collect(Collectors.toList());
    }
}
