package weather2.client.entity.model;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import weather2.Weather;
import weather2.client.tile.state.WindTurbineRenderState;

public class WindTurbineModel extends Model<WindTurbineRenderState> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Weather.MODID, "wind_turbine"), "main");

    public WindTurbineModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(36, 2).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition shaft = root.addOrReplaceChild("shaft", CubeListBuilder.create().texOffs(0, 35).addBox(-1.0F, -31.0F, -1.0F, 2.0F, 29.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20).addBox(-2.0F, -30.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 33).addBox(-9.0F, -29.5F, -0.5F, 18.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 14).addBox(-0.5F, -29.5F, -9.0F, 1.0F, 1.0F, 18.0F, new CubeDeformation(0.0F))
            .texOffs(2, 16).addBox(-0.5F, -13.5F, -8.0F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(36, 0).addBox(-8.0F, -13.5F, -0.5F, 16.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(2, 16).addBox(-0.5F, -13.5F, -8.0F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 14).addBox(-2.0F, -14.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition fin_r1 = shaft.addOrReplaceChild("fin_r1", CubeListBuilder.create().texOffs(8, 35).addBox(-2.0F, -21.0F, -1.0F, 4.0F, 22.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, -8.0F, -0.4162F, 0.1863F, 0.3969F));

        PartDefinition fin_r2 = shaft.addOrReplaceChild("fin_r2", CubeListBuilder.create().texOffs(8, 35).addBox(-2.0F, -21.0F, -1.0F, 4.0F, 22.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, 8.0F, 0.4164F, 0.2075F, -0.436F));

        PartDefinition fin_r3 = shaft.addOrReplaceChild("fin_r3", CubeListBuilder.create().texOffs(34, 31).addBox(-1.0F, -21.0F, -2.0F, 2.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -13.0F, 0.0F, 0.4363F, 0.0F, 0.4712F));

        PartDefinition fin_r4 = shaft.addOrReplaceChild("fin_r4", CubeListBuilder.create().texOffs(34, 31).addBox(-1.0F, -21.0F, -2.0F, 2.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -13.0F, 0.0F, -0.4363F, 0.0F, -0.4712F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(WindTurbineRenderState state) {
        super.setupAnim(state);

        ModelPart root = root();
        root.x += 8;
        root.y += 8;
        root.z += 8;
        root.xRot += Math.toRadians(180);
        root.yRot += Math.toRadians(180);
        root.y -= 32;

        ModelPart top = root.getChild("base").getChild("top");
        if (top != null) {
            top.yRot = state.yRot;
        }
    }
}