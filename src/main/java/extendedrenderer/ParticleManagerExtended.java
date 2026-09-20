package extendedrenderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import extendedrenderer.particle.entity.EntityRotFX;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ElderGuardianParticleGroup;
import net.minecraft.client.particle.ItemPickupParticleGroup;
import net.minecraft.client.particle.NoRenderParticleGroup;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.particle.QuadParticleGroup;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleLimit;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParticleManagerExtended extends ParticleEngine implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particles");
    private static final Identifier PARTICLES_ATLAS_INFO = Identifier.withDefaultNamespace("particles");
    private static final List<ParticleRenderType> RENDER_ORDER = ImmutableList.of(ParticleRenderType.SINGLE_QUADS, ParticleRenderType.ITEM_PICKUP, ParticleRenderType.ELDER_GUARDIANS, EntityRotFX.SORTED_OPAQUE_BLOCK_TYPE, EntityRotFX.SORTED_TRANSLUCENT_TYPE);
    private final Queue<TrackingEmitter> trackingEmitters = Queues.newArrayDeque();
    private final Map<ParticleRenderType, Function<ParticleEngine, ParticleGroup<?>>> particleGroupFactories;
    private final List<ParticleRenderType> particleRenderOrder;
    private final Map<Identifier, ParticleProvider<?>> providers = new java.util.HashMap<>();
   private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
    private final Map<Identifier, ParticleManagerExtended.MutableSpriteSet> spriteSets = Maps.newHashMap();
   private final TextureAtlas textureAtlas;
    private final Object2IntOpenHashMap<ParticleLimit> trackedParticleCounts = new Object2IntOpenHashMap<>();

    public ParticleManagerExtended(ClientLevel level, ParticleResources resourceManager) {
        super(level, resourceManager);
      this.textureAtlas = new TextureAtlas(TextureAtlas.LOCATION_PARTICLES);
      //p_107300_.register(this.textureAtlas.location(), this.textureAtlas);
        var particleGroupFactories = new Reference2ObjectOpenHashMap<ParticleRenderType, Function<ParticleEngine, ParticleGroup<?>>>();
        var particleRenderOrder = new ArrayList<>(RENDER_ORDER);
        net.neoforged.fml.ModLoader.postEvent(new RegisterParticleGroupsEvent(particleGroupFactories, particleRenderOrder));
        this.particleGroupFactories = Reference2ObjectMaps.unmodifiable(particleGroupFactories);
        this.particleRenderOrder = List.copyOf(particleRenderOrder);
   }

	@Override
    public CompletableFuture<Void> reload(PreparableReloadListener.SharedState currentReload, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor) {
        record ParticleDefinition(Identifier id, Optional<List<Identifier>> sprites) {
      }
        ResourceManager p_107306_ = currentReload.resourceManager();
      CompletableFuture<List<ParticleDefinition>> completablefuture = CompletableFuture.supplyAsync(() -> {
         return PARTICLE_LISTER.listMatchingResources(p_107306_);
      }, taskExecutor).thenCompose((p_247914_) -> {
         List<CompletableFuture<ParticleDefinition>> list = new ArrayList<>(p_247914_.size());
         p_247914_.forEach((p_247903_, p_247904_) -> {
             Identifier resourcelocation = PARTICLE_LISTER.fileToId(p_247903_);
            list.add(CompletableFuture.supplyAsync(() -> {
               return new ParticleDefinition(resourcelocation, this.loadParticleDescription(resourcelocation, p_247904_));
            }, taskExecutor));
         });
         return Util.sequence(list);
      });
        CompletableFuture<SpriteLoader.Preparations> completablefuture1 = SpriteLoader.create(this.textureAtlas).loadAndStitch(p_107306_, PARTICLES_ATLAS_INFO, 0, taskExecutor, Set.of());
        return CompletableFuture.allOf(completablefuture1, completablefuture).thenCompose(preparationBarrier::wait).thenAcceptAsync((p_247900_) -> {
         this.clearParticles();
		  ProfilerFiller profiler = Profiler.get();
		  profiler.startTick();
		  profiler.push("upload");
         SpriteLoader.Preparations spriteloader$preparations = completablefuture1.join();
         this.textureAtlas.upload(spriteloader$preparations);
		  profiler.popPush("bindSpriteSets");
          Set<Identifier> set = new HashSet<>();
         TextureAtlasSprite textureatlassprite = spriteloader$preparations.missing();
         completablefuture.join().forEach((p_247911_) -> {
             Optional<List<Identifier>> optional = p_247911_.sprites();
            if (!optional.isEmpty()) {
               List<TextureAtlasSprite> list = new ArrayList<>();

                for (Identifier resourcelocation : optional.get()) {
                  TextureAtlasSprite textureatlassprite1 = spriteloader$preparations.regions().get(resourcelocation);
                  if (textureatlassprite1 == null) {
                     set.add(resourcelocation);
                     list.add(textureatlassprite);
                  } else {
                     list.add(textureatlassprite1);
                  }
               }

               if (list.isEmpty()) {
                  list.add(textureatlassprite);
               }

               this.spriteSets.get(p_247911_.id()).rebind(list);
            }
         });
         if (!set.isEmpty()) {
             LOGGER.warn("Missing particle sprites: {}", set.stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")));
         }

		  profiler.pop();
		  profiler.endTick();
        }, reloadExecutor);
   }

    private Optional<List<Identifier>> loadParticleDescription(Identifier p_250648_, Resource p_248793_) {
      if (!this.spriteSets.containsKey(p_250648_)) {
         LOGGER.debug("Redundant texture list for particle: {}", (Object)p_250648_);
         return Optional.empty();
      } else {
         try (Reader reader = p_248793_.openAsReader()) {
            ParticleDescription particledescription = ParticleDescription.fromJson(GsonHelper.parse(reader));
            return Optional.of(particledescription.getTextures());
         } catch (IOException ioexception) {
            throw new IllegalStateException("Failed to load description for particle " + p_250648_, ioexception);
         }
      }
   }

   public void add(Particle p_107345_) {
       Optional<ParticleLimit> optional = p_107345_.getParticleLimit();
      if (optional.isPresent()) {
         if (this.hasSpaceInParticleLimit(optional.get())) {
            this.particlesToAdd.add(p_107345_);
            this.updateCount(optional.get(), 1);
         }
      } else {
         this.particlesToAdd.add(p_107345_);
      }

   }

   public void tick() {
	   ProfilerFiller profiler = Profiler.get();
	   profiler.push("weather2_particle_tick");
       this.particles.forEach((p_288249_, group) -> {
		  profiler.push("weather2_particle_tick_" + p_288249_.toString());
           group.tickParticles();
		  profiler.pop();
      });
      if (!this.trackingEmitters.isEmpty()) {
         List<TrackingEmitter> list = Lists.newArrayList();

         for(TrackingEmitter trackingemitter : this.trackingEmitters) {
            trackingemitter.tick();
            if (!trackingemitter.isAlive()) {
               list.add(trackingemitter);
            }
         }

         this.trackingEmitters.removeAll(list);
      }

      Particle particle;
      if (!this.particlesToAdd.isEmpty()) {
         while((particle = this.particlesToAdd.poll()) != null) {
             if (!this.particles.computeIfAbsent(particle.getGroup(), this::createParticleGroup).add(particle)) {
                 particle.getParticleLimit().ifPresent(options -> this.updateCount(options, -1));
             }
         }
      }
	   profiler.pop();
   }

    private ParticleGroup<?> createParticleGroup(ParticleRenderType type) {
        if (type == EntityRotFX.SORTED_TRANSLUCENT_TYPE || type == EntityRotFX.SORTED_OPAQUE_BLOCK_TYPE) {
            return new WeatherParticleGroup(this, type);
        } else if (type == ParticleRenderType.ITEM_PICKUP) {
            return new ItemPickupParticleGroup(this);
        } else if (type == ParticleRenderType.ELDER_GUARDIANS) {
            return new ElderGuardianParticleGroup(this);
        } else if (this.particleGroupFactories.containsKey(type)) {
            return this.particleGroupFactories.get(type).apply(this);
        } else {
            return type == ParticleRenderType.NO_RENDER ? new NoRenderParticleGroup(this) : new QuadParticleGroup(this, type);
        }
    }

    protected void updateCount(ParticleLimit p_172282_, int p_172283_) {
        this.trackedParticleCounts.addTo(p_172282_, p_172283_);
    }

    public void extract(ParticlesRenderState particlesRenderState, Frustum frustum, Camera camera, float partialTickTime) {
        for (ParticleRenderType particleType : particleRenderOrder) {
            ParticleGroup<?> particles = this.particles.get(particleType);
            if (particles != null && !particles.isEmpty()) {
                particlesRenderState.add(particles.extractRenderState(frustum, camera, partialTickTime));
            }
        }
    }

    public void setLevel(@Nullable ClientLevel p_107343_) {
      this.level = p_107343_;
      this.clearParticles();
      this.trackingEmitters.clear();
   }

    private boolean hasSpaceInParticleLimit(ParticleLimit p_172280_) {
        return this.trackedParticleCounts.getInt(p_172280_) < p_172280_.limit();
   }

   public void clearParticles() {
      this.particles.clear();
      this.particlesToAdd.clear();
      this.trackingEmitters.clear();
      this.trackedParticleCounts.clear();
   }

   static class MutableSpriteSet implements SpriteSet {
      private List<TextureAtlasSprite> sprites;

      public TextureAtlasSprite get(int p_107413_, int p_107414_) {
          return sprites.get(p_107413_ * (this.sprites.size() - 1) / p_107414_);
      }

      public TextureAtlasSprite get(RandomSource p_233889_) {
          return sprites.get(p_233889_.nextInt(this.sprites.size()));
      }

       @Override
       public TextureAtlasSprite first() {
           return sprites.getFirst();
       }

       public void rebind(List<TextureAtlasSprite> p_107416_) {
           sprites = ImmutableList.copyOf(p_107416_);
      }
   }

    public Map<ParticleRenderType, ParticleGroup<?>> getParticles() {
      return particles;
   }
}
