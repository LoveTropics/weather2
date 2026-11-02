package com.lovetropics.weather.minigames.actions;

import com.lovetropics.weather.api.SerializableTornado;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.lovetropics.games.common.core.game.GameException;
import org.lovetropics.games.common.core.game.IGamePhase;
import org.lovetropics.games.common.core.game.behavior.IGameBehavior;
import org.lovetropics.games.common.core.game.behavior.event.EventRegistrar;
import org.lovetropics.games.common.core.game.behavior.event.GameActionEvents;
import org.lovetropics.games.common.core.game.region.RegionQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SpawnTornadoAction(
		SerializableTornado tornado,
		SpawnMode spawnMode,
		boolean aimAtTarget,
		int xOffset,
		int zOffset,
		Optional<RegionQuery> target
) implements IGameBehavior {

	public static final MapCodec<SpawnTornadoAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			SerializableTornado.CODEC.fieldOf("tornado").forGetter(SpawnTornadoAction::tornado),
			SpawnMode.CODEC.optionalFieldOf("mode", SpawnMode.PRE_LEVEL).forGetter(SpawnTornadoAction::spawnMode),
			Codec.BOOL.optionalFieldOf("aim_at_target", false).forGetter(SpawnTornadoAction::aimAtTarget),
			Codec.INT.optionalFieldOf("x_offset", 150).forGetter(SpawnTornadoAction::xOffset),
			Codec.INT.optionalFieldOf("z_offset", 150).forGetter(SpawnTornadoAction::zOffset),
			RegionQuery.CODEC.optionalFieldOf("target").forGetter(SpawnTornadoAction::target)
	).apply(i, SpawnTornadoAction::new));

	@Override
	public void register(IGamePhase game, EventRegistrar events) throws GameException {
		events.listen(GameActionEvents.APPLY, (_, targets) -> {

			// This is a bit messy to but like it should work, slowly to expose more settings
			switch (spawnMode) {
				case PRE_LEVEL -> {
					List<ServerLevel> levelToSpawnIn = new ArrayList<>();
					for (ServerPlayer player : targets.asPlayers(game)) {
						if (!levelToSpawnIn.contains(player.level())) {
							levelToSpawnIn.add(player.level());
						}
					}
					for (ServerLevel level : levelToSpawnIn) {
						Vec3 targetLocation = target.map(query -> query.one(game).center()).orElse(getLegacyTargetLocation(level));
						Vec3 spawnLocation = getRandomOffset(targetLocation);
						tornado.createTornado(spawnLocation, targetLocation)
								.spawnTornado(level);
					}
				}
				case ALL_TARGET -> {
					for (ServerPlayer player : targets.asPlayers(game)) {
						Vec3 targetLocation = aimAtTarget ? player.position() : getLegacyTargetLocation(player.level());
						Vec3 spawnLocation = getRandomOffset(targetLocation);
						if (aimAtTarget) {
							tornado.createTornado(spawnLocation, targetLocation, player)
									.spawnTornado(player.level());
						} else {
							tornado.createTornado(spawnLocation, targetLocation)
									.spawnTornado(player.level());
						}
					}
				}
			}

			return true;
		});
	}

	public enum SpawnMode implements StringRepresentable {
		PRE_LEVEL("pre_level"),
		ALL_TARGET("all_target");

		private final String name;

		public static final Codec<SpawnMode> CODEC = StringRepresentable.fromEnum(SpawnMode::values);

		SpawnMode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	/// Look this is a bit of legacy code that we should make better, but like :cry: right now - UnReal Nov 1st 2026
	private Vec3 getLegacyTargetLocation(ServerLevel level) {
		List<? extends Player> players = level.players();
		Vec3 vecAdd = new Vec3(0, 0, 0);
		for (Player player : players) {
			vecAdd = vecAdd.add(player.position());
		}
		vecAdd = new Vec3(vecAdd.x / players.size(), vecAdd.y / players.size(), vecAdd.z / players.size());
		return vecAdd;
	}

	/// Look this is a bit of legacy code that we should make better, but like :cry: right now - UnReal Nov 1st 2026
	private Vec3 getRandomOffset(Vec3 base) {
		boolean xNeg = Math.random() < 0.5;
		boolean zNeg = Math.random() < 0.5;
		return new Vec3(base.x + (xNeg ? -xOffset: xOffset), base.y, base.z + (zNeg ? -zOffset : zOffset));
	}

	@Override
	public MapCodec<? extends IGameBehavior> codec() {
		return CODEC;
	}
}
