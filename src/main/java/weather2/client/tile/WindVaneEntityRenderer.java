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
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import weather2.ClientTickHandler;
import weather2.Weather;
import weather2.blockentity.WindVaneBlockEntity;
import weather2.client.entity.model.WindVaneModel;
import weather2.client.tile.state.WindVaneRenderState;
import weather2.weathersystem.WeatherManagerClient;
import weather2.weathersystem.wind.WindManager;

public class WindVaneEntityRenderer implements BlockEntityRenderer<WindVaneBlockEntity, WindVaneRenderState> {
    public static final SpriteId TEXTURE = new SpriteId(AtlasIds.BLOCKS, Identifier.fromNamespaceAndPath(Weather.MODID, "blocks/te/wind_vane.png"));
    private final SpriteGetter sprites;
    protected final WindVaneModel model;

    public WindVaneEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.model = new WindVaneModel(Minecraft.getInstance().getEntityModels().bakeLayer(WindVaneModel.LAYER_LOCATION));
    }

    @Override
    public void submit(WindVaneRenderState state, PoseStack stack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        WeatherManagerClient weatherMan = ClientTickHandler.weatherManager;
        if (weatherMan == null) return;
        WindManager windMan = weatherMan.getWindManager();
        if (windMan == null) return;
        submitNodeCollector.submitModel(model, state, stack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, TEXTURE, sprites, 0, state.breakProgress);
    }

    @Override
    public WindVaneRenderState createRenderState() {
        return new WindVaneRenderState();
    }

    @Override
    public void extractRenderState(WindVaneBlockEntity te, WindVaneRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
        RandomSource rand = te.getLevel().getRandom();
        float lerpAngle = (float) Mth.lerp((double) partialTicks, te.smoothAnglePrev, te.smoothAngle);
        state.yRot = (float) Math.toRadians(lerpAngle);
        state.yRotAddition = (float) ((rand.nextFloat() - rand.nextFloat()) * Math.toRadians(2));
        state.zRot = (float) ((rand.nextFloat() - rand.nextFloat()) * Math.toRadians(1));
        WeatherManagerClient weatherMan = ClientTickHandler.weatherManager;
        if (weatherMan != null) {
            WindManager windMan = weatherMan.getWindManager();
            if (windMan != null) {
                state.shaking = windMan.getWindSpeed(te.getBlockPos()) >= 1.5;
            } else {
                state.shaking = false;
            }
        } else {
            state.shaking = false;
        }
    }
}
