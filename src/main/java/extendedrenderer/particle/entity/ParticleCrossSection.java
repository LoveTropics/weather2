package extendedrenderer.particle.entity;

import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleCrossSection extends ParticleTexFX {

    public ParticleCrossSection(Level worldIn, double posXIn, double posYIn,
                                double posZIn, double mX, double mY, double mZ,
                                TextureAtlasSprite par8Item) {
        super((ClientLevel) worldIn, posXIn, posYIn, posZIn, mX, mY, mZ, par8Item);
    }

    @Override
    public void extract(QuadParticleRenderState state, Camera renderInfo, float partialTicks) {

        Vec3 Vector3d = renderInfo.position();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - Vector3d.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - Vector3d.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - Vector3d.z());
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

        Vector3f[] avector3f2 = new Vector3f[] {
            new Vector3f(0.0F, -1.0F, -1.0F),
            new Vector3f(0.0F, 1.0F, -1.0F),
            new Vector3f(0.0F, 1.0F, 1.0F),
            new Vector3f(0.0F, -1.0F, 1.0F)};

        Vector3f[] avector3f3 = new Vector3f[] {
            new Vector3f(-1.0F, 0.0F, -1.0F),
            new Vector3f(-1.0F, 0.0F, 1.0F),
            new Vector3f(1.0F, 0.0F, 1.0F),
            new Vector3f(1.0F, 0.0F, -1.0F)};

        float scale = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f2[i];
            vector3f.rotate(quaternion);
            vector3f.mul(scale);
            vector3f.add(x, y, z);
        }

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f3[i];
            vector3f.rotate(quaternion);
            vector3f.mul(scale);
            vector3f.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int color = ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol);
        int lightCoords = this.getLightCoords(partialTicks);
        if (lightCoords > 0) {
            lastNonZeroBrightness = lightCoords;
        } else {
            lightCoords = lastNonZeroBrightness;
        }

        state.add(getLayer(), x, y, z, quaternion.x, quaternion.y, quaternion.z, quaternion.w, scale, u0, u1, v0, v1, color, lightCoords);

        buffer.addVertex(avector3f2[0].x(), avector3f2[0].y(), avector3f2[0].z()).setUv(u1, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);
        buffer.addVertex(avector3f2[1].x(), avector3f2[1].y(), avector3f2[1].z()).setUv(u1, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);
        buffer.addVertex(avector3f2[2].x(), avector3f2[2].y(), avector3f2[2].z()).setUv(u0, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);
        buffer.addVertex(avector3f2[3].x(), avector3f2[3].y(), avector3f2[3].z()).setUv(u0, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);

        buffer.addVertex(avector3f3[0].x(), avector3f3[0].y(), avector3f3[0].z()).setUv(u1, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);
        buffer.addVertex(avector3f3[1].x(), avector3f3[1].y(), avector3f3[1].z()).setUv(u1, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);
        buffer.addVertex(avector3f3[2].x(), avector3f3[2].y(), avector3f3[2].z()).setUv(u0, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);
        buffer.addVertex(avector3f3[3].x(), avector3f3[3].y(), avector3f3[3].z()).setUv(u0, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(lightCoords);

    }
}
