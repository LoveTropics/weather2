package weather2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import weather2.Weather;
import weather2.Weather2Tags;

import java.util.concurrent.CompletableFuture;

public class TagProvider extends TagsProvider<Item> {

    public TagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, Weather.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(Weather2Tags.ACID_REPELLENT).addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Weather.LTMINIGAMES_MODID, "acid_repellent_umbrella")));
    }
}

