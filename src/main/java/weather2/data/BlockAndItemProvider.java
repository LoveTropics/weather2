package weather2.data;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import weather2.Weather;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BlockAndItemProvider extends SpriteSourceProvider {

	public BlockAndItemProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
	{
		super(output, lookupProvider, Weather.MODID);
	}

	@Override
	protected void gather()
	{
		addSpriteBlock("tornado_siren");
		addSpriteBlock("tornado_siren_manual");
		addSpriteBlock("tornado_siren_manual_on");
		addSpriteBlock("tornado_sensor");
		addSpriteBlock("weather_deflector");
		addSpriteBlock("weather_forecast");
		addSpriteBlock("weather_machine");
		addSpriteBlock("anemometer");
		addSpriteBlock("wind_vane");
		addSpriteBlock("wind_turbine");
		addSpriteItem("weather_item");
		addSpriteItem("sand_layer");
		addSpriteItem("sand_layer_placeable");
	}

	public void addSpriteBlock(String textureName) {
		atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(ResourceLocation.parse(Weather.MODID + ":blocks/" + textureName), Optional.empty()));
	}

	public void addSpriteItem(String textureName) {
		atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(ResourceLocation.parse(Weather.MODID + ":items/" + textureName), Optional.empty()));
	}
}
