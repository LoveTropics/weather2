package weather2.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import weather2.Weather;
import weather2.blockentity.WindTurbineBlockEntity;
import weather2.client.entity.model.WindTurbineModel;
import weather2.client.tile.state.WindTurbineRenderState;

public class WindTurbineEntityRenderer implements BlockEntityRenderer<WindTurbineBlockEntity, WindTurbineRenderState> {
    public static final SpriteId TEXTURE = new SpriteId(AtlasIds.BLOCKS, Identifier.fromNamespaceAndPath(Weather.MODID, "blocks/te/wind_turbine.png"));
    private final SpriteGetter sprites;
    protected final WindTurbineModel model;

    public WindTurbineEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.model = new WindTurbineModel(Minecraft.getInstance().getEntityModels().bakeLayer(WindTurbineModel.LAYER_LOCATION));
    }

    @Override
    public void submit(WindTurbineRenderState state, PoseStack stack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        submitNodeCollector.submitModel(model, state, stack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, TEXTURE, sprites, 0, state.breakProgress);
    }

    @Override
    public WindTurbineRenderState createRenderState() {
        return new WindTurbineRenderState();
    }

    @Override
    public void extractRenderState(WindTurbineBlockEntity te, WindTurbineRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        float lerpAngle = (float) Mth.lerp((double) partialTicks, te.smoothAnglePrev, te.smoothAngle);
        state.yRot = (float) Math.toRadians(lerpAngle);
    }
}
