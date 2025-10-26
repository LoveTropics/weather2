package weather2.weathersystem.tornado;

import net.minecraft.nbt.CompoundTag;

/**
 * Defines the shape and other characteristics of a tornado
 */
public class ActiveTornadoConfig {

    private float radiusOfBase;
    //incremental size of radius per layer
    private float radiusIncreasePerLayer;
    private float height;
    private float spinSpeed;
    private float entityPullDistXZ;
    private float entityPullDistXZForY;

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("radiusOfBase", radiusOfBase);
        tag.putFloat("radiusIncreasePerLayer", radiusIncreasePerLayer);
        tag.putFloat("height", height);
        tag.putFloat("spinSpeed", spinSpeed);
        tag.putFloat("entityPullDistXZ", entityPullDistXZ);
        tag.putFloat("entityPullDistXZForY", entityPullDistXZForY);
        return tag;
    }

    public static ActiveTornadoConfig deserialize(CompoundTag tag) {
        ActiveTornadoConfig config = new ActiveTornadoConfig();
		config.setRadiusOfBase(tag.getFloatOr("radiusOfBase", 0));
		config.setRadiusIncreasePerLayer(tag.getFloatOr("radiusIncreasePerLayer", 0));
		config.setHeight(tag.getFloatOr("height", 0));
		config.setSpinSpeed(tag.getFloatOr("spinSpeed", 0));
		config.setEntityPullDistXZ(tag.getFloatOr("entityPullDistXZ", 0));
		config.setEntityPullDistXZForY(tag.getFloatOr("entityPullDistXZForY", 0));
        return config;
    }

    public float getRadiusOfBase() {
        return radiusOfBase;
    }

    public ActiveTornadoConfig setRadiusOfBase(float radiusOfBase) {
        this.radiusOfBase = radiusOfBase;
        return this;
    }

    public float getRadiusIncreasePerLayer() {
        return radiusIncreasePerLayer;
    }

    public ActiveTornadoConfig setRadiusIncreasePerLayer(float radiusIncreasePerLayer) {
        this.radiusIncreasePerLayer = radiusIncreasePerLayer;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public ActiveTornadoConfig setHeight(float height) {
        this.height = height;
        return this;
    }

    public float getSpinSpeed() {
        return spinSpeed;
    }

    public ActiveTornadoConfig setSpinSpeed(float spinSpeed) {
        this.spinSpeed = spinSpeed;
        return this;
    }

    public float getEntityPullDistXZ() {
        return entityPullDistXZ;
    }

    public ActiveTornadoConfig setEntityPullDistXZ(float entityPullDistXZ) {
        this.entityPullDistXZ = entityPullDistXZ;
        return this;
    }

    public float getEntityPullDistXZForY() {
        return entityPullDistXZForY;
    }

    public ActiveTornadoConfig setEntityPullDistXZForY(float entityPullDistXZForY) {
        this.entityPullDistXZForY = entityPullDistXZForY;
        return this;
    }
}
