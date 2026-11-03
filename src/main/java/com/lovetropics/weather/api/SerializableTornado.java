package com.lovetropics.weather.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import weather2.api.NadoEntitySpawnSettings;
import weather2.api.PlayerController;
import weather2.api.Tornado;

import java.util.Optional;

public record SerializableTornado(
        Optional<PlayerController> playerController,
        boolean isBaby,
        boolean isFireNado,
        Optional<NadoEntitySpawnSettings> entitySpawnSettings,
        float speed,
        int startStage,
        int maxStage,
        Optional<Integer> lengthInTicks,
        boolean giveSlowFallingEffect
) {

    public static final Codec<SerializableTornado> CODEC = RecordCodecBuilder.create(i -> i.group(
            PlayerController.CODEC.optionalFieldOf("player_controller").forGetter(SerializableTornado::playerController),
            Codec.BOOL.optionalFieldOf("baby", false).forGetter(SerializableTornado::isBaby),
            Codec.BOOL.optionalFieldOf("fire", false).forGetter(SerializableTornado::isFireNado),
            NadoEntitySpawnSettings.CODEC.optionalFieldOf("entity_spawn_settings").forGetter(SerializableTornado::entitySpawnSettings),
            Codec.FLOAT.optionalFieldOf("speed", 0.1f).forGetter(SerializableTornado::speed),
            Codec.INT.optionalFieldOf("start_stage", 5).forGetter(SerializableTornado::startStage), // This should be better than some magic numbers
            Codec.INT.optionalFieldOf("max_stage", 8).forGetter(SerializableTornado::maxStage), // Again should be better than some magic numbers
            Codec.INT.optionalFieldOf("length_in_ticks").forGetter(SerializableTornado::lengthInTicks),
            Codec.BOOL.optionalFieldOf("give_slow_falling", false).forGetter(SerializableTornado::giveSlowFallingEffect)
    ).apply(i, SerializableTornado::new));


    public Tornado createTornado(Vec3 spawnLocation, Vec3 aimLocation) {
        return new Tornado(
                playerController,
                isBaby,
                isFireNado,
                entitySpawnSettings,
                startStage,
                maxStage,
                speed,
                lengthInTicks,
                Optional.of(spawnLocation),
                Optional.of(aimLocation),
                Optional.empty(),
                giveSlowFallingEffect
        );
    }

    public Tornado createTornado(Vec3 spawnLocation, Vec3 aimLocation, Entity entity) {
        return new Tornado(
                playerController,
                isBaby,
                isFireNado,
                entitySpawnSettings,
                startStage,
                maxStage,
                speed,
                lengthInTicks,
                Optional.of(spawnLocation),
                Optional.of(aimLocation),
                Optional.of(entity),
                giveSlowFallingEffect
        );
    }
}
