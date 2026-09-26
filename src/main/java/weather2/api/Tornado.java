package weather2.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record Tornado(
        Optional<PlayerController> playerController,
        boolean isBaby,
        boolean isFireNado,
        Optional<NadoEntitySpawnSettings> entitySpawnSettings,
        int startStage,
        int maxStage
) {

    public static final Codec<Tornado> CODEC = RecordCodecBuilder.create(i -> i.group(
            PlayerController.CODEC.optionalFieldOf("player_controller").forGetter(Tornado::playerController),
            Codec.BOOL.optionalFieldOf("baby", false).forGetter(Tornado::isBaby),
            Codec.BOOL.optionalFieldOf("fire", false).forGetter(Tornado::isFireNado),
            NadoEntitySpawnSettings.CODEC.optionalFieldOf("entity_spawn_settings").forGetter(Tornado::entitySpawnSettings),
            Codec.INT.optionalFieldOf("start_stage", 5).forGetter(Tornado::startStage), // This should be better than some magic numbers
            Codec.INT.optionalFieldOf("max_stage", 8).forGetter(Tornado::maxStage) // Again should be better than some magic numbers
    ).apply(i, Tornado::new));


}
