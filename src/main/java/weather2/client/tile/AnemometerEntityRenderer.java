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
import weather2.blockentity.AnemometerBlockEntity;
import weather2.client.entity.model.AnemometerModel;
import weather2.client.tile.state.AnemometerRenderState;
import weather2.weathersystem.WeatherManagerClient;
import weather2.weathersystem.wind.WindManager;

public class AnemometerEntityRenderer implements BlockEntityRenderer<AnemometerBlockEntity, AnemometerRenderState> {
    public static final SpriteId TEXTURE = new SpriteId(AtlasIds.BLOCKS, Identifier.fromNamespaceAndPath(Weather.MODID, "blocks/te/anemometer.png"));
    private final SpriteGetter sprites;
    protected final AnemometerModel model;

    public AnemometerEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.model = new AnemometerModel(Minecraft.getInstance().getEntityModels().bakeLayer(AnemometerModel.LAYER_LOCATION));
    }

    @Override
    public void submit(AnemometerRenderState state, PoseStack stack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        WeatherManagerClient weatherMan = ClientTickHandler.weatherManager;
        if (weatherMan == null) return;
        WindManager windMan = weatherMan.getWindManager();
        if (windMan == null) return;
        submitNodeCollector.submitModel(model, state, stack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, TEXTURE, sprites, 0, state.breakProgress);
    }

    @Override
    public AnemometerRenderState createRenderState() {
        return new AnemometerRenderState();
    }

    @Override
    public void extractRenderState(AnemometerBlockEntity te, AnemometerRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPos, breakProgress);
        RandomSource rand = te.getLevel().getRandom();
        float lerpAngle = (float) Mth.lerp((double) partialTicks, te.smoothAnglePrev, te.smoothAngle);
        state.shaking = te.smoothAngleRotationalVel > 45;
        state.xRot = (float) ((rand.nextFloat() - rand.nextFloat()) * Math.toRadians(7));
        state.yRot = (float) Math.toRadians(lerpAngle);
        state.zRot = (float) ((rand.nextFloat() - rand.nextFloat()) * Math.toRadians(7));
    }
}
