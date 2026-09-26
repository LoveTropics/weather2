package weather2.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class EntityNandoAttachment {

    public static final MapCodec<EntityNandoAttachment> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            UUIDUtil.CODEC.optionalFieldOf("player").forGetter(a -> a.player),
            Codec.BOOL.fieldOf("has_moved_enough").forGetter(a -> a.hasMovedEnough),
            Codec.BOOL.fieldOf("was_created_by_nando").forGetter(a -> a.wasCreatedByNando)
    ).apply(i, EntityNandoAttachment::new));


    private Optional<UUID> player;
    private boolean hasMovedEnough;
    private boolean wasCreatedByNando;

    public EntityNandoAttachment(Optional<UUID> player, boolean hasMovedEnough, boolean wasCreatedByNando) {
        this.player = player;
        this.hasMovedEnough = hasMovedEnough;
        this.wasCreatedByNando = wasCreatedByNando;
    }

    public EntityNandoAttachment() {
        this.player = Optional.empty();
        this.hasMovedEnough = false;
        this.wasCreatedByNando = false;
    }

    public boolean hasPlayer() {
        return player.isPresent();
    }

    public void setPlayer(Player player) {
        this.player = Optional.of(player.getUUID());
    }

    public boolean hasMovedEnough() {
        return hasMovedEnough;
    }

    public void setHasMovedEnough(boolean hasMovedEnough) {
        this.hasMovedEnough = hasMovedEnough;
    }

    public @Nullable Player getPlayer(Level level) {
        return player.map(level::getPlayerByUUID).orElse(null);
    }

    public boolean isWasCreatedByNando() {
        return wasCreatedByNando;
    }

    public void setWasCreatedByNando(boolean wasCreatedByNando) {
        this.wasCreatedByNando = wasCreatedByNando;
    }
}
