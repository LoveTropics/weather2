package extendedrenderer.particle.entity;

import com.corosus.coroutil.util.CULog;
import com.corosus.coroutil.util.CoroUtilBlock;
import com.mojang.math.Axis;
import extendedrenderer.WeatherParticleRenderState;
import extendedrenderer.particle.ParticleRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public class ParticleCube extends ParticleTexFX {
	public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

	public ParticleCube(Level worldIn, double posXIn, double posYIn,
                        double posZIn, double mX, double mY, double mZ,
                        BlockState state) {
		super((ClientLevel) worldIn, posXIn, posYIn, posZIn, mX, mY, mZ, ParticleRegistry.potato);

		/**
		 * really basic way to get a sprite from a blockstate, could easily get the wrong one if multiple quads are used per direction
		 * should do fine for most blocks that have the same texture on every side
		 */
		TextureAtlasSprite sprite = getSpriteFromState(state);
		if (sprite != null) {
			setSprite(sprite);
		} else {
			CULog.dbg("unable to find sprite to use from block: " + state);
			sprite = getSpriteFromState(Blocks.DIRT.defaultBlockState());
			//if (CoroUtilMisc.random().nextBoolean()) sprite = getSpriteFromState(Blocks.GRASS.defaultBlockState());
			if (sprite != null) {
				setSprite(sprite);
			}
		}
		int multiplier = Minecraft.getInstance().getBlockColors().getTintSource(state, 0).colorInWorld(state, this.level, CoroUtilBlock.blockPos(posXIn, posYIn, posZIn));
		float mr = ((multiplier >>> 16) & 0xFF) / 255f;
		float mg = ((multiplier >>> 8) & 0xFF) / 255f;
		float mb = (multiplier & 0xFF) / 255f;
		setColor(mr, mg, mb);
	}

	public TextureAtlasSprite getSpriteFromState(BlockState state) {
		return Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(state).sprite();
	}

	@Override
	public void extract(QuadParticleRenderState qState, Camera renderInfo, float partialTicks) {
		if (!(qState instanceof WeatherParticleRenderState state)) {
			return;
		}

		//if (true) return;
		Vec3 pos = renderInfo.position();
		float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - pos.x());
		float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - pos.y());
		float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - pos.z());
		Quaternionf quaternion;
		if (this.facePlayer || (this.rotationPitch == 0 && this.rotationYaw == 0)) {
			quaternion = renderInfo.rotation();
		} else {
			// override rotations
			quaternion = new Quaternionf(0, 0, 0, 1);
			if (facePlayerYaw) {
				quaternion.mul(Axis.YP.rotationDegrees(-renderInfo.yRot()));
			} else {
				quaternion.mul(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, this.prevRotationYaw, rotationYaw)));
			}
			quaternion.mul(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, this.prevRotationPitch, rotationPitch)));
		}

		TextureAtlasSprite sprite = null;

		List<Vector3fc[]> faces = new ArrayList<>();

		//xy -z
		faces.add(new Vector3f[] {
			new Vector3f(-1.0F, -1.0F, -1.0F),
			new Vector3f(-1.0F, 1.0F, -1.0F),
			new Vector3f(1.0F, 1.0F, -1.0F),
			new Vector3f(1.0F, -1.0F, -1.0F)});

		//xy +z
		faces.add(new Vector3f[] {
			new Vector3f(-1.0F, -1.0F, 1.0F),
			new Vector3f(-1.0F, 1.0F, 1.0F),
			new Vector3f(1.0F, 1.0F, 1.0F),
			new Vector3f(1.0F, -1.0F, 1.0F)});

		//yz -x
		faces.add(new Vector3f[] {
			new Vector3f(-1.0F, -1.0F, -1.0F),
			new Vector3f(-1.0F, 1.0F, -1.0F),
			new Vector3f(-1.0F, 1.0F, 1.0F),
			new Vector3f(-1.0F, -1.0F, 1.0F)});

		//yz +x
		faces.add(new Vector3f[] {
			new Vector3f(1.0F, -1.0F, -1.0F),
			new Vector3f(1.0F, 1.0F, -1.0F),
			new Vector3f(1.0F, 1.0F, 1.0F),
			new Vector3f(1.0F, -1.0F, 1.0F)});

		//xz -y
		faces.add(new Vector3f[] {
			new Vector3f(-1.0F, -1.0F, -1.0F),
			new Vector3f(-1.0F, -1.0F, 1.0F),
			new Vector3f(1.0F, -1.0F, 1.0F),
			new Vector3f(1.0F, -1.0F, -1.0F)});

		//xz +y
		faces.add(new Vector3f[] {
			new Vector3f(-1.0F, 1.0F, -1.0F),
			new Vector3f(-1.0F, 1.0F, 1.0F),
			new Vector3f(1.0F, 1.0F, 1.0F),
			new Vector3f(1.0F, 1.0F, -1.0F)});

		float scale = this.getQuadSize(partialTicks);
		float u0 = this.getU0();
		float u1 = this.getU1();
		float v0 = this.getV0();
		float v1 = this.getV1();
		if (sprite != null) {
			u0 = sprite.getU0();
			u1 = sprite.getU1();
			v0 = sprite.getV0();
			v1 = sprite.getV1();
		}
		int color = ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol);
		int lightCoords = this.getLightCoords(partialTicks);
		if (lightCoords > 0) {
			lastNonZeroBrightness = lightCoords;
		} else {
			lightCoords = lastNonZeroBrightness;
		}
		for (Vector3fc[] entryFace : faces) {
			state.add(getLayer(), x, y, z, quaternion.x, quaternion.y, quaternion.z, quaternion.w, scale, u0, u1, v0, v1, color, lightCoords, entryFace);
		}

	}

	@Override
	public Layer getLayer() {
		return SORTED_OPAQUE_BLOCK;
	}

	@Override
	public ParticleRenderType getGroup() {
		return SORTED_OPAQUE_BLOCK_TYPE;
	}
}
