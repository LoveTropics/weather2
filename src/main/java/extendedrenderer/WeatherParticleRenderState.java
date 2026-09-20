package extendedrenderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WeatherParticleRenderState extends QuadParticleRenderState {
    private static final int INITIAL_PARTICLE_CAPACITY = 1024;
    private static final int FLOATS_PER_PARTICLE = 24;
    private static final int INTS_PER_PARTICLE = 2;
    private final Map<SingleQuadParticle.Layer, WeatherParticleRenderState.Storage> particles = new HashMap<>();
    private int particleCount;

    final Vector3fc[] defaultVertices = {
        new Vector3f(1.0F, -1.0F, 0.0F),
        new Vector3f(1.0F, 1.0F, 0.0F),
        new Vector3f(-1.0F, 1.0F, 0.0F),
        new Vector3f(-1.0F, -1.0F, 0.0F)
    };
    public void add(SingleQuadParticle.Layer layer, float x, float y, float z, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords) {
        add(layer, x, y, z, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords, defaultVertices);
    }

    public void add(SingleQuadParticle.Layer layer, float x, float y, float z, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords, Vector3fc[] vertices) {
        particles
            .computeIfAbsent(layer, ignored -> new WeatherParticleRenderState.Storage())
            .add(x, y, z, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords, vertices);
        particleCount++;
    }

    @Override
    public void clear() {
        particles.values().forEach(WeatherParticleRenderState.Storage::clear);
        particleCount = 0;
    }

    public boolean isEmpty() {
        return particleCount == 0;
    }

    public void buildLayer(SingleQuadParticle.Layer layer, VertexConsumer bufferBuilder) {
        WeatherParticleRenderState.Storage storage = particles.get(layer);
        if (storage != null) {
            storage.forEachParticle(
                (x, y, z, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords, vertices) -> renderRotatedQuad(
                    bufferBuilder, x, y, z, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords, vertices
                )
            );
        }
    }

    public Set<SingleQuadParticle.Layer> layers() {
        return particles.keySet();
    }

    protected void renderRotatedQuad(VertexConsumer builder, float x, float y, float z, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords, Vector3fc[] vertices) {
        Quaternionf rotation = new Quaternionf(xRot, yRot, zRot, wRot);
        renderVertex(builder, rotation, x, y, z, vertices[0].x(), vertices[0].y(), vertices[0].z(), scale, u1, v1, color, lightCoords);
        renderVertex(builder, rotation, x, y, z, vertices[1].x(), vertices[1].y(), vertices[1].z(), scale, u1, v0, color, lightCoords);
        renderVertex(builder, rotation, x, y, z, vertices[2].x(), vertices[2].y(), vertices[2].z(), scale, u0, v0, color, lightCoords);
        renderVertex(builder, rotation, x, y, z, vertices[3].x(), vertices[3].y(), vertices[3].z(), scale, u0, v1, color, lightCoords);
    }

    private void renderVertex(
        VertexConsumer builder, Quaternionf rotation, float x, float y, float z, float nx, float ny, float nz, float scale, float u, float v, int color, int lightCoords
    ) {
        Vector3f scratch = new Vector3f(nx, ny, nz).rotate(rotation).mul(scale).add(x, y, z);
        builder.addVertex(scratch.x(), scratch.y(), scratch.z()).setUv(u, v).setColor(color).setLight(lightCoords);
    }

    @Override
    public void submit(SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (particleCount > 0) {
            submitNodeCollector.submitQuadParticleGroup(this);
        }
    }

    @FunctionalInterface
    public interface ParticleConsumer {
        void consume(final float x, final float y, final float z, final float xRot, final float yRot, final float zRot, final float wRot, final float scale, final float u0, final float u1, final float v0, final float v1, final int color, final int lightCoords, final Vector3fc[] vertices);
    }

    private static class Storage {
        private int capacity = INITIAL_PARTICLE_CAPACITY;
        private float[] floatValues = new float[FLOATS_PER_PARTICLE * INITIAL_PARTICLE_CAPACITY];
        private int[] intValues = new int[INTS_PER_PARTICLE * INITIAL_PARTICLE_CAPACITY];
        private int currentParticleIndex;

        public void add(float x, float y, float z, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords, Vector3fc[] vertices) {
            if (currentParticleIndex >= capacity) {
                grow();
            }

            int index = currentParticleIndex * FLOATS_PER_PARTICLE;
            floatValues[index++] = x;
            floatValues[index++] = y;
            floatValues[index++] = z;
            floatValues[index++] = xRot;
            floatValues[index++] = yRot;
            floatValues[index++] = zRot;
            floatValues[index++] = wRot;
            floatValues[index++] = scale;
            floatValues[index++] = u0;
            floatValues[index++] = u1;
            floatValues[index++] = v0;
            floatValues[index++] = v1;
            floatValues[index++] = vertices[0].x();
            floatValues[index++] = vertices[0].y();
            floatValues[index++] = vertices[0].z();
            floatValues[index++] = vertices[1].x();
            floatValues[index++] = vertices[1].y();
            floatValues[index++] = vertices[1].z();
            floatValues[index++] = vertices[2].x();
            floatValues[index++] = vertices[2].y();
            floatValues[index++] = vertices[2].z();
            floatValues[index++] = vertices[3].x();
            floatValues[index++] = vertices[3].y();
            floatValues[index] = vertices[3].z();
            index = currentParticleIndex * INTS_PER_PARTICLE;
            intValues[index++] = color;
            intValues[index] = lightCoords;
            currentParticleIndex++;
        }

        public void forEachParticle(WeatherParticleRenderState.ParticleConsumer consumer) {
            for (int particleIndex = 0; particleIndex < this.currentParticleIndex; particleIndex++) {
                int floatIndex = particleIndex * FLOATS_PER_PARTICLE;
                int intIndex = particleIndex * INTS_PER_PARTICLE;
                consumer.consume(
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    floatValues[floatIndex++],
                    intValues[intIndex++],
                    intValues[intIndex],
                    new Vector3fc[] {
                        new Vector3f(floatValues[floatIndex++], floatValues[floatIndex++], floatValues[floatIndex++]),
                        new Vector3f(floatValues[floatIndex++], floatValues[floatIndex++], floatValues[floatIndex++]),
                        new Vector3f(floatValues[floatIndex++], floatValues[floatIndex++], floatValues[floatIndex++]),
                        new Vector3f(floatValues[floatIndex++], floatValues[floatIndex++], floatValues[floatIndex])
                    }
                );
            }
        }

        public void clear() {
            currentParticleIndex = 0;
        }

        private void grow() {
            capacity *= 2;
            floatValues = Arrays.copyOf(floatValues, capacity * FLOATS_PER_PARTICLE);
            intValues = Arrays.copyOf(intValues, capacity * INTS_PER_PARTICLE);
        }

        public int count() {
            return currentParticleIndex;
        }
    }
}
