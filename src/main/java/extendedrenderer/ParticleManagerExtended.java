package extendedrenderer;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import extendedrenderer.particle.entity.EntityRotFX;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ParticleManagerExtended implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particles");
   private static final ResourceLocation PARTICLES_ATLAS_INFO = ResourceLocation.withDefaultNamespace("particles");
   private static final int MAX_PARTICLES_PER_LAYER = 16384;
	private static final List<ParticleRenderType> RENDER_ORDER = ImmutableList.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, ParticleRenderType.CUSTOM, ParticleRenderType.CUSTOM, EntityRotFX.SORTED_OPAQUE_BLOCK, EntityRotFX.SORTED_TRANSLUCENT);
   protected ClientLevel level;
   public final Map<ParticleRenderType, Queue<Particle>> particles = Maps.newTreeMap(net.neoforged.neoforge.client.ClientHooks.makeParticleRenderTypeComparator(RENDER_ORDER));
   private final Queue<TrackingEmitter> trackingEmitters = Queues.newArrayDeque();
   private final TextureManager textureManager;
   private final RandomSource random = RandomSource.create();
   private final Map<ResourceLocation, ParticleProvider<?>> providers = new java.util.HashMap<>();
   private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
   private final Map<ResourceLocation, ParticleManagerExtended.MutableSpriteSet> spriteSets = Maps.newHashMap();
   private final TextureAtlas textureAtlas;
   private final Object2IntOpenHashMap<ParticleGroup> trackedParticleCounts = new Object2IntOpenHashMap<>();

   public ParticleManagerExtended(ClientLevel p_107299_, TextureManager p_107300_) {
      this.textureAtlas = new TextureAtlas(TextureAtlas.LOCATION_PARTICLES);
      //p_107300_.register(this.textureAtlas.location(), this.textureAtlas);
      this.level = p_107299_;
      this.textureManager = p_107300_;
   }

	@Override
	public CompletableFuture<Void> reload(PreparationBarrier p_107305_, ResourceManager p_107306_, Executor p_107309_, Executor p_107310_) {
      record ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) {
      }
      CompletableFuture<List<ParticleDefinition>> completablefuture = CompletableFuture.supplyAsync(() -> {
         return PARTICLE_LISTER.listMatchingResources(p_107306_);
      }, p_107309_).thenCompose((p_247914_) -> {
         List<CompletableFuture<ParticleDefinition>> list = new ArrayList<>(p_247914_.size());
         p_247914_.forEach((p_247903_, p_247904_) -> {
            ResourceLocation resourcelocation = PARTICLE_LISTER.fileToId(p_247903_);
            list.add(CompletableFuture.supplyAsync(() -> {
               return new ParticleDefinition(resourcelocation, this.loadParticleDescription(resourcelocation, p_247904_));
            }, p_107309_));
         });
         return Util.sequence(list);
      });
      CompletableFuture<SpriteLoader.Preparations> completablefuture1 = SpriteLoader.create(this.textureAtlas).loadAndStitch(p_107306_, PARTICLES_ATLAS_INFO, 0, p_107309_).thenCompose(SpriteLoader.Preparations::waitForUpload);
      return CompletableFuture.allOf(completablefuture1, completablefuture).thenCompose(p_107305_::wait).thenAcceptAsync((p_247900_) -> {
         this.clearParticles();
		  ProfilerFiller profiler = Profiler.get();
		  profiler.startTick();
		  profiler.push("upload");
         SpriteLoader.Preparations spriteloader$preparations = completablefuture1.join();
         this.textureAtlas.upload(spriteloader$preparations);
		  profiler.popPush("bindSpriteSets");
         Set<ResourceLocation> set = new HashSet<>();
         TextureAtlasSprite textureatlassprite = spriteloader$preparations.missing();
         completablefuture.join().forEach((p_247911_) -> {
            Optional<List<ResourceLocation>> optional = p_247911_.sprites();
            if (!optional.isEmpty()) {
               List<TextureAtlasSprite> list = new ArrayList<>();

               for(ResourceLocation resourcelocation : optional.get()) {
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
            LOGGER.warn("Missing particle sprites: {}", set.stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
         }

		  profiler.pop();
		  profiler.endTick();
      }, p_107310_);
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   private Optional<List<ResourceLocation>> loadParticleDescription(ResourceLocation p_250648_, Resource p_248793_) {
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

   @Nullable
   private <T extends ParticleOptions> Particle makeParticle(T p_107396_, double p_107397_, double p_107398_, double p_107399_, double p_107400_, double p_107401_, double p_107402_) {
      ParticleProvider<T> particleprovider = (ParticleProvider<T>)this.providers.get(BuiltInRegistries.PARTICLE_TYPE.getKey(p_107396_.getType()));
      return particleprovider == null ? null : particleprovider.createParticle(p_107396_, this.level, p_107397_, p_107398_, p_107399_, p_107400_, p_107401_, p_107402_);
   }

   public void add(Particle p_107345_) {
      Optional<ParticleGroup> optional = p_107345_.getParticleGroup();
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
      this.particles.forEach((p_288249_, p_288250_) -> {
		  profiler.push("weather2_particle_tick_" + p_288249_.toString());
         this.tickParticleList(p_288250_);
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
            this.particles.computeIfAbsent(particle.getRenderType(), (p_107347_) -> {
               return EvictingQueue.create(16384 * 2);
            }).add(particle);
         }
      }
	   profiler.pop();
   }

   private void tickParticleList(Collection<Particle> p_107385_) {
      if (!p_107385_.isEmpty()) {
         Iterator<Particle> iterator = p_107385_.iterator();

         while(iterator.hasNext()) {
            Particle particle = iterator.next();
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               particle.getParticleGroup().ifPresent((p_172289_) -> {
                  this.updateCount(p_172289_, -1);
               });
               iterator.remove();
            }
         }
      }

   }

   private void updateCount(ParticleGroup p_172282_, int p_172283_) {
      this.trackedParticleCounts.addTo(p_172282_, p_172283_);
   }

   private void tickParticle(Particle p_107394_) {
      try {
         p_107394_.tick();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking Particle");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being ticked");
         crashreportcategory.setDetail("Particle", p_107394_::toString);
         crashreportcategory.setDetail("Particle Type", p_107394_.getRenderType()::toString);
         throw new ReportedException(crashreport);
      }
   }

    public void render(Camera camera, float partialTick, MultiBufferSource.BufferSource bufferSource, @Nullable net.minecraft.client.renderer.culling.Frustum frustum, java.util.function.Predicate<ParticleRenderType> renderTypePredicate) {
	   ProfilerFiller profiler = Profiler.get();
	   profiler.push("weather2_particle_render");
//      float fogStart = RenderSystem.getShaderFogStart();
//      float fogEnd = RenderSystem.getShaderFogEnd();
//      RenderSystem.setShaderFogStart(fogStart * 4);
//      RenderSystem.setShaderFogEnd(fogEnd * 4);

        for (ParticleRenderType particlerendertype : this.particles.keySet()) { // Neo: allow custom IParticleRenderType's
            if (particlerendertype == ParticleRenderType.NO_RENDER || particlerendertype == ParticleRenderType.CUSTOM || !renderTypePredicate.test(particlerendertype)) continue;
            Queue<Particle> queue = this.particles.get(particlerendertype);
            if (queue != null && !queue.isEmpty()) {
                renderParticleType(camera, partialTick, bufferSource, particlerendertype, queue, frustum);
            }
        }

        Queue<Particle> queue1 = this.particles.get(ParticleRenderType.CUSTOM);
        if (queue1 != null && !queue1.isEmpty()) {
            renderCustomParticles(camera, partialTick, bufferSource, queue1, frustum);
        }

        bufferSource.endBatch();

//      RenderSystem.setShaderFogStart(fogStart);
//      RenderSystem.setShaderFogEnd(fogEnd);
	   profiler.pop();
   }

    private static void renderParticleType(
        Camera camera, float partialTick, MultiBufferSource.BufferSource bufferSource, ParticleRenderType particleType, Queue<Particle> particles, @Nullable net.minecraft.client.renderer.culling.Frustum frustum
    ) {
        VertexConsumer vertexconsumer = bufferSource.getBuffer(Objects.requireNonNull(particleType.renderType()));

        for (Particle particle : particles) {
            if (frustum != null && !frustum.isVisible(particle.getRenderBoundingBox(partialTick))) continue;
            try {
                particle.render(vertexconsumer, camera, partialTick);
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Particle");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                crashreportcategory.setDetail("Particle", particle::toString);
                crashreportcategory.setDetail("Particle Type", particleType::toString);
                throw new ReportedException(crashreport);
            }
        }
    }

    private static void renderCustomParticles(Camera camera, float partialTick, MultiBufferSource.BufferSource bufferSource, Queue<Particle> particles, @Nullable net.minecraft.client.renderer.culling.Frustum frustum) {
        PoseStack posestack = new PoseStack();

        for (Particle particle : particles) {
            if (frustum != null && !frustum.isVisible(particle.getRenderBoundingBox(partialTick))) continue;
            try {
                particle.renderCustom(posestack, bufferSource, camera, partialTick);
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Particle");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                crashreportcategory.setDetail("Particle", particle::toString);
                crashreportcategory.setDetail("Particle Type", "Custom");
                throw new ReportedException(crashreport);
            }
        }
    }

    public void setLevel(@Nullable ClientLevel p_107343_) {
      this.level = p_107343_;
      this.clearParticles();
      this.trackingEmitters.clear();
   }

   public String countParticles() {
      return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
   }

   private boolean hasSpaceInParticleLimit(ParticleGroup p_172280_) {
      return this.trackedParticleCounts.getInt(p_172280_) < p_172280_.getLimit();
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
         return this.sprites.get(p_107413_ * (this.sprites.size() - 1) / p_107414_);
      }

      public TextureAtlasSprite get(RandomSource p_233889_) {
         return this.sprites.get(p_233889_.nextInt(this.sprites.size()));
      }

      public void rebind(List<TextureAtlasSprite> p_107416_) {
         this.sprites = ImmutableList.copyOf(p_107416_);
      }
   }

   public Map<ParticleRenderType, Queue<Particle>> getParticles() {
      return particles;
   }
}
