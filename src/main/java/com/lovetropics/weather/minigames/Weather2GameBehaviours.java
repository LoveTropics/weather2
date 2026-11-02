package com.lovetropics.weather.minigames;

import com.lovetropics.weather.minigames.actions.SpawnTornadoAction;
import com.lovetropics.weather.minigames.actions.TransformPlayerTornadoAction;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.lovetropics.games.common.core.game.behavior.GameBehaviorTypes;
import org.lovetropics.games.common.core.game.behavior.IGameBehavior;
import weather2.Weather;

public class Weather2GameBehaviours {

    public static final DeferredRegister<MapCodec<? extends IGameBehavior>> REGISTER = DeferredRegister.create(GameBehaviorTypes.REGISTRY_KEY, Weather.MODID);

    static {
        register("spawn_tornado", SpawnTornadoAction.CODEC);
        register("transform_player_tornado", TransformPlayerTornadoAction.CODEC);
    }

    private static <T extends IGameBehavior> void register(String name, MapCodec<T> codec) {
        REGISTER.register(name, () -> codec);
    }

    public static void init(IEventBus modBus) {
        REGISTER.register(modBus);
    }
}
