package weather2;

import com.corosus.coroutil.util.CULog;
import com.corosus.modconfig.ConfigMod;
import com.corosus.modconfig.IConfigCategory;
import com.mojang.brigadier.CommandDispatcher;
import extendedrenderer.particle.ParticleRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weather2.command.WeatherCommand;
import weather2.config.ConfigDebug;
import weather2.config.ConfigMisc;
import weather2.config.ConfigParticle;
import weather2.config.ConfigSand;
import weather2.config.ConfigSnow;
import weather2.config.ConfigSound;
import weather2.config.ConfigStorm;
import weather2.config.ConfigTornado;
import weather2.config.ConfigWind;
import weather2.data.BlockAndItemProvider;
import weather2.data.BlockLootTables;
import weather2.data.WeatherRecipeProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Weather.MODID)
public class Weather
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "weather2";

    public static boolean initProperNeededForWorld = true;

    public static List<IConfigCategory> listConfigs = new ArrayList<>();
    public static ConfigMisc configMisc = null;

    //public static final CreativeModeTab CREATIVE_TAB = new WeatherTab();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WEATHER_TAB = CREATIVE_MODE_TABS.register("weather_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("itemGroup.weather2"))
            .icon(() -> WeatherItems.WEATHER_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(WeatherItems.WEATHER_ITEM.get());
                output.accept(WeatherItems.BLOCK_TORNADO_SIREN_ITEM.get());
                output.accept(WeatherItems.BLOCK_TORNADO_SENSOR_ITEM.get());
                output.accept(WeatherItems.BLOCK_DEFLECTOR_ITEM.get());
                output.accept(WeatherItems.BLOCK_FORECAST_ITEM.get());
                output.accept(WeatherItems.BLOCK_SAND_LAYER_ITEM.get());
                output.accept(WeatherItems.BLOCK_ANEMOMETER_ITEM.get());
                output.accept(WeatherItems.BLOCK_WIND_VANE_ITEM.get());
                output.accept(WeatherItems.BLOCK_WIND_TURBINE_ITEM.get());
            }).build());

    public Weather(ModContainer modContainer) {

        //TODO: 1.21 might not need
        //ParticleRegistry2ElectricBubbleoo.bootstrap();

        new WeatherNetworkingv2();

        //load class so it registers
        EntityRegistry.init(modContainer);

        IEventBus modBus = modContainer.getEventBus();
        modBus.addListener(this::setup);
        NeoForge.EVENT_BUS.addListener(this::serverStop);
        NeoForge.EVENT_BUS.addListener(this::serverStart);
        NeoForge.EVENT_BUS.register(ServerTickHandler.class);
        CREATIVE_MODE_TABS.register(modBus);
        modBus.addListener(this::gatherData);
        modBus.addListener(this::processIMC);
        modBus.addListener(this::addCreative);

        NeoForge.EVENT_BUS.register(this);
        WeatherBlocks.registerHandlers(modBus);
        WeatherItems.registerHandlers(modBus);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        //NeoForge.EVENT_BUS.register(new WeatherBlocks());
        modContainer.getEventBus().addListener(this::registerPackets);

        new File("./config/Weather2").mkdirs();
        configMisc = new ConfigMisc();
        ConfigMod.addConfigFile(MODID, addConfig(configMisc));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigWind()));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigSand()));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigSnow()));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigStorm()));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigTornado()));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigParticle()));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigDebug()));
        ConfigMod.addConfigFile(MODID, addConfig(new ConfigSound()));
        //ConfigMod.addConfigFile(MODID, addConfig(new ConfigFoliage()));
        //WeatherUtilConfig.nbtLoadDataAll();

        SoundRegistry.init();
    }

    public void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        WeatherNetworkingv2.register(registrar);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(WeatherItems.WEATHER_ITEM.get());
    }

    public static IConfigCategory addConfig(IConfigCategory config) {
        listConfigs.add(config);
        return config;
    }

    private void setup(final FMLCommonSetupEvent event) {
        //WeatherNetworkingOld.register();
    }

    private void processIMC(final InterModProcessEvent event)
    {
        LOGGER.info("Got IMC {}", event.getIMCStream().
            map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void serverStart(ServerStartedEvent event) {
        //initProperNeededForWorld = true;
        //WeatherUtil.testAllBlocks();
    }

    @SubscribeEvent
    public void serverStop(ServerStoppedEvent event) {
        initProperNeededForWorld = true;
    }

    public static void dbg(Object obj) {
        CULog.dbg("" + obj);
    }

    public static boolean isLoveTropicsInstalled() {
        return ModList.get().isLoaded("ltminigames");
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        WeatherCommand.register(dispatcher);
    }


    /**
     *
     * run runData for me
     *
     * @param event
     */
    private void gatherData(GatherDataEvent.Client event) {
        event.createProvider(WeatherRecipeProvider.Runner::new);
        event.createProvider((output, lookupProvider) -> new LootTableProvider(output, Set.of(),
            List.of(new LootTableProvider.SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)), lookupProvider));
        gatherClientData(event);
    }

    /**
     *
     * run runData for me
     *
     * @param event
     */
    private void gatherClientData(GatherDataEvent event) {
        event.createProvider(ParticleRegistry::new);
        event.createProvider(BlockAndItemProvider::new);
    }
}
