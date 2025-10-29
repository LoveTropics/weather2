package weather2.weathersystem.tornado.simple;

import com.corosus.coroutil.util.CULog;
import extendedrenderer.particle.entity.PivotingParticle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import weather2.Weather;
import weather2.weathersystem.storm.StormObject;
import weather2.weathersystem.tornado.ActiveTornadoConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TornadoFunnelSimple {

    protected ActiveTornadoConfig config;

    public Vec3 pos = new Vec3(0, 0, 0);
    public List<Layer> listLayers = new ArrayList<>();

    protected float heightPerLayer = 1F;

    protected StormObject stormObject;

    protected float targetSizeRadius = 0;
    protected float sizeRadiusRate = 0;
    protected float renderDistCutoff = 50;

    public TornadoFunnelSimple(ActiveTornadoConfig config, StormObject stormObject) {
        this.config = config;
        this.stormObject = stormObject;
        config.setRadiusOfBase(stormObject.tornadoHelper.getTornadoBaseSize() / 2);
    }

    public void init() {
        listLayers.clear();
    }

    public void tick() {
        if (stormObject.isPet()) {
            heightPerLayer = 0.2F;
        }

        //TESTING
        //config.setEntityPullDistXZForY(90);

        //dynamic sizing
        targetSizeRadius = stormObject.tornadoHelper.getTornadoBaseSize() / 2;
        sizeRadiusRate = 0.01F;

        if (config.getRadiusOfBase() != targetSizeRadius) {
            //CULog.dbg("tornado size transitioning: " + config.getRadiusOfBase());
            if (config.getRadiusOfBase() < targetSizeRadius) {
                config.setRadiusOfBase(config.getRadiusOfBase() + sizeRadiusRate);
                if (config.getRadiusOfBase() > targetSizeRadius) config.setRadiusOfBase(targetSizeRadius);
            } else {
                config.setRadiusOfBase(config.getRadiusOfBase() - sizeRadiusRate);
                if (config.getRadiusOfBase() < targetSizeRadius) config.setRadiusOfBase(targetSizeRadius);
            }
        }

        int layers = (int) (config.getHeight() / heightPerLayer);
        float radiusMax = config.getRadiusOfBase() + (config.getRadiusIncreasePerLayer() * (layers+1));

        for (int i = 0; i < layers; i++) {

            //grow layer count as height increases
            if (i >= listLayers.size()) {
                listLayers.add(new Layer(stormObject.posBaseFormationPos));
            }

            /**
             * get radius for current layer
             * convert to circumference (c = r2 * pi)
             * count = space per particle / circumference
             */

            float radius = config.getRadiusOfBase() + (config.getRadiusIncreasePerLayer() * (i));

            Vec3 posLayer = listLayers.get(i).getPos();

            float relYDown1 = (heightPerLayer * (radius / radiusMax));

            Vec3 posLayerLower;
            if (i == 0) {
                posLayerLower = new Vec3(pos.x, pos.y, pos.z);
            } else {
                Vec3 temp = listLayers.get(i-1).getPos();
                posLayerLower = new Vec3(temp.x, temp.y + relYDown1, temp.z);
            }

            double dist = posLayer.distanceTo(posLayerLower);
            //easy way to fix the spawning at 0,0 issue
            if (dist > 50) {
                //CULog.dbg("teleporting tornado layer to lower piece");
                listLayers.get(i).setPos(new Vec3(posLayerLower.x, posLayerLower.y, posLayerLower.z));
            } else if (dist > 0.1F * (radius / radiusMax)) {
                double dynamicSpeed = 15F * (Math.min(30F, dist) / 30F);
                double speed = dynamicSpeed;//0.01F;
                Vec3 moveVec = posLayer.vectorTo(posLayerLower).normalize().multiply(speed, speed * 1F, speed);
                Vec3 newPos = posLayer.add(moveVec);
                listLayers.get(i).setPos(new Vec3(newPos.x, newPos.y, newPos.z));
            }
        }

        Level level = stormObject.manager.getWorld();

        if (stormObject.isSharknado()) {
            if (!level.isClientSide()) {
                if (level.getGameTime() % 20 == 0) {
                    Entity ent = null;
                    if (Weather.isLoveTropicsInstalled()) {
                        /*EntityType type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse("tropicraft:hammerhead"));
                        if (type != null) {
                            ent = new SharkEntity(type, level);
                        }*/

                    }
                    if (ent == null) {
                        if (Weather.isLoveTropicsInstalled()) {
                            CULog.dbg("failed to create shark, falling back to dolphin");
                        }

                        ent = new Dolphin(EntityType.DOLPHIN, level);
                    }
                    Vec3 posRand = new Vec3(pos.x + 0, pos.y + 25, pos.z - 5);
                    ent.setPos(posRand);
                    ent.setDeltaMovement(3F, 0, 0);
                    level.addFreshEntity(ent);
                }
            }
        }
    }

    public void tickClient() {
    }

    public void cleanupList(List<PivotingParticle> list, int particlesPerLayer) {
        Iterator<PivotingParticle> it = list.iterator();
        float index = 0;
        while (it.hasNext()) {
            PivotingParticle particle = it.next();
            if (!particle.isAlive() || index >= particlesPerLayer) {
                particle.remove();
                it.remove();
            } else {
                index++;
            }
        }
    }

    public Vec3 getPosTop() {
        if (listLayers.size() == 0) return pos;
        return listLayers.get(listLayers.size()-1).getPos();
    }

    public StormObject getStormObject() {
        return stormObject;
    }

    public void setStormObject(StormObject stormObject) {
        this.stormObject = stormObject;
    }

    public void cleanup() {
        listLayers.clear();
    }

    /**
     * Dramatic version for effect
     */
    public void cleanupClient() {
        for (int i = 0; i < listLayers.size(); i++) {
            listLayers.get(i).getListParticles().stream().forEach(pivotingParticle -> disperseParticleSmoothly(pivotingParticle, true));
            listLayers.get(i).getListParticlesExtra().stream().forEach(pivotingParticle -> disperseParticleSmoothly(pivotingParticle, true));
            listLayers.get(i).getListParticles().clear();
            listLayers.get(i).getListParticlesExtra().clear();
        }
    }

    public void fadeOut() {
        for (int i = 0; i < listLayers.size(); i++) {
            listLayers.get(i).getListParticles().stream().forEach(pivotingParticle -> disperseParticleSmoothly(pivotingParticle, false));
            listLayers.get(i).getListParticlesExtra().stream().forEach(pivotingParticle -> disperseParticleSmoothly(pivotingParticle, false));
            listLayers.get(i).getListParticles().clear();
            listLayers.get(i).getListParticlesExtra().clear();
        }
    }

    public void disperseParticleSmoothly(PivotingParticle pivotingParticle, boolean explode) {
        pivotingParticle.prevRotationYaw = pivotingParticle.rotationYaw;
        pivotingParticle.setPivotPrev(pivotingParticle.getPivot());
        pivotingParticle.setPivotRotPrev(pivotingParticle.getPivotRot());
        Random rand = new Random();
        if (explode) {
            pivotingParticle.setMotionX((rand.nextFloat() - rand.nextFloat()) * 2F);
            pivotingParticle.setMotionZ((rand.nextFloat() - rand.nextFloat()) * 2F);
        } else {
            pivotingParticle.setMotionX((rand.nextFloat() - rand.nextFloat()) * 0.4F);
            pivotingParticle.setMotionZ((rand.nextFloat() - rand.nextFloat()) * 0.4F);
        }
        pivotingParticle.setAge(100);
        pivotingParticle.setMaxAge(200);
        pivotingParticle.setTicksFadeOutMax(80);
        pivotingParticle.spinFast = false;
    }

    public void cleanupClientQuick() {
        for (int i = 0; i < listLayers.size(); i++) {
            listLayers.get(i).getListParticles().stream().forEach(pivotingParticle -> pivotingParticle.remove());
            listLayers.get(i).getListParticlesExtra().stream().forEach(pivotingParticle -> pivotingParticle.remove());
            listLayers.get(i).getListParticles().clear();
            listLayers.get(i).getListParticlesExtra().clear();
        }
    }

    public ActiveTornadoConfig getConfig() {
        return config;
    }

    public void setConfig(ActiveTornadoConfig config) {
        this.config = config;
    }
}
