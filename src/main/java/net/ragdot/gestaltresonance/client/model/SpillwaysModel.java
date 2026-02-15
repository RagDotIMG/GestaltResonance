package net.ragdot.gestaltresonance.client.model;


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.model.ModelPart;
import net.ragdot.gestaltresonance.entities.Spillways;


public class SpillwaysModel extends SinglePartEntityModel<Spillways> {

	private final ModelPart Spillways_model;
	private final ModelPart body_up;
	private final ModelPart torso_up;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart main;
	private final ModelPart hat_end;
	private final ModelPart arm_L;
	private final ModelPart arm_low_L;
	private final ModelPart hand_L;
	private final ModelPart arm_R;
	private final ModelPart arm_low_R;
	private final ModelPart hand_R;
	private final ModelPart torso_low;
	private final ModelPart body_low;
	private final ModelPart leg_L;
	private final ModelPart leg_low_L;
	private final ModelPart foot_L;
	private final ModelPart leg_R;
	private final ModelPart leg_low_R;
	private final ModelPart foot_R;
	public SpillwaysModel(ModelPart root) {
		this.Spillways_model = root.getChild("Spillways_model");
		this.body_up = this.Spillways_model.getChild("body_up");
		this.torso_up = this.body_up.getChild("torso_up");
		this.head = this.torso_up.getChild("head");
		this.hat = this.head.getChild("hat");
		this.main = this.hat.getChild("main");
		this.hat_end = this.main.getChild("hat_end");
		this.arm_L = this.torso_up.getChild("arm_L");
		this.arm_low_L = this.arm_L.getChild("arm_low_L");
		this.hand_L = this.arm_low_L.getChild("hand_L");
		this.arm_R = this.torso_up.getChild("arm_R");
		this.arm_low_R = this.arm_R.getChild("arm_low_R");
		this.hand_R = this.arm_low_R.getChild("hand_R");
		this.torso_low = this.body_up.getChild("torso_low");
		this.body_low = this.Spillways_model.getChild("body_low");
		this.leg_L = this.body_low.getChild("leg_L");
		this.leg_low_L = this.leg_L.getChild("leg_low_L");
		this.foot_L = this.leg_low_L.getChild("foot_L");
		this.leg_R = this.body_low.getChild("leg_R");
		this.leg_low_R = this.leg_R.getChild("leg_low_R");
		this.foot_R = this.leg_low_R.getChild("foot_R");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData Spillways_model = modelPartData.addChild("Spillways_model", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.4F, 0.1F));

		ModelPartData body_up = Spillways_model.addChild("body_up", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.2844F, -0.3573F));

		ModelPartData torso_up = body_up.addChild("torso_up", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5F, -3.4F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 29).cuboid(-2.0F, -1.7F, -1.0F, 4.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.3F, -0.7F));

		ModelPartData boob_r1 = torso_up.addChild("boob_r1", ModelPartBuilder.create().uv(0, 23).cuboid(-2.0F, -1.0F, -1.5F, 4.0F, 2.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.5672F, 0.0F, 0.0F));

		ModelPartData head = torso_up.addChild("head", ModelPartBuilder.create().uv(15, 25).cuboid(-2.0F, -3.5F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
				.uv(15, 34).cuboid(-1.5F, -1.9F, -1.9F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.1F, 0.7F));

		ModelPartData hat = head.addChild("hat", ModelPartBuilder.create(), ModelTransform.pivot(0.0799F, -3.5625F, -0.1049F));

		ModelPartData edge_w_r1 = hat.addChild("edge_w_r1", ModelPartBuilder.create().uv(10, 7).cuboid(-5.0F, -2.0F, 0.0F, 10.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.096F, 0.7399F, 0.0559F, 0.0F, 1.5708F, -1.0908F));

		ModelPartData edge_e_r1 = hat.addChild("edge_e_r1", ModelPartBuilder.create().uv(10, 7).cuboid(-5.0F, -2.0F, 0.0F, 10.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-3.2552F, 0.7399F, -0.0453F, 0.0F, -1.5708F, 1.0908F));

		ModelPartData edge_s_r1 = hat.addChild("edge_s_r1", ModelPartBuilder.create().uv(10, 7).cuboid(-5.0F, -2.0F, 0.0F, 10.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.1309F, 0.7399F, 3.1809F, 1.0908F, 0.0F, 0.0F));

		ModelPartData edge_n_r1 = hat.addChild("edge_n_r1", ModelPartBuilder.create().uv(10, 7).cuboid(-5.0F, -2.0F, 0.0F, 10.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0297F, 0.7399F, -3.1703F, -2.0508F, 0.0F, -3.1416F));

		ModelPartData main = hat.addChild("main", ModelPartBuilder.create().uv(19, 0).cuboid(-1.0F, -3.6F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0201F, -0.0875F, 0.0049F));

		ModelPartData side_L_r1 = main.addChild("side_L_r1", ModelPartBuilder.create().uv(19, 12).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0205F, -2.1F, -0.0205F, 0.0F, 0.7854F, 0.0F));

		ModelPartData side_R_r1 = main.addChild("side_R_r1", ModelPartBuilder.create().uv(19, 12).mirrored().cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.0007F, -2.1F, 0.0007F, -3.1416F, 0.7854F, 3.1416F));

		ModelPartData hat_end = main.addChild("hat_end", ModelPartBuilder.create(), ModelTransform.pivot(0.001F, -3.0F, 0.0F));

		ModelPartData tip_r1 = hat_end.addChild("tip_r1", ModelPartBuilder.create().uv(12, 11).cuboid(0.0F, -4.0F, -3.5F, 0.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.6F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData arm_L = torso_up.addChild("arm_L", ModelPartBuilder.create().uv(10, 0).cuboid(-0.25F, -0.6619F, -0.8918F, 2.0F, 4.0F, 2.0F, new Dilation(-0.2F))
				.uv(5, 5).cuboid(0.1F, 2.1381F, -0.3918F, 1.0F, 3.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(2.05F, -0.55F, 0.6F, -0.2531F, 0.0F, 0.0F));

		ModelPartData arm_low_L = arm_L.addChild("arm_low_L", ModelPartBuilder.create().uv(5, 10).cuboid(-0.5F, -0.0119F, -0.4918F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.6F, 5.2F, 0.1F));

		ModelPartData hand_L = arm_low_L.addChild("hand_L", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.1118F, -0.4918F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

		ModelPartData arm_R = torso_up.addChild("arm_R", ModelPartBuilder.create().uv(10, 0).mirrored().cuboid(-1.75F, -0.6619F, -0.8918F, 2.0F, 4.0F, 2.0F, new Dilation(-0.2F)).mirrored(false)
				.uv(5, 5).mirrored().cuboid(-1.1F, 2.1381F, -0.3918F, 1.0F, 3.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(-2.05F, -0.55F, 0.6F, -0.2531F, 0.0F, 0.0F));

		ModelPartData arm_low_R = arm_R.addChild("arm_low_R", ModelPartBuilder.create().uv(5, 10).mirrored().cuboid(-0.5F, -0.0119F, -0.4918F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-0.6F, 5.2F, 0.1F));

		ModelPartData hand_R = arm_low_R.addChild("hand_R", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-0.5F, -0.1118F, -0.4918F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

		ModelPartData torso_low = body_up.addChild("torso_low", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -3.6F, -1.5F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -0.4F, 0.3F));

		ModelPartData body_low = Spillways_model.addChild("body_low", ModelPartBuilder.create().uv(10, 12).cuboid(-1.0F, -0.2333F, -1.0667F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.5333F, -0.1333F));

		ModelPartData leg_L = body_low.addChild("leg_L", ModelPartBuilder.create().uv(28, 18).cuboid(-0.1F, -0.9F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.1F, 1.7667F, 0.0333F));

		ModelPartData leg_low_L = leg_L.addChild("leg_low_L", ModelPartBuilder.create().uv(5, 0).cuboid(-0.5525F, 0.0595F, 0.2436F, 1.0F, 3.0F, 1.0F, new Dilation(0.3F)), ModelTransform.pivot(0.8525F, 6.0405F, -0.7436F));

		ModelPartData foot_L = leg_low_L.addChild("foot_L", ModelPartBuilder.create().uv(0, 10).cuboid(-0.5525F, -0.0405F, -0.5564F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.1F, 3.1F, 0.8F));

		ModelPartData leg_R = body_low.addChild("leg_R", ModelPartBuilder.create().uv(28, 18).mirrored().cuboid(-1.9F, -0.9F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-1.1F, 1.7667F, 0.0333F));

		ModelPartData leg_low_R = leg_R.addChild("leg_low_R", ModelPartBuilder.create().uv(5, 0).mirrored().cuboid(-0.5F, 0.3F, 0.3F, 1.0F, 3.0F, 1.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.pivot(-0.8F, 5.8F, -0.8F));

		ModelPartData foot_R = leg_low_R.addChild("foot_R", ModelPartBuilder.create().uv(0, 10).mirrored().cuboid(-0.5F, -0.3F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.1F, 3.6F, 0.8F));
		return TexturedModelData.of(modelData, 128, 128);
	}
    @Override
    public void setAngles(Spillways entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Reset transforms each frame like Scorched Utopia
        this.getPart().traverse().forEach(ModelPart::resetTransform);


        this.updateAnimation(entity.animationHelper.idleAnimationState, SpillwaysAnimation.Idle, ageInTicks, 1f);
        this.updateAnimation(entity.animationHelper.guardAnimationState, SpillwaysAnimation.Guard, ageInTicks, 1f);
        this.updateAnimation(entity.animationHelper.throwAnimationState, SpillwaysAnimation.Throw, ageInTicks, 1f);
        this.updateAnimation(entity.animationHelper.grabAnimationState, SpillwaysAnimation.grab, ageInTicks, 1f);
        this.updateAnimation(entity.animationHelper.introAnimationState, SpillwaysAnimation.Intro, ageInTicks, 1f);
        this.updateAnimation(entity.animationHelper.windUpAnimationState, SpillwaysAnimation.WindUp, ageInTicks, 1f);
        this.updateAnimation(entity.animationHelper.punchAnimationState, SpillwaysAnimation.Punch, ageInTicks, 2f);
    }
	@Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        Spillways_model.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return this.Spillways_model;
    }
}