package weather2.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.neoforged.neoforge.client.extensions.IDimensionSpecialEffectsExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import weather2.config.ConfigParticle;

@Mixin(IDimensionSpecialEffectsExtension.class)
public interface RenderParticlesOverride {

    //replaced by RenderLevelStageEvent.Stage.AFTER_PARTICLES
    /*@Redirect(method = "renderLevel",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V"))
    public void render(ParticleEngine particleManager, PoseStack matrixStackIn, MultiBufferSource.BufferSource bufferIn, LightTexture lightTextureIn, Camera activeRenderInfoIn, float partialTicks, @Nullable net.minecraft.client.renderer.culling.Frustum clippingHelper) {
        ClientTickHandler.particleManagerExtended().render(matrixStackIn, bufferIn, lightTextureIn, activeRenderInfoIn, partialTicks, clippingHelper);
        particleManager.render(matrixStackIn, bufferIn, lightTextureIn, activeRenderInfoIn, partialTicks, clippingHelper);

    }*/

    @ModifyReturnValue(method = "renderSnowAndRain", at = @At("RETURN"))
    default boolean renderSnowAndRain(boolean original) {
        //CULog.dbg("renderSnowAndRain hook");
        //stopping vanilla from running renderRainSnow
        //returns true to disable vanilla renderSnowAndRain
        return !ConfigParticle.Particle_vanilla_precipitation || original;
    }

    /*@Redirect(method = "renderLevel",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/math/Matrix4f;FDDD)V"))
    public void renderClouds(LevelRenderer instance, PoseStack poseStack, Matrix4f l, float i1, double f1, double f2, double d0) {
        //CULog.dbg("renderClouds hook");
        //workaround for missing projection matrix info
        ICloudRenderHandler cloudRenderHandler = Minecraft.getInstance().level().effects().getCloudRenderHandler();
        if (cloudRenderHandler instanceof CloudRenderHandler) {
            ((CloudRenderHandler)cloudRenderHandler).render(poseStack, l, i1, f1, f2, d0);
        } else {
            instance.renderClouds(poseStack, l, i1, f1, f2, d0);
        }
    }*/
}