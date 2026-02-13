package net.ragdot.gestaltresonance.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.model.ModelPart;
import net.ragdot.gestaltresonance.entities.AmenBreak;

public class AmenBreakIIIModel extends SinglePartEntityModel<AmenBreak> {

	private final ModelPart root;
	private final ModelPart AmenBreak;
	private final ModelPart Head;
	private final ModelPart Hat;
	private final ModelPart Torso;
	private final ModelPart BodyT;
	private final ModelPart BodyB;
	private final ModelPart ArmRight;
	private final ModelPart LowerArmRight;
	private final ModelPart ArmLeft;
	private final ModelPart LowerArmLeft;
	private final ModelPart LegLeft;
	private final ModelPart LegRight;

	public AmenBreakIIIModel(ModelPart root) {
		this.root = root;
		this.AmenBreak = root.getChild("AmenBreak");
		this.Head = this.AmenBreak.getChild("Head");
		this.Hat = this.Head.getChild("Hat");
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
		ModelPartData AmenBreak = modelPartData.addChild("AmenBreak", ModelPartBuilder.create(), ModelTransform.of(0.4847F, 1.0812F, 0.5861F, 0.0F, 3.1416F, 0.0F));

		ModelPartData Head = AmenBreak.addChild("Head", ModelPartBuilder.create(), ModelTransform.pivot(0.4847F, -12.0812F, 0.5862F));

		ModelPartData Beak_r1 = Head.addChild("Beak_r1", ModelPartBuilder.create().uv(18, 33).cuboid(-1.0F, -4.0F, -2.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.6F, 5.6F, 7.3F, -0.5109F, 0.6051F, -0.3624F));

		ModelPartData EYEM_r1 = Head.addChild("EYEM_r1", ModelPartBuilder.create().uv(27, 3).cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 3.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.of(-1.0F, -1.463F, 7.9395F, 0.6796F, 0.4626F, 1.5251F));

		ModelPartData EYEL_r1 = Head.addChild("EYEL_r1", ModelPartBuilder.create().uv(27, 3).cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 3.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.of(-4.1F, 1.337F, 5.4395F, -0.4473F, 0.6438F, -0.2955F));

		ModelPartData EYER_r1 = Head.addChild("EYER_r1", ModelPartBuilder.create().uv(27, 3).mirrored().cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 3.0F, 3.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(3.1F, 1.337F, 6.0395F, -0.5724F, -0.7978F, 0.4053F));

		ModelPartData HeadInside_r1 = Head.addChild("HeadInside_r1", ModelPartBuilder.create().uv(0, 16).cuboid(-4.5F, -3.0F, -4.5F, 9.0F, 7.0F, 9.0F, new Dilation(-1.1F))
		.uv(0, 0).cuboid(-4.5F, -3.0F, -4.5F, 9.0F, 7.0F, 9.0F, new Dilation(-0.2F)), ModelTransform.of(-0.2902F, -0.4F, 4.0856F, -0.4578F, 0.6286F, -0.3257F));

		ModelPartData Hat = Head.addChild("Hat", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.2F, 0.0F, -0.3503F, -0.082F, 0.0299F));

		ModelPartData HatTop_r1 = Hat.addChild("HatTop_r1", ModelPartBuilder.create().uv(56, 58).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.3F, -4.0F, 1.3963F, 0.0F, 0.0F));

		ModelPartData HatBottom_r1 = Hat.addChild("HatBottom_r1", ModelPartBuilder.create().uv(24, 56).cuboid(-3.0F, -4.0F, -2.0F, 6.0F, 6.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, -3.3F, -1.0F, -0.5498F, 0.0F, 0.0F));

		ModelPartData HatMid_r1 = Hat.addChild("HatMid_r1", ModelPartBuilder.create().uv(41, 57).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.2F, -1.0F, 1.0908F, 0.0F, 0.0F));

		ModelPartData PodH3_r1 = Hat.addChild("PodH3_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0599F, -4.3473F, -5.1267F, 0.269F, -0.8771F, -1.3756F));

		ModelPartData PodH2_r1 = Hat.addChild("PodH2_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0599F, -5.0973F, -6.5267F, -0.1649F, -0.7655F, -1.0551F));

		ModelPartData PodH1_r1 = Hat.addChild("PodH1_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0599F, -5.3973F, -7.8267F, -0.6654F, -0.436F, -0.7643F));

		ModelPartData PodHR3_r1 = Hat.addChild("PodHR3_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(6.6599F, 2.9527F, -2.9267F, 2.3167F, 0.0024F, -2.4384F));

		ModelPartData FeatherPetal3R_r1 = Hat.addChild("FeatherPetal3R_r1", ModelPartBuilder.create().uv(52, 52).mirrored().cuboid(-2.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.0396F, -1.6583F, -1.5596F, -0.384F, 0.7573F, 1.7131F));

		ModelPartData PodHL3_r1 = Hat.addChild("PodHL3_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-6.7401F, 2.9527F, -2.9267F, 2.0613F, -0.2875F, -2.4016F));

		ModelPartData FeatherPetal3L_r1 = Hat.addChild("FeatherPetal3L_r1", ModelPartBuilder.create().uv(52, 52).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-4.0396F, -1.6583F, -1.5596F, -0.384F, -0.7573F, -1.7131F));

		ModelPartData PodHR2_r1 = Hat.addChild("PodHR2_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(9.6599F, -1.6473F, 2.5733F, -0.7272F, 0.4248F, -0.4645F));

		ModelPartData FeatherPetal2R_r1 = Hat.addChild("FeatherPetal2R_r1", ModelPartBuilder.create().uv(52, 52).mirrored().cuboid(-2.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(5.4397F, -3.5583F, 0.3404F, -0.9871F, 0.2271F, 0.7109F));

		ModelPartData PodHL2_r1 = Hat.addChild("PodHL2_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-9.7401F, -1.6473F, 2.3733F, 1.7275F, -0.5789F, -2.1367F));

		ModelPartData FeatherPetal2L_r1 = Hat.addChild("FeatherPetal2L_r1", ModelPartBuilder.create().uv(52, 52).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.4397F, -3.5583F, 0.3404F, -0.9871F, -0.2271F, -0.7109F));

		ModelPartData PodHR1_r1 = Hat.addChild("PodHR1_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(6.6599F, -6.6473F, 5.1733F, 1.4377F, -0.7005F, -1.9626F));

		ModelPartData FeatherPetal1R_r1 = Hat.addChild("FeatherPetal1R_r1", ModelPartBuilder.create().uv(52, 52).mirrored().cuboid(-2.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.3396F, -6.0583F, 1.1404F, -0.9519F, -0.4364F, 0.2882F));

		ModelPartData PodHL1_r1 = Hat.addChild("PodHL1_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-6.7401F, -6.6473F, 5.1733F, 1.121F, -0.7699F, -1.7491F));

		ModelPartData FeatherPetal1L_r1 = Hat.addChild("FeatherPetal1L_r1", ModelPartBuilder.create().uv(52, 52).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-4.3396F, -6.0583F, 1.1404F, -0.9519F, 0.4364F, -0.2882F));

		ModelPartData PodHB_r1 = Hat.addChild("PodHB_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.2599F, 5.6527F, -5.1267F, 0.3132F, -0.7053F, -1.189F));

		ModelPartData FeatherPetalB_r1 = Hat.addChild("FeatherPetalB_r1", ModelPartBuilder.create().uv(52, 52).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.1604F, 0.2417F, -3.5596F, 0.3017F, -0.2048F, -2.3882F));

		ModelPartData PodHT_r1 = Hat.addChild("PodHT_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0599F, -10.4473F, 4.7733F, 0.3132F, -0.7053F, -1.189F));

		ModelPartData FeatherPetalT_r1 = Hat.addChild("FeatherPetalT_r1", ModelPartBuilder.create().uv(52, 52).cuboid(-3.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.1604F, -7.9583F, 1.0404F, -0.7065F, 0.5167F, 0.607F));

		ModelPartData Torso = AmenBreak.addChild("Torso", ModelPartBuilder.create(), ModelTransform.pivot(0.4847F, -1.0812F, 0.5862F));

		ModelPartData BodyT = Torso.addChild("BodyT", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData BodyTop_r1 = BodyT.addChild("BodyTop_r1", ModelPartBuilder.create().uv(31, 32).cuboid(-3.0F, -8.0F, -1.0F, 6.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -2.0F, -0.2618F, 0.0F, 0.0F));

		ModelPartData BodyB = Torso.addChild("BodyB", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData BodyMid_r1 = BodyB.addChild("BodyMid_r1", ModelPartBuilder.create().uv(50, 8).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 6.0F, 3.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, 0.7F, -0.7F, -0.0262F, 0.0F, 0.0F));

		ModelPartData BodyLow_r1 = BodyB.addChild("BodyLow_r1", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, 4.0695F, -0.6061F, 2.0F, 4.0F, 2.0F, new Dilation(0.5F)), ModelTransform.of(0.0F, 0.3F, -2.4F, 0.1745F, 0.0F, 0.0F));

		ModelPartData ArmRight = AmenBreak.addChild("ArmRight", ModelPartBuilder.create(), ModelTransform.of(5.4847F, -7.8812F, 1.3862F, 0.0F, 0.0F, -0.0175F));

		ModelPartData cube_r1 = ArmRight.addChild("cube_r1", ModelPartBuilder.create().uv(0, 55).cuboid(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.1444F, 0.4292F, -0.8313F, -0.2746F, -0.0468F, -0.3266F));

		ModelPartData ArmRT_r1 = ArmRight.addChild("ArmRT_r1", ModelPartBuilder.create().uv(11, 33).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 1.0F, 11.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.1F, 0.0F, -0.1719F, -0.0302F, -0.3901F));

		ModelPartData LowerArmRight = ArmRight.addChild("LowerArmRight", ModelPartBuilder.create(), ModelTransform.of(3.5205F, 13.2531F, -4.9033F, -0.3911F, -0.0166F, -0.2175F));

		ModelPartData PodR3_r1 = LowerArmRight.addChild("PodR3_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.7795F, -2.1531F, -2.1967F, -0.4028F, -0.0803F, 0.0342F));

		ModelPartData PodR2_r1 = LowerArmRight.addChild("PodR2_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.5795F, -0.1531F, -1.3967F, -0.3979F, 0.0544F, 0.1287F));

		ModelPartData PodR1_r1 = LowerArmRight.addChild("PodR1_r1", ModelPartBuilder.create().uv(40, 7).mirrored().cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.2795F, 1.8469F, -0.3967F, -0.4094F, -0.0285F, 0.1546F));

		ModelPartData FingersRight_r1 = LowerArmRight.addChild("FingersRight_r1", ModelPartBuilder.create().uv(32, 16).mirrored().cuboid(0.0F, -2.0F, -3.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.2205F, 6.1469F, 5.2033F, 0.5672F, 0.0F, 0.0F));

		ModelPartData ArmRL_r1 = LowerArmRight.addChild("ArmRL_r1", ModelPartBuilder.create().uv(0, 31).mirrored().cuboid(-1.0F, -2.0F, -3.0F, 1.0F, 11.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.8795F, -3.7531F, 0.8033F, 0.3902F, -0.0393F, 0.1249F));

		ModelPartData ArmLeft = AmenBreak.addChild("ArmLeft", ModelPartBuilder.create(), ModelTransform.of(-8.0685F, -0.2541F, -0.2893F, 0.1306F, -0.0113F, 0.182F));

		ModelPartData cube_r2 = ArmLeft.addChild("cube_r2", ModelPartBuilder.create().uv(0, 55).mirrored().cuboid(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.8976F, -7.9978F, 0.9441F, -0.2423F, 0.0311F, 0.4231F));

		ModelPartData ArmLT_r1 = ArmLeft.addChild("ArmLT_r1", ModelPartBuilder.create().uv(11, 33).cuboid(0.0F, -2.0F, -2.0F, 1.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.8532F, -8.2338F, 1.833F, -0.2122F, 0.0487F, 0.4754F));

		ModelPartData LowerArmLeft = ArmLeft.addChild("LowerArmLeft", ModelPartBuilder.create(), ModelTransform.of(-1.7533F, 5.1887F, 0.104F, 0.3917F, -0.0226F, 0.1282F));

		ModelPartData PodL3_r1 = LowerArmLeft.addChild("PodL3_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.3935F, 1.5775F, -1.2709F, -1.8875F, -0.1514F, -0.0423F));

		ModelPartData PodL2_r1 = LowerArmLeft.addChild("PodL2_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.8935F, -2.4225F, -3.0709F, -1.8838F, -0.0269F, -0.083F));

		ModelPartData PodL1_r1 = LowerArmLeft.addChild("PodL1_r1", ModelPartBuilder.create().uv(40, 7).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.8935F, -0.4225F, -2.2709F, -1.8878F, -0.1328F, 0.0434F));

		ModelPartData FingersLeft_r1 = LowerArmLeft.addChild("FingersLeft_r1", ModelPartBuilder.create().uv(32, 16).cuboid(-1.0F, -2.0F, -3.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.5065F, 5.3775F, 5.3291F, 0.5859F, 0.2103F, -0.2351F));

		ModelPartData ArmLL_r1 = LowerArmLeft.addChild("ArmLL_r1", ModelPartBuilder.create().uv(0, 31).cuboid(0.0F, -2.0F, -3.0F, 1.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.0935F, -3.9225F, 0.8291F, 0.3902F, 0.0393F, -0.1249F));

		ModelPartData LegLeft = AmenBreak.addChild("LegLeft", ModelPartBuilder.create(), ModelTransform.of(-0.7535F, 10.4582F, -1.022F, 0.2104F, -0.1838F, 0.0433F));

		ModelPartData ToesLeft_r1 = LegLeft.addChild("ToesLeft_r1", ModelPartBuilder.create().uv(38, 20).cuboid(-1.9179F, -0.3455F, -0.5537F, 3.0F, 2.0F, 4.0F, new Dilation(-0.1F)), ModelTransform.of(-2.1785F, 7.7871F, 2.2153F, -0.8841F, -0.2607F, 0.0116F));

		ModelPartData LegLeftLow_r1 = LegLeft.addChild("LegLeftLow_r1", ModelPartBuilder.create().uv(51, 32).mirrored().cuboid(-1.9285F, -2.1504F, -1.6363F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-2.1F, 4.3F, 4.3F, -0.588F, 0.0191F, 0.0726F));

		ModelPartData LegLeft_r1 = LegLeft.addChild("LegLeft_r1", ModelPartBuilder.create().uv(52, 21).mirrored().cuboid(-1.8447F, -4.2088F, -1.274F, 3.0F, 8.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.8168F, -0.7463F, 3.2618F, 0.678F, -0.2293F, -0.0407F));

		ModelPartData LegRight = AmenBreak.addChild("LegRight", ModelPartBuilder.create(), ModelTransform.of(2.3679F, 10.8395F, -1.2472F, -0.3054F, 0.1745F, 0.0F));

		ModelPartData ToesRight_r1 = LegRight.addChild("ToesRight_r1", ModelPartBuilder.create().uv(38, 20).mirrored().cuboid(-1.1916F, 1.5684F, -0.362F, 3.0F, 2.0F, 4.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(1.431F, 5.5824F, 3.9333F, -0.4264F, 0.1811F, -0.0495F));

		ModelPartData LegRightLow_r1 = LegRight.addChild("LegRightLow_r1", ModelPartBuilder.create().uv(51, 32).mirrored().cuboid(-1.0685F, -2.1162F, -1.6234F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.2787F, 3.4187F, 3.1252F, 0.0111F, 0.0064F, -0.028F));

		ModelPartData LegLeft_r2 = LegRight.addChild("LegLeft_r2", ModelPartBuilder.create().uv(52, 21).mirrored().cuboid(-1.1481F, -4.4725F, -1.1295F, 3.0F, 8.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.0851F, -1.735F, 1.3721F, 0.4011F, 0.2198F, -0.0134F));
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