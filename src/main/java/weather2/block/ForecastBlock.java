package weather2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import weather2.ServerTickHandler;
import weather2.config.ConfigStorm;
import weather2.weathersystem.WeatherManagerServer;

public class ForecastBlock extends Block {

    public ForecastBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (use(state, level, pos, player, hand, hitResult) == InteractionResult.SUCCESS) {
			return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return use(state, level, pos, player, InteractionHand.MAIN_HAND, hitResult);
    }

    public InteractionResult use(BlockState p_60503_, Level level, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {

        if (!level.isClientSide()) {
            WeatherManagerServer wm = ServerTickHandler.getWeatherManagerFor(p_60506_.level().dimension());
            float chance = wm.getBiomeBasedStormSpawnChanceInArea(new BlockPos(p_60506_.blockPosition()));
            float chanceEvery10Days = 0;

            int rateOften;
            int rateLessOften;
            int day = 20*60*20;
            int diceRollRate = ConfigStorm.Storm_AllTypes_TickRateDelay;
            if (ConfigStorm.Server_Storm_Deadly_UseGlobalRate) {
                rateOften = ConfigStorm.Server_Storm_Deadly_TimeBetweenInTicks / day;
                rateLessOften = ConfigStorm.Server_Storm_Deadly_TimeBetweenInTicks_Land_Based / day;
                chanceEvery10Days = ConfigStorm.Server_Storm_Deadly_OddsTo1_Land_Based;
            } else {
                rateOften = ConfigStorm.Player_Storm_Deadly_TimeBetweenInTicks / day;
                rateLessOften = ConfigStorm.Player_Storm_Deadly_TimeBetweenInTicks_Land_Based / day;
                chanceEvery10Days = ConfigStorm.Player_Storm_Deadly_OddsTo1_Land_Based;
            }
            if (chanceEvery10Days > 0 && diceRollRate > 0) {
                chanceEvery10Days = ((float)day / (float)diceRollRate) / chanceEvery10Days;
			}
			if (p_60506_ instanceof ServerPlayer serverPlayer) {
				serverPlayer.sendSystemMessage(Component.literal(String.format("Chance of a deadly storm here every:")));
				serverPlayer.sendSystemMessage(Component.literal(String.format("%d days: %.2f", rateOften, (chance * 100F)) + "% from nearby biome temperature differences"));
				serverPlayer.sendSystemMessage(Component.literal(String.format("%d days: %.2f", rateLessOften, (chanceEvery10Days * 100F)) + "% from randomly trying once a day"));
			}
            int count = 0;
            for (int i = 0; i < 100000; i++) {
                if (level.getRandom().nextInt(1000) == 0) {
                    count++;
                }
            }
            //System.out.println(count);
        }

        return InteractionResult.CONSUME;
    }
}
