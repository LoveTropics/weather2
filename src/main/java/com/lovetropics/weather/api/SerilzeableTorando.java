package com.lovetropics.weather.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import weather2.api.NadoEntitySpawnSettings;
import weather2.api.PlayerController;
import weather2.api.Tornado;

import java.util.Optional;

public record SerilzeableTorando(
        Optional<PlayerController> playerController,
        boolean isBaby,
        boolean isFireNado,
        Optional<NadoEntitySpawnSettings> entitySpawnSettings,
        float speed,
        int startStage,
        int maxStage
) {

    public static final Codec<SerilzeableTorando> CODEC = RecordCodecBuilder.create(i -> i.group(
            PlayerController.CODEC.optionalFieldOf("player_controller").forGetter(SerilzeableTorando::playerController),
            Codec.BOOL.optionalFieldOf("baby", false).forGetter(SerilzeableTorando::isBaby),
            Codec.BOOL.optionalFieldOf("fire", false).forGetter(SerilzeableTorando::isFireNado),
            NadoEntitySpawnSettings.CODEC.optionalFieldOf("entity_spawn_settings").forGetter(SerilzeableTorando::entitySpawnSettings),
            Codec.FLOAT.optionalFieldOf("speed", 0.1f).forGetter(SerilzeableTorando::speed),
            Codec.INT.optionalFieldOf("start_stage", 5).forGetter(SerilzeableTorando::startStage), // This should be better than some magic numbers
            Codec.INT.optionalFieldOf("max_stage", 8).forGetter(SerilzeableTorando::maxStage) // Again should be better than some magic numbers
    ).apply(i, SerilzeableTorando::new));


    public Tornado createTornado(Vec3 spawnLocation, Vec3 aimLocation) {
        return new Tornado(
                playerController,
                isBaby,
                isFireNado,
                entitySpawnSettings,
                startStage,
                maxStage,
                speed,
                Optional.of(spawnLocation),
                Optional.of(aimLocation),
                Optional.empty()
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
                Optional.of(spawnLocation),
                Optional.of(aimLocation),
                Optional.of(entity)
        );
    }
}
