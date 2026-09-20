package extendedrenderer;

import extendedrenderer.particle.entity.EntityRotFX;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;

public class WeatherParticleGroup extends ParticleGroup<EntityRotFX> {
    private final ParticleRenderType type;
    private final WeatherParticleRenderState particleTypeRenderState = new WeatherParticleRenderState();

    public WeatherParticleGroup(ParticleEngine engine, ParticleRenderType type) {
        super(engine);
        this.type = type;
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTickTime) {
        for (EntityRotFX particle : particles) {
            if (frustum.pointInFrustum(particle.x, particle.y, particle.z)) {
                try {
                    particle.extract(particleTypeRenderState, camera, partialTickTime);
                } catch (Throwable throwable) {
                    CrashReport report = CrashReport.forThrowable(throwable, "Rendering Particle");
                    CrashReportCategory category = report.addCategory("Particle being rendered");
                    category.setDetail("Particle", particle::toString);
                    category.setDetail("Particle Type", type::toString);
                    throw new ReportedException(report);
                }
            }
        }

        return particleTypeRenderState;
    }
}
