package weather2.weathersystem.storm;

import com.corosus.coroutil.util.CoroUtilBlock;
import com.google.common.collect.Sets;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class LightningBoltWeatherNew extends Entity {
   private static final int START_LIFE = 2;
   private static final double DAMAGE_RADIUS = 3.0D;
   private static final double DETECTION_RADIUS = 15.0D;
   private int life;
   public long seed;
   private int flashes;
   private boolean visualOnly;
   @Nullable
   private ServerPlayer cause;
   private final Set<Entity> hitEntities = Sets.newHashSet();
   private int blocksSetOnFire;
   private float damage = 5.0F;

   public LightningBoltWeatherNew(EntityType<? extends LightningBoltWeatherNew> p_20865_, Level p_20866_) {
      super(p_20865_, p_20866_);
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
   }

   public void setVisualOnly(boolean p_20875_) {
      this.visualOnly = p_20875_;
   }

   public SoundSource getSoundSource() {
      return SoundSource.WEATHER;
   }

   @Nullable
   public ServerPlayer getCause() {
      return this.cause;
   }

   public void setCause(@Nullable ServerPlayer p_20880_) {
      this.cause = p_20880_;
   }

   private void powerLightningRod() {
      BlockPos blockpos = this.getStrikePosition();
      BlockState blockstate = this.level().getBlockState(blockpos);
      if (blockstate.is(Blocks.LIGHTNING_ROD)) {
         ((LightningRodBlock)blockstate.getBlock()).onLightningStrike(blockstate, this.level(), blockpos);
      }

   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public float getDamage() {
      return this.damage;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder builder) {

   }

   public void tick() {
      super.tick();
      if (this.life == 2) {
         if (this.level().isClientSide()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
         } else {
            /*Difficulty difficulty = this.level().getDifficulty();
            if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
               this.spawnFire(4);
            }*/

            this.powerLightningRod();
            clearCopperOnLightningStrike(this.level(), this.getStrikePosition());
            this.gameEvent(GameEvent.LIGHTNING_STRIKE);
         }
      }

      --this.life;
      if (this.life < 0) {
         if (this.flashes == 0) {
            if (this.level() instanceof ServerLevel) {
               /*List<Entity> list = this.level().getEntities(this, new AABB(this.getX() - 15.0D, this.getY() - 15.0D, this.getZ() - 15.0D, this.getX() + 15.0D, this.getY() + 6.0D + 15.0D, this.getZ() + 15.0D), (p_147140_) -> {
                  return p_147140_.isAlive() && !this.hitEntities.contains(p_147140_);
               });

               for(ServerPlayer serverplayer : ((ServerLevel)this.level()).getPlayers((p_147157_) -> {
                  return p_147157_.distanceTo(this) < 256.0F;
               })) {
                  CriteriaTriggers.LIGHTNING_STRIKE.trigger(serverplayer, this, list);
               }*/
            }

            this.discard();
         } else if (this.life < -this.random.nextInt(10)) {
            --this.flashes;
            this.life = 1;
            this.seed = this.random.nextLong();
            /*this.spawnFire(0);*/
         }
      }

      if (this.life >= 0) {
         if (!(this.level() instanceof ServerLevel)) {
            this.level().setSkyFlashTime(2);
         } else if (!this.visualOnly) {
            List<Entity> list1 = this.level().getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), Entity::isAlive);

            /*for(Entity entity : list1) {
               if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this))
               entity.thunderHit((ServerLevel)this.level(), this);
            }*/

            this.hitEntities.addAll(list1);
            if (this.cause != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, list1);
            }
         }
      }

   }

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
		return false;
	}

	private BlockPos getStrikePosition() {
      Vec3 vec3 = this.position();
      return CoroUtilBlock.blockPos(vec3.x, vec3.y - 1.0E-6D, vec3.z);
   }

   private void spawnFire(int p_20871_) {
      BlockPos blockpos = this.blockPosition();
      if (!this.visualOnly && level() instanceof ServerLevel level && level.canSpreadFireAround(blockpos)) {
		   BlockState blockstate = BaseFireBlock.getState(level, blockpos);
		   if (level.getBlockState(blockpos).isAir() && blockstate.canSurvive(level, blockpos)) {
			   level.setBlockAndUpdate(blockpos, blockstate);
            ++this.blocksSetOnFire;
         }

         for(int i = 0; i < p_20871_; ++i) {
            BlockPos blockpos1 = blockpos.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
			 blockstate = BaseFireBlock.getState(level, blockpos1);
			 if (level.getBlockState(blockpos1).isAir() && blockstate.canSurvive(level, blockpos1)) {
				 level.setBlockAndUpdate(blockpos1, blockstate);
               ++this.blocksSetOnFire;
            }
         }

      }
   }

   private static void clearCopperOnLightningStrike(Level p_147151_, BlockPos p_147152_) {
      BlockState blockstate = p_147151_.getBlockState(p_147152_);
      BlockPos blockpos;
      BlockState blockstate1;
      if (blockstate.is(Blocks.LIGHTNING_ROD)) {
         blockpos = p_147152_.relative(blockstate.getValue(LightningRodBlock.FACING).getOpposite());
         blockstate1 = p_147151_.getBlockState(blockpos);
      } else {
         blockpos = p_147152_;
         blockstate1 = blockstate;
      }

      if (blockstate1.getBlock() instanceof WeatheringCopper) {
         p_147151_.setBlockAndUpdate(blockpos, WeatheringCopper.getFirst(p_147151_.getBlockState(blockpos)));
         BlockPos.MutableBlockPos blockpos$mutableblockpos = p_147152_.mutable();
         int i = p_147151_.getRandom().nextInt(3) + 3;

         for(int j = 0; j < i; ++j) {
            int k = p_147151_.getRandom().nextInt(8) + 1;
            randomWalkCleaningCopper(p_147151_, blockpos, blockpos$mutableblockpos, k);
         }

      }
   }

   private static void randomWalkCleaningCopper(Level p_147146_, BlockPos p_147147_, BlockPos.MutableBlockPos p_147148_, int p_147149_) {
      p_147148_.set(p_147147_);

      for(int i = 0; i < p_147149_; ++i) {
         Optional<BlockPos> optional = randomStepCleaningCopper(p_147146_, p_147148_);
         if (!optional.isPresent()) {
            break;
         }

         p_147148_.set(optional.get());
      }

   }

   private static Optional<BlockPos> randomStepCleaningCopper(Level level, BlockPos p_147155_) {
      for (BlockPos blockpos : BlockPos.randomInCube(level.getRandom(), 10, p_147155_, 1)) {
         BlockState blockstate = level.getBlockState(blockpos);
         if (blockstate.getBlock() instanceof WeatheringCopper) {
            WeatheringCopper.getPrevious(blockstate).ifPresent((p_147144_) -> {
               level.setBlockAndUpdate(blockpos, p_147144_);
            });
            level.levelEvent(3002, blockpos, -1);
            return Optional.of(blockpos);
         }
      }

      return Optional.empty();
   }

   public boolean shouldRenderAtSqrDistance(double p_20869_) {
      double d0 = 64.0D * getViewScale();
      return p_20869_ < d0 * d0;
   }

   protected void defineSynchedData() {
   }

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
	}

   public int getBlocksSetOnFire() {
      return this.blocksSetOnFire;
   }

   public Stream<Entity> getHitEntities() {
      return this.hitEntities.stream().filter(Entity::isAlive);
   }
}
