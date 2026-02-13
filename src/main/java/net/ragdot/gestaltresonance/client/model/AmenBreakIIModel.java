package net.ragdot.gestaltresonance.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.model.ModelPart;
import net.ragdot.gestaltresonance.entities.AmenBreak;

public class AmenBreakIIModel extends SinglePartEntityModel<AmenBreak> {

	private final ModelPart root;
	private final ModelPart AmenBreak;
	private final ModelPart Head;
	private final ModelPart Torso;
	private final ModelPart BodyT;
	private final ModelPart BodyB;
	private final ModelPart ArmRight;
	private final ModelPart LowerArmRight;
	private final ModelPart ArmLeft;
	private final ModelPart LowerArmLeft;
	private final ModelPart LegLeft;
	private final ModelPart LegRight;
	
	public AmenBreakIIModel(ModelPart root) {
		this.root = root;
		this.AmenBreak = root.getChild("AmenBreak");
		this.Head = this.AmenBreak.getChild("Head");
		this.Torso = this.AmenBreak.getChild("Torso");
		this.BodyT = this.Torso.getChild("BodyT");
		this.BodyB = this.Torso.getChild("BodyB");
		this.ArmRight = this.AmenBreak.getChild("ArmRight");
		this.LowerArmRight = this.ArmRight.getChild("LowerArmRight");
		this.ArmLeft = this.AmenBreak.getChild("ArmLeft");
		this.LowerArmLeft = this.ArmLeft.getChild("LowerArmLeft");
		this.LegLeft = this.AmenBreak.getChild("LegLeft");
		this.LegRight = this.AmenBreak.getChild("LegRight");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData AmenBreak = modelPartData.addChild("AmenBreak", ModelPartBuilder.create(), ModelTransform.of(0.0F, 19.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData Head = AmenBreak.addChild("Head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -29.0F, 0.0F));

		ModelPartData Beak_r1 = Head.addChild("Beak_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.6F, 5.6F, 7.3F, -0.5109F, 0.6051F, -0.3624F));

		ModelPartData FeatherPetal3R_r1 = Head.addChild("FeatherPetal3R_r1", ModelPartBuilder.create().uv(42, 51).mirrored().cuboid(-2.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.0396F, 0.7417F, 0.0404F, -0.4304F, 0.463F, 1.6863F));

		ModelPartData FeatherPetal3L_r1 = Head.addChild("FeatherPetal3L_r1", ModelPartBuilder.create().uv(42, 51).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-4.0396F, 0.7417F, 0.0404F, -0.4304F, -0.463F, -1.6863F));

		ModelPartData FeatherPetal2R_r1 = Head.addChild("FeatherPetal2R_r1", ModelPartBuilder.create().uv(42, 51).mirrored().cuboid(-2.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(5.4397F, -2.2583F, 1.4404F, -0.5294F, -0.1812F, 0.7218F));

		ModelPartData FeatherPetal2L_r1 = Head.addChild("FeatherPetal2L_r1", ModelPartBuilder.create().uv(42, 51).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.4397F, -2.2583F, 1.4404F, -0.5294F, 0.1812F, -0.7218F));

		ModelPartData FeatherPetal1R_r1 = Head.addChild("FeatherPetal1R_r1", ModelPartBuilder.create().uv(42, 51).mirrored().cuboid(-2.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.3396F, -4.2583F, 1.4404F, -0.1472F, -0.5098F, -0.0951F));

		ModelPartData FeatherPetal1L_r1 = Head.addChild("FeatherPetal1L_r1", ModelPartBuilder.create().uv(42, 51).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-4.3396F, -4.2583F, 1.4404F, -0.1472F, 0.5098F, 0.0951F));

		ModelPartData FeatherPetalM_r1 = Head.addChild("FeatherPetalM_r1", ModelPartBuilder.create().uv(42, 51).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.1603F, -6.0583F, 0.0404F, -0.2501F, 0.1568F, 0.7626F));

		ModelPartData EYEL_r1 = Head.addChild("EYEL_r1", ModelPartBuilder.create().uv(27, 3).cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 3.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.of(-4.1F, 1.337F, 5.4395F, -0.4473F, 0.6438F, -0.2955F));

		ModelPartData EYER_r1 = Head.addChild("EYER_r1", ModelPartBuilder.create().uv(27, 3).mirrored().cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 3.0F, 3.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(3.1F, 1.337F, 6.0395F, -0.5724F, -0.7978F, 0.4053F));

		ModelPartData HeadInside_r1 = Head.addChild("HeadInside_r1", ModelPartBuilder.create().uv(0, 16).cuboid(-4.5F, -3.0F, -4.5F, 9.0F, 7.0F, 9.0F, new Dilation(-1.1F))
		.uv(0, 0).cuboid(-4.5F, -3.0F, -4.5F, 9.0F, 7.0F, 9.0F, new Dilation(-0.1F)), ModelTransform.of(-0.2902F, -0.4F, 4.0857F, -0.4578F, 0.6286F, -0.3257F));

		ModelPartData Torso = AmenBreak.addChild("Torso", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -18.0F, 0.0F));

		ModelPartData BodyT = Torso.addChild("BodyT", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData BodyTop_r1 = BodyT.addChild("BodyTop_r1", ModelPartBuilder.create().uv(35, 27).cuboid(-3.0F, -8.0F, -1.0F, 6.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -2.0F, -0.2618F, 0.0F, 0.0F));

		ModelPartData BodyB = Torso.addChild("BodyB", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData BodyMid_r1 = BodyB.addChild("BodyMid_r1", ModelPartBuilder.create().uv(50, 14).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.7F, -0.6F, -0.1309F, 0.0F, 0.0F));

		ModelPartData BodyLow_r1 = BodyB.addChild("BodyLow_r1", ModelPartBuilder.create().uv(56, 6).cuboid(-1.0F, 2.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.3F, -1.7F, 0.1745F, 0.0F, 0.0F));

		ModelPartData ArmRight = AmenBreak.addChild("ArmRight", ModelPartBuilder.create(), ModelTransform.of(4.5F, -24.5F, 0.8F, 0.0F, 0.0F, -0.192F));

		ModelPartData ArmRT_r1 = ArmRight.addChild("ArmRT_r1", ModelPartBuilder.create().uv(11, 33).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 1.0F, 11.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1719F, -0.0302F, -0.3901F));

		ModelPartData LowerArmRight = ArmRight.addChild("LowerArmRight", ModelPartBuilder.create(), ModelTransform.of(2.3205F, 13.6531F, -4.3033F, -0.3054F, 0.0F, 0.0F));

		ModelPartData PodR3_r1 = LowerArmRight.addChild("PodR3_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.7795F, -2.1531F, -2.1967F, -0.4028F, -0.0803F, 0.0342F));

		ModelPartData PodR2_r1 = LowerArmRight.addChild("PodR2_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.5795F, -0.1531F, -1.3967F, -0.3979F, 0.0544F, 0.1287F));

		ModelPartData PodR1_r1 = LowerArmRight.addChild("PodR1_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.2795F, 1.8469F, -0.3967F, -0.4094F, -0.0285F, 0.1546F));

		ModelPartData FingersRight_r1 = LowerArmRight.addChild("FingersRight_r1", ModelPartBuilder.create().uv(33, 16).mirrored().cuboid(0.0F, -2.0F, -3.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.2205F, 6.1469F, 5.2033F, 0.5672F, 0.0F, 0.0F));

		ModelPartData ArmRL_r1 = LowerArmRight.addChild("ArmRL_r1", ModelPartBuilder.create().uv(0, 31).mirrored().cuboid(-1.0F, -2.0F, -3.0F, 1.0F, 11.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.8795F, -3.7531F, 0.8033F, 0.3902F, -0.0393F, 0.1249F));

		ModelPartData ArmLeft = AmenBreak.addChild("ArmLeft", ModelPartBuilder.create(), ModelTransform.of(-6.5532F, -16.073F, -1.8754F, -0.0873F, 0.0F, 0.0F));

		ModelPartData ArmLT_r1 = ArmLeft.addChild("ArmLT_r1", ModelPartBuilder.create().uv(11, 33).cuboid(0.0F, -2.0F, -2.0F, 1.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.8532F, -8.2338F, 1.8331F, -0.2122F, 0.0487F, 0.4754F));

		ModelPartData LowerArmLeft = ArmLeft.addChild("LowerArmLeft", ModelPartBuilder.create(), ModelTransform.of(-0.7533F, 5.1887F, -0.096F, 0.1745F, 0.0F, 0.0F));

		ModelPartData PodL3_r1 = LowerArmLeft.addChild("PodL3_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.3935F, 1.5775F, -1.2709F, -1.8875F, -0.1514F, -0.0423F));

		ModelPartData PodL2_r1 = LowerArmLeft.addChild("PodL2_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.8935F, -2.4225F, -3.0709F, -1.8838F, -0.0269F, -0.083F));

		ModelPartData PodL1_r1 = LowerArmLeft.addChild("PodL1_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.8935F, -0.4225F, -2.2709F, -1.8878F, -0.1328F, 0.0434F));

		ModelPartData FingersLeft_r1 = LowerArmLeft.addChild("FingersLeft_r1", ModelPartBuilder.create().uv(33, 16).cuboid(-1.0F, -2.0F, -3.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.3065F, 5.5775F, 5.3291F, 0.5859F, 0.2103F, -0.2351F));

		ModelPartData ArmLL_r1 = LowerArmLeft.addChild("ArmLL_r1", ModelPartBuilder.create().uv(0, 31).cuboid(0.0F, -2.0F, -3.0F, 1.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.2935F, -3.7225F, 0.8291F, 0.3902F, 0.0393F, -0.1249F));

		ModelPartData LegLeft = AmenBreak.addChild("LegLeft", ModelPartBuilder.create(), ModelTransform.of(-1.2382F, -6.4606F, -1.6081F, 0.2104F, -0.1838F, 0.0433F));

		ModelPartData ToesLeft_r1 = LegLeft.addChild("ToesLeft_r1", ModelPartBuilder.create().uv(40, 21).cuboid(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(-1.4785F, 7.3871F, 2.2153F, -0.8557F, -0.133F, -0.0972F));

		ModelPartData LegLeftLow_r1 = LegLeft.addChild("LegLeftLow_r1", ModelPartBuilder.create().uv(55, 32).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.6F, 3.9F, 3.4F, -0.5672F, 0.0F, 0.0F));

		ModelPartData LegLeft_r1 = LegLeft.addChild("LegLeft_r1", ModelPartBuilder.create().uv(54, 21).cuboid(-1.0F, -3.5F, -1.5F, 2.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-1.3168F, -0.8463F, 2.5618F, 0.3927F, -0.2182F, 0.0F));

		ModelPartData LegRight = AmenBreak.addChild("LegRight", ModelPartBuilder.create(), ModelTransform.of(1.3832F, -6.5793F, -1.3333F, -0.3054F, 0.1745F, 0.0F));

		ModelPartData ToesRight_r1 = LegRight.addChild("ToesRight_r1", ModelPartBuilder.create().uv(40, 21).mirrored().cuboid(-1.0114F, 0.8978F, -1.2238F, 2.0F, 2.0F, 3.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(1.431F, 5.2824F, 4.4333F, -0.2603F, 0.1758F, 0.0076F));

		ModelPartData LegRightLow_r1 = LegRight.addChild("LegRightLow_r1", ModelPartBuilder.create().uv(55, 32).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.2787F, 3.5187F, 3.1252F, 0.1745F, 0.0F, 0.0F));

		ModelPartData LegLeft_r2 = LegRight.addChild("LegLeft_r2", ModelPartBuilder.create().uv(54, 21).mirrored().cuboid(-1.0F, -3.5F, -1.5F, 2.0F, 7.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.0851F, -1.735F, 1.3721F, 0.4363F, 0.2182F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	
	@Override
	public ModelPart getPart() {
		return root;
	}

	@Override
	public void setAngles(AmenBreak entity,
						  float limbSwing,
						  float limbSwingAmount,
						  float ageInTicks,
						  float netHeadYaw,
						  float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
  this.updateAnimation(entity.animationHelper.idleAnimationState, AmenBreakAnimation.Idle, ageInTicks, 1.0f);
  this.updateAnimation(entity.animationHelper.guardAnimationState, AmenBreakAnimation.Guard, ageInTicks, 1.0f);
  this.updateAnimation(entity.animationHelper.throwAnimationState, AmenBreakAnimation.Throw, ageInTicks, 1.2f);
  this.updateAnimation(entity.animationHelper.grabAnimationState, AmenBreakAnimation.grab, ageInTicks, 1.0f);
  this.updateAnimation(entity.animationHelper.windUpAnimationState, AmenBreakAnimation.WindUp, ageInTicks, 1.0f);
  this.updateAnimation(entity.animationHelper.punchAnimationState, AmenBreakAnimation.Punch, ageInTicks, 2.0f);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		root.render(matrices, vertexConsumer, light, overlay, color);
	}

}