package weather2.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import weather2.WeatherItems;

import java.util.concurrent.CompletableFuture;

public class WeatherRecipeProvider extends RecipeProvider {
	private final HolderGetter<Item> items;

	public WeatherRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
		super(lookupProvider, output);
		items = lookupProvider.lookupOrThrow(Registries.ITEM);
	}

    @Override
	protected void buildRecipes() {

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.WEATHER_ITEM.get(), 1)
                .pattern("X X").pattern("DID").pattern("X X")
                .define('D', Items.REDSTONE)
                .define('I', Items.GOLD_INGOT)
                .define('X', Items.IRON_INGOT)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_DEFLECTOR_ITEM.get(), 1)
                .pattern("XDX").pattern("DID").pattern("XDX")
                .define('D', Items.REDSTONE)
                .define('I', WeatherItems.WEATHER_ITEM.get())
                .define('X', Items.IRON_INGOT)
                .unlockedBy("has_weather_item", has(WeatherItems.WEATHER_ITEM.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_FORECAST_ITEM.get(), 1)
                .pattern("XDX").pattern("DID").pattern("XDX")
                .define('D', Items.REDSTONE)
                .define('I', Items.COMPASS)
                .define('X', WeatherItems.WEATHER_ITEM.get())
                .unlockedBy("has_weather_item", has(WeatherItems.WEATHER_ITEM.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_TORNADO_SENSOR_ITEM.get(), 1)
                .pattern("X X").pattern("DID").pattern("X X")
                .define('D', Items.REDSTONE)
                .define('I', WeatherItems.WEATHER_ITEM.get())
                .define('X', Items.IRON_INGOT)
                .unlockedBy("has_weather_item", has(WeatherItems.WEATHER_ITEM.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_TORNADO_SIREN_ITEM.get(), 1)
                .pattern("XDX").pattern("DID").pattern("XDX")
                .define('D', Items.REDSTONE)
                .define('I', WeatherItems.BLOCK_TORNADO_SENSOR_ITEM.get())
                .define('X', Items.IRON_INGOT)
                .unlockedBy("has_sensor_item", has(WeatherItems.BLOCK_TORNADO_SENSOR_ITEM.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_WIND_VANE_ITEM.get(), 1)
                .pattern("X X").pattern("DXD").pattern("X X")
                .define('D', Items.REDSTONE)
                .define('X', WeatherItems.WEATHER_ITEM.get())
                .unlockedBy("has_weather_item", has(WeatherItems.WEATHER_ITEM.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_WIND_TURBINE_ITEM.get(), 1)
                .pattern("ODO").pattern("IVI").pattern("RGR")
                .define('I', Items.IRON_BLOCK)
                .define('O', Items.IRON_INGOT)
                .define('D', Items.DIAMOND)
                .define('V', WeatherItems.BLOCK_WIND_VANE_ITEM.get())
                .define('R', Items.REDSTONE_BLOCK)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_wind_vane", has(WeatherItems.BLOCK_WIND_TURBINE_ITEM.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_ANEMOMETER_ITEM.get(), 1)
                .pattern("X X").pattern("XDX").pattern("X X")
                .define('D', Items.REDSTONE)
                .define('X', WeatherItems.WEATHER_ITEM.get())
                .unlockedBy("has_weather_item", has(WeatherItems.WEATHER_ITEM.get()))
			.save(output);

		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, WeatherItems.BLOCK_SAND_LAYER_ITEM.get(), 8)
                .pattern("DDD").pattern("D D").pattern("DDD")
                .define('D', Items.SAND)
                .unlockedBy("has_sand", has(Items.SAND))
			.save(output);
    }

	public static final class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
			return new WeatherRecipeProvider(lookupProvider, output);
		}

		@Override
		public String getName() {
			return "Weather2 recipes";
		}
	}
}
