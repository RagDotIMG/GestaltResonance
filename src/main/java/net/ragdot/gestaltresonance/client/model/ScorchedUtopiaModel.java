package net.ragdot.gestaltresonance.client.model;


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;


public class ScorchedUtopiaModel extends SinglePartEntityModel<ScorchedUtopia> {

	public static final EntityModelLayer SCORCHED_UTOPIA = new EntityModelLayer(Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia"), "main");


	private final ModelPart zombie_model;
	private final ModelPart body_top;
	private final ModelPart body_up;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart mask;
	private final ModelPart front1;
	private final ModelPart front2;
	private final ModelPart R_side;
	private final ModelPart L_side;
	private final ModelPart top1;
	private final ModelPart top2;
	private final ModelPart hair;
	private final ModelPart body_u;
	private final ModelPart wing;
	private final ModelPart gear;
	private final ModelPart arms;
	private final ModelPart R_arm;
	private final ModelPart R_u_arm;
	private final ModelPart R_l_arm;
	private final ModelPart L_arm;
	private final ModelPart L_u_arm;
	private final ModelPart L_l_arm;
	private final ModelPart body_mid;
	private final ModelPart body_bot;
	private final ModelPart body_low;
	private final ModelPart legs;
	private final ModelPart R_leg;
	private final ModelPart R_u_leg;
	private final ModelPart R_l_leg;
	private final ModelPart L_leg;
	private final ModelPart left_u_leg;
	private final ModelPart left_l_leg;
	public ScorchedUtopiaModel(ModelPart root) {
		this.zombie_model = root.getChild("zombie_model");
		this.body_top = this.zombie_model.getChild("body_top");
		this.body_up = this.body_top.getChild("body_up");
		this.head = this.body_up.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.mask = this.head.getChild("mask");
		this.front1 = this.mask.getChild("front1");
		this.front2 = this.mask.getChild("front2");
		this.R_side = this.mask.getChild("R_side");
		this.L_side = this.mask.getChild("L_side");
		this.top1 = this.mask.getChild("top1");
		this.top2 = this.mask.getChild("top2");
		this.hair = this.mask.getChild("hair");
		this.body_u = this.body_up.getChild("body_u");
		this.wing = this.body_u.getChild("wing");
		this.gear = this.body_u.getChild("gear");
		this.arms = this.body_up.getChild("arms");
		this.R_arm = this.arms.getChild("R_arm");
		this.R_u_arm = this.R_arm.getChild("R_u_arm");
		this.R_l_arm = this.R_arm.getChild("R_l_arm");
		this.L_arm = this.arms.getChild("L_arm");
		this.L_u_arm = this.L_arm.getChild("L_u_arm");
		this.L_l_arm = this.L_arm.getChild("L_l_arm");
		this.body_mid = this.body_top.getChild("body_mid");
		this.body_bot = this.zombie_model.getChild("body_bot");
		this.body_low = this.body_bot.getChild("body_low");
		this.legs = this.body_bot.getChild("legs");
		this.R_leg = this.legs.getChild("R_leg");
		this.R_u_leg = this.R_leg.getChild("R_u_leg");
		this.R_l_leg = this.R_leg.getChild("R_l_leg");
		this.L_leg = this.legs.getChild("L_leg");
		this.left_u_leg = this.L_leg.getChild("left_u_leg");
		this.left_l_leg = this.L_leg.getChild("left_l_leg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData zombie_model = modelPartData.addChild("zombie_model", ModelPartBuilder.create(), ModelTransform.of(0.0F, 20.9F, 0.0F, 0.2618F, 0.0F, 0.0F));

		ModelPartData body_top = zombie_model.addChild("body_top", ModelPartBuilder.create(), ModelTransform.of(0.6626F, -18.4923F, 0.4634F, -0.6498F, -0.1834F, 0.3203F));

		ModelPartData body_up = body_top.addChild("body_up", ModelPartBuilder.create(), ModelTransform.of(-1.4431F, -4.0729F, -2.4863F, -0.0209F, 0.3416F, -0.2647F));

		ModelPartData head = body_up.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.9796F, -4.814F, -1.148F, 8.0F, 7.0F, 8.0F, new Dilation(-0.002F))
				.uv(0, 0).cuboid(-3.4755F, -2.4002F, -1.0261F, 7.0F, 4.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 4).cuboid(-2.2091F, -0.7124F, -1.1348F, 4.0F, 3.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 15).cuboid(-3.9535F, 1.7609F, -1.0815F, 8.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.8064F, -7.4792F, -4.6801F, 0.0927F, 0.0012F, -0.2513F));

		ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create(), ModelTransform.of(-0.0254F, 1.9774F, 1.415F, 0.8726F, -0.0412F, -0.1258F));

		ModelPartData jaw_r1 = jaw.addChild("jaw_r1", ModelPartBuilder.create().uv(0, 17).cuboid(-3.9F, -1.2745F, -0.4488F, 8.0F, 3.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.0378F, 0.2698F, -0.6771F, -1.2654F, 0.0F, 0.0F));

		ModelPartData mask = head.addChild("mask", ModelPartBuilder.create().uv(31, 55).cuboid(-4.0129F, -1.5708F, 1.7311F, 8.0F, 2.0F, 7.0F, new Dilation(0.2F)), ModelTransform.pivot(0.0333F, -3.1433F, -1.879F));

		ModelPartData front1 = mask.addChild("front1", ModelPartBuilder.create(), ModelTransform.of(0.0F, 1.6921F, 0.3259F, 0.0873F, 0.0F, 0.0F));

		ModelPartData R_eye_r1 = front1.addChild("R_eye_r1", ModelPartBuilder.create().uv(48, 39).cuboid(-1.0F, -2.4641F, 0.0086F, 3.0F, 4.0958F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-2.4599F, -0.0961F, -0.3641F, 0.0873F, 0.0873F, 1.5708F));

		ModelPartData L_eye_r1 = front1.addChild("L_eye_r1", ModelPartBuilder.create().uv(48, 39).mirrored().cuboid(-2.0F, -2.5F, 0.0F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(2.4943F, -0.1019F, -0.3435F, 0.0873F, -0.0873F, -1.5708F));

		ModelPartData front2 = mask.addChild("front2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5315F, 0.8476F, -0.6109F, 0.0F, 0.0F));

		ModelPartData cube_r1 = front2.addChild("cube_r1", ModelPartBuilder.create().uv(57, 39).cuboid(-1.1352F, -0.0223F, -0.1986F, 3.0F, 4.0996F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0044F, 0.8351F, -0.134F, 0.0873F, -0.3927F, 1.5708F));

		ModelPartData cube_r2 = front2.addChild("cube_r2", ModelPartBuilder.create().uv(57, 39).mirrored().cuboid(-1.8648F, -0.011F, -0.1909F, 3.0074F, 4.0035F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0044F, 0.8351F, -0.134F, 0.0873F, 0.3927F, -1.5708F));

		ModelPartData R_side = mask.addChild("R_side", ModelPartBuilder.create(), ModelTransform.pivot(-3.9621F, -0.0927F, 0.2998F));

		ModelPartData cube_r3 = R_side.addChild("cube_r3", ModelPartBuilder.create().uv(57, 40).cuboid(-1.0F, 0.0F, -0.0483F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0761F, -0.9293F, 0.1427F, 1.553F, 0.2181F, 1.5685F));

		ModelPartData cube_r4 = R_side.addChild("cube_r4", ModelPartBuilder.create().uv(57, 40).cuboid(-2.0F, 0.0F, -0.0312F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0961F, 2.7209F, -0.1945F, 1.571F, 0.0001F, 1.5746F));

		ModelPartData L_side = mask.addChild("L_side", ModelPartBuilder.create(), ModelTransform.pivot(3.9886F, -0.0927F, 0.2998F));

		ModelPartData cube_r5 = L_side.addChild("cube_r5", ModelPartBuilder.create().uv(57, 40).mirrored().cuboid(-2.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0078F, -0.9373F, 0.1408F, 1.553F, -0.2181F, -1.5685F));

		ModelPartData cube_r6 = L_side.addChild("cube_r6", ModelPartBuilder.create().uv(57, 40).mirrored().cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0038F, 2.7132F, -0.1943F, 1.571F, -0.0001F, -1.5746F));

		ModelPartData top1 = mask.addChild("top1", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.4881F, 1.9374F, -1.5708F, 0.0F, 0.0F));

		ModelPartData cube_r7 = top1.addChild("cube_r7", ModelPartBuilder.create().uv(58, 39).cuboid(-0.8352F, -2.0224F, -0.1986F, 2.0F, 4.0996F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.988F, 0.1333F, -0.5308F, 0.0873F, -0.3491F, 1.5708F));

		ModelPartData cube_r8 = top1.addChild("cube_r8", ModelPartBuilder.create().uv(58, 39).mirrored().cuboid(-1.1648F, -2.011F, -0.1909F, 2.0074F, 4.0035F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.9967F, 0.1333F, -0.5308F, 0.0873F, 0.3491F, -1.5708F));

		ModelPartData top2 = mask.addChild("top2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5968F, 3.3712F, -1.789F, 0.0F, 0.0F));

		ModelPartData cube_r9 = top2.addChild("cube_r9", ModelPartBuilder.create().uv(58, 39).cuboid(-0.8352F, -2.0223F, -0.1986F, 2.0F, 4.0996F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.988F, 0.1333F, -0.5308F, 0.0873F, -0.3491F, 1.5708F));

		ModelPartData cube_r10 = top2.addChild("cube_r10", ModelPartBuilder.create().uv(58, 39).mirrored().cuboid(-1.1648F, -2.011F, -0.1909F, 2.0074F, 4.0035F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.9967F, 0.1333F, -0.5308F, 0.0873F, 0.3491F, -1.5708F));

		ModelPartData hair = mask.addChild("hair", ModelPartBuilder.create(), ModelTransform.pivot(-4.5174F, 2.5593F, 4.4843F));

		ModelPartData cube_r11 = hair.addChild("cube_r11", ModelPartBuilder.create().uv(52, 35).mirrored().cuboid(-0.4F, 0.0F, -1.9826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 1.2F, 1.0F, 0.5902F, 0.3448F, -1.4679F));

		ModelPartData cube_r12 = hair.addChild("cube_r12", ModelPartBuilder.create().uv(52, 35).mirrored().cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.6193F, 2.1631F, 4.2309F, -1.0948F, -0.3056F, 0.1677F));

		ModelPartData cube_r13 = hair.addChild("cube_r13", ModelPartBuilder.create().uv(52, 35).cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(7.442F, 2.1631F, 4.2309F, -1.1268F, 0.3521F, -0.2668F));

		ModelPartData cube_r14 = hair.addChild("cube_r14", ModelPartBuilder.create().uv(52, 35).mirrored().cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(7.8193F, -0.5369F, 3.5309F, -1.1024F, 0.7036F, -0.2214F));

		ModelPartData cube_r15 = hair.addChild("cube_r15", ModelPartBuilder.create().uv(52, 35).cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(1.442F, -0.9369F, 3.6309F, -0.8237F, -0.6771F, 0.0443F));

		ModelPartData cube_r16 = hair.addChild("cube_r16", ModelPartBuilder.create().uv(52, 35).cuboid(-3.6F, 0.0F, -1.9826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(9.2612F, 0.9F, 1.2F, 0.5733F, -0.5296F, 1.4989F));

		ModelPartData cube_r17 = hair.addChild("cube_r17", ModelPartBuilder.create().uv(52, 35).cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(6.3108F, -1.4196F, 5.1557F, -0.5649F, -0.0657F, 0.0551F));

		ModelPartData cube_r18 = hair.addChild("cube_r18", ModelPartBuilder.create().uv(52, 35).cuboid(-2.0F, 1.3F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(1.742F, -2.6369F, 5.5309F, -0.4186F, -0.3208F, 0.0342F));

		ModelPartData cube_r19 = hair.addChild("cube_r19", ModelPartBuilder.create().uv(52, 35).cuboid(-4.2F, -2.3F, 2.0349F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(6.7025F, 1.2719F, 0.8447F, -0.4192F, 0.0076F, -0.0869F));

		ModelPartData body_u = body_up.addChild("body_u", ModelPartBuilder.create().uv(36, 12).cuboid(-4.0053F, -5.0469F, 0.1535F, 9.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-0.2147F, 0.5F, -1.6649F, -0.0027F, -0.1023F, -0.0883F));

		ModelPartData wing = body_u.addChild("wing", ModelPartBuilder.create(), ModelTransform.of(-1.9361F, -4.1885F, 4.614F, 0.3491F, 0.0873F, 0.4363F));

		ModelPartData wing_r1 = wing.addChild("wing_r1", ModelPartBuilder.create().uv(46, 47).mirrored().cuboid(-4.5268F, -5.511F, 3.2421F, 9.0F, 15.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-3.6F, 5.8F, -0.2F, 0.0425F, 0.5493F, -0.1434F));

		ModelPartData gear = body_u.addChild("gear", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.332F, -3.9953F, 5.4945F, 1.2337F, -1.1441F, -0.6993F));

		ModelPartData cube_r20 = gear.addChild("cube_r20", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.7F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(0.005F, -0.005F, 0.0F, 3.1416F, 0.0F, -2.3562F));

		ModelPartData cube_r21 = gear.addChild("cube_r21", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.7F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(0.005F, 0.005F, 0.0F, 3.1416F, 0.0F, -0.7854F));

		ModelPartData cube_r22 = gear.addChild("cube_r22", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.7F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(-0.005F, -0.005F, 0.0F, 3.1416F, 0.0F, 2.3562F));

		ModelPartData cube_r23 = gear.addChild("cube_r23", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.7F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(-0.005F, 0.005F, 0.0F, 3.1416F, 0.0F, 0.7854F));

		ModelPartData cube_r24 = gear.addChild("cube_r24", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		ModelPartData cube_r25 = gear.addChild("cube_r25", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 1.5708F));

		ModelPartData cube_r26 = gear.addChild("cube_r26", ModelPartBuilder.create().uv(46, 44).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		ModelPartData arms = body_up.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(-0.6F, -2.7371F, 0.6F));

		ModelPartData R_arm = arms.addChild("R_arm", ModelPartBuilder.create(), ModelTransform.of(-4.2664F, 0.5703F, 0.2007F, 0.3843F, 0.2896F, 0.332F));

		ModelPartData R_u_arm = R_arm.addChild("R_u_arm", ModelPartBuilder.create().uv(0, 21).cuboid(-2.2203F, -0.675F, -0.9167F, 2.0F, 9.0F, 2.0F, new Dilation(0.3F)), ModelTransform.pivot(-0.0301F, 0.0243F, -0.0471F));

		ModelPartData R_l_arm = R_arm.addChild("R_l_arm", ModelPartBuilder.create().uv(0, 32).cuboid(-1.138F, 0.4368F, -0.8956F, 2.0F, 7.0F, 2.0F, new Dilation(0.4F)), ModelTransform.of(-0.9596F, 8.0899F, 0.0748F, 0.1439F, 0.5766F, 0.0777F));

		ModelPartData right_hand_r1 = R_l_arm.addChild("right_hand_r1", ModelPartBuilder.create().uv(18, 15).cuboid(-1.5F, -0.3F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.1361F, 7.2343F, -0.3692F, 0.7854F, 0.0F, 0.0F));

		ModelPartData L_arm = arms.addChild("L_arm", ModelPartBuilder.create(), ModelTransform.of(4.7645F, 0.4196F, 1.0147F, 0.3971F, -0.3711F, -0.2265F));

		ModelPartData L_u_arm = L_arm.addChild("L_u_arm", ModelPartBuilder.create().uv(8, 21).mirrored().cuboid(0.2348F, -2.075F, -1.0404F, 2.0F, 9.0F, 2.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.pivot(-0.475F, 0.3242F, 0.2482F));

		ModelPartData detail2_r1 = L_u_arm.addChild("detail2_r1", ModelPartBuilder.create().uv(46, 43).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.3372F, -0.8105F, -0.0404F, -0.027F, -0.2159F, 1.6684F));

		ModelPartData detail1_r1 = L_u_arm.addChild("detail1_r1", ModelPartBuilder.create().uv(46, 43).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.8372F, -1.9105F, -0.2404F, -3.1373F, -0.4801F, -3.0564F));

		ModelPartData pauldron_r1 = L_u_arm.addChild("pauldron_r1", ModelPartBuilder.create().uv(50, 39).cuboid(-3.0F, -3.0F, -1.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(3.1348F, 0.225F, -1.0404F, 0.0F, 0.0F, 0.0785F));

		ModelPartData L_l_arm = L_arm.addChild("L_l_arm", ModelPartBuilder.create().uv(8, 32).mirrored().cuboid(-0.9832F, -0.675F, -1.1289F, 2.0F, 7.0F, 2.0F, new Dilation(0.4F)).mirrored(false), ModelTransform.of(0.5917F, 7.6233F, 0.5345F, 0.1017F, -0.184F, 0.2813F));

		ModelPartData left_hand_r1 = L_l_arm.addChild("left_hand_r1", ModelPartBuilder.create().uv(26, 18).mirrored().cuboid(-1.5F, -0.5F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0269F, 6.4276F, -0.417F, 0.7854F, 0.0F, 0.0F));

		ModelPartData body_mid = body_top.addChild("body_mid", ModelPartBuilder.create(), ModelTransform.of(0.1557F, 0.2596F, -0.1254F, 0.2593F, 0.1604F, -0.2687F));

		ModelPartData body_m_r1 = body_mid.addChild("body_m_r1", ModelPartBuilder.create().uv(40, 4).cuboid(-3.3F, -2.2F, -1.9F, 7.0F, 4.0F, 4.0F, new Dilation(0.3F)), ModelTransform.of(-0.125F, -2.3163F, -0.1892F, 0.0F, 0.0873F, 0.0F));

		ModelPartData body_bot = zombie_model.addChild("body_bot", ModelPartBuilder.create(), ModelTransform.of(0.5574F, -18.0941F, 0.1303F, 0.0095F, -0.0076F, -0.0864F));

		ModelPartData body_low = body_bot.addChild("body_low", ModelPartBuilder.create().uv(24, 0).cuboid(-3.2329F, -1.725F, -1.8327F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0307F, 1.5826F, 0.0077F, 0.0F, 0.0F, 0.3491F));

		ModelPartData legs = body_bot.addChild("legs", ModelPartBuilder.create(), ModelTransform.of(-0.0961F, 3.8879F, 0.8006F, -0.5203F, 0.0677F, 0.1657F));

		ModelPartData R_leg = legs.addChild("R_leg", ModelPartBuilder.create(), ModelTransform.of(-2.287F, -1.0653F, -0.8725F, 0.0837F, -0.0081F, 0.0106F));

		ModelPartData R_u_leg = R_leg.addChild("R_u_leg", ModelPartBuilder.create().uv(0, 41).cuboid(-0.798F, 0.8478F, -4.2452F, 2.0F, 6.0F, 2.0F, new Dilation(0.5F)), ModelTransform.pivot(-0.4F, -0.9F, 3.0F));

		ModelPartData R_l_leg = R_leg.addChild("R_l_leg", ModelPartBuilder.create(), ModelTransform.of(-0.351F, 5.1686F, 0.1619F, 1.0298F, 0.2469F, -0.1335F));

		ModelPartData right_l_leg_r1 = R_l_leg.addChild("right_l_leg_r1", ModelPartBuilder.create().uv(0, 49).cuboid(-1.4F, -3.7F, -1.2F, 2.0F, 7.0F, 2.0F, new Dilation(0.3F)), ModelTransform.of(0.2374F, 3.4098F, -0.4697F, 0.0F, 0.0F, 0.1309F));

		ModelPartData L_leg = legs.addChild("L_leg", ModelPartBuilder.create(), ModelTransform.of(1.341F, 0.1658F, -1.0138F, -0.0872F, -0.1124F, -0.1234F));

		ModelPartData left_u_leg = L_leg.addChild("left_u_leg", ModelPartBuilder.create().uv(8, 41).mirrored().cuboid(-1.0F, -0.2F, -1.1873F, 2.0F, 6.0F, 2.0F, new Dilation(0.5F)).mirrored(false), ModelTransform.of(-0.1994F, -0.0956F, 0.7042F, 0.3487F, -0.0151F, 0.0859F));

		ModelPartData left_l_leg = L_leg.addChild("left_l_leg", ModelPartBuilder.create(), ModelTransform.of(-0.4399F, 6.0174F, 2.9064F, 0.7828F, -0.1116F, 0.0174F));

		ModelPartData left_l_leg_r1 = left_l_leg.addChild("left_l_leg_r1", ModelPartBuilder.create().uv(8, 49).mirrored().cuboid(-0.9596F, -0.3089F, -2.6232F, 2.0F, 7.0F, 2.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.of(-0.2654F, 0.15F, 1.5732F, -0.1745F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(ScorchedUtopia entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);

  this.updateAnimation(entity.animationHelper.idleAnimationState, ScorchedUtopianimation.Idle, ageInTicks, 1f);
  this.updateAnimation(entity.animationHelper.guardAnimationState, ScorchedUtopianimation.Guard, ageInTicks, 1f);
  this.updateAnimation(entity.animationHelper.throwAnimationState, ScorchedUtopianimation.Throw, ageInTicks, 1f);
  this.updateAnimation(entity.animationHelper.grabAnimationState, ScorchedUtopianimation.grab, ageInTicks, 1f);
  this.updateAnimation(entity.animationHelper.windUpAnimationState, ScorchedUtopianimation.WindUp, ageInTicks, 1f);
  this.updateAnimation(entity.animationHelper.punchAnimationState, ScorchedUtopianimation.Punch, ageInTicks, 2f);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		zombie_model.render(matrices, vertexConsumer, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return this.zombie_model;
	}

}