package weather2.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import weather2.ServerTickHandler;
import weather2.WeatherBlocks;
import weather2.weathersystem.WeatherManagerServer;

public class DeflectorBlockEntity extends BlockEntity {

    private boolean needsInit = true;

    public DeflectorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(WeatherBlocks.BLOCK_ENTITY_DEFLECTOR.get(), p_155229_, p_155230_);
    }

    public static void tickHelper(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        ((DeflectorBlockEntity)blockEntity).tick();
    }

    public void tick() {
        if (needsInit) {
            needsInit = false;
            init();
        }
    }

    public void init() {
        if (!level.isClientSide()) {
            ServerTickHandler.getWeatherManagerFor(level).registerDeflector(getBlockPos());
        }
    }

    public void blockBroken() {
        WeatherManagerServer weatherManagerServer = ServerTickHandler.getWeatherManagerFor(level);
        if (weatherManagerServer != null) {
            //CULog.dbg("removing weather deflector poi at " + getBlockPos());
            weatherManagerServer.removeDeflector(getBlockPos());
        }
    }

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		blockBroken();
	}
}
