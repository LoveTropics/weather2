package weather2;

import net.minecraft.world.entity.Entity;
import net.tropicraft.core.common.entity.underdasea.SharkEntity;

public class LoveTropicsIntegration {
    public static boolean isShark(Entity entity) {
        return entity instanceof SharkEntity;
    }
}
