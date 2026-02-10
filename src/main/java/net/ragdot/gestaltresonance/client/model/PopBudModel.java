package net.ragdot.gestaltresonance.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.ragdot.gestaltresonance.projectile.PopBud;

public class PopBudModel extends EntityModel<PopBud> {
	private final ModelPart bb_main;
	public PopBudModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}
 public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        // Center the model around the origin so spinning occurs around the geometric center.
        // Shift Y by +2.25 (pixels) compared to the previous export so the piece is centered.
        ModelPartData bb_main = modelPartData.addChild(
                "bb_main",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-1.0F, -0.75F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -2.25F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );
        return TexturedModelData.of(modelData, 16, 16);
    }
	@Override
 public void setAngles(PopBud entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
 }
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		bb_main.render(matrices, vertexConsumer, light, overlay, color);
	}
}