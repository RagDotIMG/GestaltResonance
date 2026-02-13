package net.ragdot.gestaltresonance.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.model.ModelPart;
import net.ragdot.gestaltresonance.entities.AmenBreak;

public class AmenBreakModel extends SinglePartEntityModel<AmenBreak> {

	private final ModelPart root;
	private final ModelPart AmenBreak;
	private final ModelPart Torso;
	private final ModelPart BodyT;
	private final ModelPart Head;
	private final ModelPart Ribbon;
	private final ModelPart ArmRight;
	private final ModelPart LowerArmRight;
	private final ModelPart ArmLeft;
	private final ModelPart LowerArmLeft;
	private final ModelPart BodyB;
	private final ModelPart Legs;
	private final ModelPart LegLeft;
	private final ModelPart LegRight;

	public AmenBreakModel(ModelPart root) {
		this.root = root;
		this.AmenBreak = root.getChild("AmenBreak");
		this.Torso = this.AmenBreak.getChild("Torso");
		this.BodyT = this.Torso.getChild("BodyT");
		this.Head = this.BodyT.getChild("Head");
		this.Ribbon = this.Head.getChild("Ribbon");
		this.ArmRight = this.BodyT.getChild("ArmRight");
		this.LowerArmRight = this.ArmRight.getChild("LowerArmRight");
		this.ArmLeft = this.BodyT.getChild("ArmLeft");
		this.LowerArmLeft = this.ArmLeft.getChild("LowerArmLeft");
		this.BodyB = this.Torso.getChild("BodyB");
		this.Legs = this.AmenBreak.getChild("Legs");
		this.LegLeft = this.Legs.getChild("LegLeft");
		this.LegRight = this.Legs.getChild("LegRight");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData AmenBreak = modelPartData.addChild("AmenBreak", ModelPartBuilder.create(), ModelTransform.of(0.0F, 21.6F, 1.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData Torso = AmenBreak.addChild("Torso", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -8.7F, 0.5F));

		ModelPartData BodyT = Torso.addChild("BodyT", ModelPartBuilder.create(), ModelTransform.pivot(0.1F, -9.8F, -1.0F));

		ModelPartData BodyTop_r1 = BodyT.addChild("BodyTop_r1", ModelPartBuilder.create().uv(1, 16).cuboid(-2.0F, -8.0F, -1.5F, 4.0F, 8.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.1F, 0.0294F, -0.017F, -0.2618F, 0.0F, 0.0F));

		ModelPartData Head = BodyT.addChild("Head", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0059F, -6.8609F, -4.0199F, 12.0F, 7.0F, 8.0F, new Dilation(-0.1F))
				.uv(40, 9).cuboid(-6.0059F, -6.1609F, 3.3801F, 12.0F, 6.0F, 0.0F, new Dilation(-0.2F))
				.uv(32, 0).cuboid(-6.0059F, -5.8609F, 2.1801F, 12.0F, 6.0F, 1.0F, new Dilation(-0.2F))
				.uv(16, 40).cuboid(-7.0059F, -7.8609F, -5.0199F, 14.0F, 14.0F, 10.0F, new Dilation(-0.6F)), ModelTransform.pivot(-0.0941F, -7.2391F, 2.5199F));

		ModelPartData HeadScarfDeco1_r1 = Head.addChild("HeadScarfDeco1_r1", ModelPartBuilder.create().uv(56, 40).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.8705F, -5.5956F, -1.6007F, -0.028F, -0.7477F, 1.1494F));

		ModelPartData Ribbon = Head.addChild("Ribbon", ModelPartBuilder.create(), ModelTransform.pivot(-6.3994F, -5.011F, -1.4009F));

		ModelPartData HeadScarfDeco2_r1 = Ribbon.addChild("HeadScarfDeco2_r1", ModelPartBuilder.create().uv(56, 43).cuboid(0.0038F, -1.1531F, 0.2268F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4668F, -0.1454F, 0.809F, -1.5839F, -0.2811F, 1.7717F));

		ModelPartData HeadScarfDeco3_r1 = Ribbon.addChild("HeadScarfDeco3_r1", ModelPartBuilder.create().uv(59, 45).cuboid(-1.1535F, 0.0915F, 0.3325F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5332F, -0.1546F, -0.509F, -2.0248F, -1.1276F, 2.3836F));

		ModelPartData ArmRight = BodyT.addChild("ArmRight", ModelPartBuilder.create(), ModelTransform.pivot(1.7744F, -7.022F, 1.9491F));

		ModelPartData ArmRT_r1 = ArmRight.addChild("ArmRT_r1", ModelPartBuilder.create().uv(11, 33).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 1.0F, 11.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.6256F, 1.622F, 0.6509F, -0.1719F, -0.0302F, -0.3901F));

		ModelPartData LowerArmRight = ArmRight.addChild("LowerArmRight", ModelPartBuilder.create(), ModelTransform.of(4.5461F, 9.9751F, -1.6524F, -0.3054F, 0.0F, 0.0F));

		ModelPartData PodR3_r1 = LowerArmRight.addChild("PodR3_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.1795F, 3.503F, -2.5104F, -0.4028F, -0.0803F, 0.0342F));

		ModelPartData PodR2_r1 = LowerArmRight.addChild("PodR2_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.0205F, 5.503F, -1.7104F, -0.3979F, 0.0544F, 0.1287F));

		ModelPartData PodR1_r1 = LowerArmRight.addChild("PodR1_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.3205F, 7.503F, -0.7104F, -0.4094F, -0.0285F, 0.1546F));

		ModelPartData FingersRight_r1 = LowerArmRight.addChild("FingersRight_r1", ModelPartBuilder.create().uv(33, 16).mirrored().cuboid(0.0F, -2.0F, -3.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.8205F, 11.803F, 4.8896F, 0.5672F, 0.0F, 0.0F));

		ModelPartData ArmRL_r1 = LowerArmRight.addChild("ArmRL_r1", ModelPartBuilder.create().uv(0, 31).mirrored().cuboid(-0.5F, 0.0F, -3.0F, 1.0F, 11.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0438F, 0.0093F, -0.2903F, 0.3902F, -0.0393F, 0.1249F));

		ModelPartData ArmLeft = BodyT.addChild("ArmLeft", ModelPartBuilder.create(), ModelTransform.of(-1.9226F, -6.6937F, 1.6509F, 0.1745F, 0.0F, 0.0F));

		ModelPartData ArmLT_r1 = ArmLeft.addChild("ArmLT_r1", ModelPartBuilder.create().uv(11, 33).cuboid(0.5504F, -0.9346F, -1.9564F, 1.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.5385F, 0.2774F, 0.8757F, -0.2122F, 0.0487F, 0.4754F));

		ModelPartData LowerArmLeft = ArmLeft.addChild("LowerArmLeft", ModelPartBuilder.create(), ModelTransform.of(-5.245F, 9.4479F, -2.153F, 0.1745F, 0.0F, 0.0F));

		ModelPartData PodL3_r1 = LowerArmLeft.addChild("PodL3_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.7065F, 7.0835F, -1.3367F, -1.8875F, -0.1514F, -0.0423F));

		ModelPartData PodL2_r1 = LowerArmLeft.addChild("PodL2_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.2065F, 3.0835F, -3.1367F, -1.8838F, -0.0269F, -0.083F));

		ModelPartData PodL1_r1 = LowerArmLeft.addChild("PodL1_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.2065F, 5.0835F, -2.3367F, -1.8878F, -0.1328F, 0.0434F));

		ModelPartData FingersLeft_r1 = LowerArmLeft.addChild("FingersLeft_r1", ModelPartBuilder.create().uv(33, 16).cuboid(-1.0F, -2.0F, -3.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.4065F, 11.0835F, 5.2633F, 0.5859F, 0.2103F, -0.2351F));

		ModelPartData ArmLL_r1 = LowerArmLeft.addChild("ArmLL_r1", ModelPartBuilder.create().uv(0, 31).cuboid(-0.5F, 0.0F, -3.0F, 1.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0422F, -0.1102F, -0.0166F, 0.3902F, 0.0393F, -0.1249F));

		ModelPartData BodyB = Torso.addChild("BodyB", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -9.8F, -1.8F));

		ModelPartData BodyLow_r1 = BodyB.addChild("BodyLow_r1", ModelPartBuilder.create().uv(17, 15).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.9F, 0.6F, 0.1745F, 0.0F, 0.0F));

		ModelPartData Legs = AmenBreak.addChild("Legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0414F, -8.7419F, 0.6418F));

		ModelPartData LegLeft = Legs.addChild("LegLeft", ModelPartBuilder.create(), ModelTransform.of(-1.6F, 0.0F, 0.0F, 0.2104F, -0.1838F, 0.0433F));

		ModelPartData ToesLeft_r1 = LegLeft.addChild("ToesLeft_r1", ModelPartBuilder.create().uv(24, 24).cuboid(-0.9886F, 0.8978F, -1.2238F, 2.0F, 2.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.3694F, 5.6152F, -2.1031F, -0.2626F, -0.218F, 0.0039F));

		ModelPartData LegLeft_r1 = LegLeft.addChild("LegLeft_r1", ModelPartBuilder.create().uv(26, 15).cuboid(-1.0F, 0.8611F, -0.0979F, 1.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.5694F, -1.1848F, -0.4031F, -0.3054F, -0.2182F, 0.0F));

		ModelPartData LegRight = Legs.addChild("LegRight", ModelPartBuilder.create(), ModelTransform.of(2.0483F, 0.3734F, 1.3048F, -0.3054F, 0.1745F, 0.0F));

		ModelPartData ToesRight_r1 = LegRight.addChild("ToesRight_r1", ModelPartBuilder.create().uv(24, 24).mirrored().cuboid(-1.0114F, 0.8978F, -1.2238F, 2.0F, 2.0F, 3.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.369F, 5.6824F, -2.0667F, -0.2626F, 0.218F, -0.0039F));

		ModelPartData LegLeft_r2 = LegRight.addChild("LegLeft_r2", ModelPartBuilder.create().uv(26, 15).mirrored().cuboid(0.0F, 0.8612F, -0.0979F, 1.0F, 7.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.669F, -1.2176F, -0.5667F, -0.3054F, 0.2182F, 0.0F));
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