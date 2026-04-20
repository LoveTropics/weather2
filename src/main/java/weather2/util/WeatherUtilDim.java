package weather2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WeatherUtilDim {

    public static boolean canBlockSeeSky(Level world, BlockPos pos) {
        if (pos.getY() >= getSeaLevel(world)) {
            return world.canSeeSkyFromBelowWater(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), getSeaLevel(world), pos.getZ());
            if (!world.canSeeSkyFromBelowWater(blockpos)) {
                return false;
            } else {
                for(BlockPos blockpos1 = blockpos.below(); blockpos1.getY() > pos.getY(); blockpos1 = blockpos1.below()) {
                    BlockState blockstate = world.getBlockState(blockpos1);
                    if (blockstate.getLightDampening() > 0 && !blockstate.liquid()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static int getSeaLevel(Level world) {
        //TODO: sync customizable sea level to client, also use World.getSeaLevel if logical server
        return 63;
    }

}
