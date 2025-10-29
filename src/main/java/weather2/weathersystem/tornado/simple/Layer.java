package weather2.weathersystem.tornado.simple;

import extendedrenderer.particle.entity.PivotingParticle;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;

import java.util.ArrayList;
import java.util.List;

public class Layer {

    private List<PivotingParticle> listParticles;
    private List<PivotingParticle> listParticlesExtra;
    private Vec3 pos = Vec3.ZERO;
    private float rotation;

    public Layer(Vec3 pos) {
        this.pos = new Vec3(pos.x, pos.y, pos.z);

        if (EffectiveSide.get() == LogicalSide.CLIENT) {
            initClient();
        }
    }

    public void initClient() {
        listParticles = new ArrayList<>();
        listParticlesExtra = new ArrayList<>();
    }

    public List<PivotingParticle> getListParticles() {
        return listParticles;
    }

    public void setListParticles(List<PivotingParticle> listParticles) {
        this.listParticles = listParticles;
    }

    public List<PivotingParticle> getListParticlesExtra() {
        return listParticlesExtra;
    }

    public void setListParticlesExtra(List<PivotingParticle> listParticlesExtra) {
        this.listParticlesExtra = listParticlesExtra;
    }

    public Vec3 getPos() {
        return pos;
    }

    public void setPos(Vec3 pos) {
        this.pos = pos;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
