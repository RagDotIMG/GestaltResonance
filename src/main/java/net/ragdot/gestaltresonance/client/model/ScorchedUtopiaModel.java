package net.ragdot.gestaltresonance.client.model;


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class ScorchedUtopiaModel extends SinglePartEntityModel<ScorchedUtopia> {

	private final ModelPart zombie_model;
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
	private final ModelPart body;
	private final ModelPart body_u;
	private final ModelPart gear;
	private final ModelPart arms;
	private final ModelPart R_arm;
	private final ModelPart R_u_arm;
	private final ModelPart R_l_arm;
	private final ModelPart R_hand;
	private final ModelPart L_arm;
	private final ModelPart L_u_arm;
	private final ModelPart L_l_arm;
	private final ModelPart L_hand;
	private final ModelPart body_mid;
	private final ModelPart body_low;
	private final ModelPart body_l;
	private final ModelPart legs;
	private final ModelPart R_leg;
	private final ModelPart R_u_leg;
	private final ModelPart R_l_leg;
	private final ModelPart L_leg;
	private final ModelPart left_u_leg;
	private final ModelPart left_l_leg;
	public ScorchedUtopiaModel(ModelPart root) {
		this.zombie_model = root.getChild("zombie_model");
		this.body_up = this.zombie_model.getChild("body_up");
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
		this.body = this.body_up.getChild("body");
		this.body_u = this.body.getChild("body_u");
		this.gear = this.body_u.getChild("gear");
		this.arms = this.body_up.getChild("arms");
		this.R_arm = this.arms.getChild("R_arm");
		this.R_u_arm = this.R_arm.getChild("R_u_arm");
		this.R_l_arm = this.R_arm.getChild("R_l_arm");
		this.R_hand = this.R_l_arm.getChild("R_hand");
		this.L_arm = this.arms.getChild("L_arm");
		this.L_u_arm = this.L_arm.getChild("L_u_arm");
		this.L_l_arm = this.L_arm.getChild("L_l_arm");
		this.L_hand = this.L_l_arm.getChild("L_hand");
		this.body_mid = this.zombie_model.getChild("body_mid");
		this.body_low = this.zombie_model.getChild("body_low");
		this.body_l = this.body_low.getChild("body_l");
		this.legs = this.body_low.getChild("legs");
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
		ModelPartData zombie_model = modelPartData.addChild("zombie_model", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, -1.0F));

		ModelPartData body_up = zombie_model.addChild("body_up", ModelPartBuilder.create(), ModelTransform.pivot(-0.0476F, -16.9293F, 2.4703F));

		ModelPartData head = body_up.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0261F, -6.9635F, -4.7631F, 8.0F, 7.0F, 8.0F, new Dilation(-0.002F))
		.uv(0, 0).cuboid(-3.5221F, -4.5497F, -4.6412F, 7.0F, 4.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 4).cuboid(-2.2556F, -2.8618F, -4.761F, 4.0F, 3.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 15).cuboid(-4.0F, -0.3885F, -4.6967F, 8.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0476F, -9.076F, -2.5703F));

		ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create(), ModelTransform.pivot(0.0378F, -0.0405F, -2.4458F));

		ModelPartData jaw_r1 = jaw.addChild("jaw_r1", ModelPartBuilder.create().uv(0, 17).cuboid(-4.0F, -0.7745F, -0.4488F, 8.0F, 3.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.0378F, -0.0302F, -0.0771F, -1.1345F, 0.0F, 0.0F));

		ModelPartData mask = head.addChild("mask", ModelPartBuilder.create().uv(32, 52).cuboid(-4.0129F, -1.6708F, 0.7311F, 8.0F, 2.0F, 8.0F, new Dilation(0.002F)), ModelTransform.pivot(-0.0132F, -5.2927F, -5.4942F));

		ModelPartData front1 = mask.addChild("front1", ModelPartBuilder.create(), ModelTransform.of(0.0F, 1.692F, 0.3259F, 0.0873F, 0.0F, 0.0F));

		ModelPartData R_eye_r1 = front1.addChild("R_eye_r1", ModelPartBuilder.create().uv(48, 44).cuboid(-1.0F, -2.4641F, 0.0086F, 3.0F, 4.0958F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-2.46F, -0.0961F, -0.3641F, 0.0873F, 0.0873F, 1.5708F));

		ModelPartData L_eye_r1 = front1.addChild("L_eye_r1", ModelPartBuilder.create().uv(48, 44).mirrored().cuboid(-2.0F, -2.5F, 0.0F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(2.4943F, -0.1019F, -0.3434F, 0.0873F, -0.0873F, -1.5708F));

		ModelPartData front2 = mask.addChild("front2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5315F, 0.8476F, -0.6109F, 0.0F, 0.0F));

		ModelPartData cube_r1 = front2.addChild("cube_r1", ModelPartBuilder.create().uv(57, 44).cuboid(-1.1352F, -0.0223F, -0.1986F, 3.0F, 4.0996F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0043F, 0.8351F, -0.134F, 0.0873F, -0.3927F, 1.5708F));

		ModelPartData cube_r2 = front2.addChild("cube_r2", ModelPartBuilder.create().uv(57, 44).mirrored().cuboid(-1.8648F, -0.011F, -0.1909F, 3.0074F, 4.0035F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0043F, 0.8351F, -0.134F, 0.0873F, 0.3927F, -1.5708F));

		ModelPartData R_side = mask.addChild("R_side", ModelPartBuilder.create(), ModelTransform.pivot(-3.9621F, -0.0927F, 0.2998F));

		ModelPartData cube_r3 = R_side.addChild("cube_r3", ModelPartBuilder.create().uv(57, 45).cuboid(-1.0F, 0.0F, -0.0483F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0761F, -0.9293F, 0.1427F, 1.553F, 0.2181F, 1.5685F));

		ModelPartData cube_r4 = R_side.addChild("cube_r4", ModelPartBuilder.create().uv(57, 45).cuboid(-2.0F, 0.0F, -0.0311F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0961F, 2.7209F, -0.1945F, 1.571F, 0.0001F, 1.5746F));

		ModelPartData L_side = mask.addChild("L_side", ModelPartBuilder.create(), ModelTransform.pivot(3.9886F, -0.0927F, 0.2998F));

		ModelPartData cube_r5 = L_side.addChild("cube_r5", ModelPartBuilder.create().uv(57, 45).mirrored().cuboid(-2.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0078F, -0.9373F, 0.1408F, 1.553F, -0.2181F, -1.5685F));

		ModelPartData cube_r6 = L_side.addChild("cube_r6", ModelPartBuilder.create().uv(57, 45).mirrored().cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0038F, 2.7132F, -0.1943F, 1.571F, -0.0001F, -1.5746F));

		ModelPartData top1 = mask.addChild("top1", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.4881F, 1.9374F, -1.5708F, 0.0F, 0.0F));

		ModelPartData cube_r7 = top1.addChild("cube_r7", ModelPartBuilder.create().uv(58, 44).cuboid(-0.8352F, -2.0224F, -0.1986F, 2.0F, 4.0996F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.988F, 0.1333F, -0.5308F, 0.0873F, -0.3491F, 1.5708F));

		ModelPartData cube_r8 = top1.addChild("cube_r8", ModelPartBuilder.create().uv(58, 44).mirrored().cuboid(-1.1648F, -2.011F, -0.1909F, 2.0074F, 4.0035F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.9967F, 0.1333F, -0.5308F, 0.0873F, 0.3491F, -1.5708F));

		ModelPartData top2 = mask.addChild("top2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5968F, 3.3712F, -1.789F, 0.0F, 0.0F));

		ModelPartData cube_r9 = top2.addChild("cube_r9", ModelPartBuilder.create().uv(58, 44).cuboid(-0.8352F, -2.0223F, -0.1986F, 2.0F, 4.0996F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.988F, 0.1333F, -0.5308F, 0.0873F, -0.3491F, 1.5708F));

		ModelPartData cube_r10 = top2.addChild("cube_r10", ModelPartBuilder.create().uv(58, 44).mirrored().cuboid(-1.1648F, -2.011F, -0.1909F, 2.0074F, 4.0035F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.9967F, 0.1333F, -0.5308F, 0.0873F, 0.3491F, -1.5708F));

		ModelPartData hair = mask.addChild("hair", ModelPartBuilder.create(), ModelTransform.pivot(-4.5174F, 2.5593F, 4.4843F));

		ModelPartData cube_r11 = hair.addChild("cube_r11", ModelPartBuilder.create().uv(21, 0).mirrored().cuboid(-0.4F, 0.0F, -1.9826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 1.2F, 1.0F, 0.5902F, 0.3448F, -1.4679F));

		ModelPartData cube_r12 = hair.addChild("cube_r12", ModelPartBuilder.create().uv(21, 0).mirrored().cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.6193F, 2.1631F, 4.2309F, -1.0948F, -0.3056F, 0.1677F));

		ModelPartData cube_r13 = hair.addChild("cube_r13", ModelPartBuilder.create().uv(21, 0).cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(7.442F, 2.1631F, 4.2309F, -1.1268F, 0.3521F, -0.2668F));

		ModelPartData cube_r14 = hair.addChild("cube_r14", ModelPartBuilder.create().uv(21, 0).mirrored().cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(7.8193F, -0.8369F, 3.5309F, -1.1024F, 0.7036F, -0.2214F));

		ModelPartData cube_r15 = hair.addChild("cube_r15", ModelPartBuilder.create().uv(21, 0).cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(1.442F, -0.9369F, 3.6309F, -0.8237F, -0.6771F, 0.0443F));

		ModelPartData cube_r16 = hair.addChild("cube_r16", ModelPartBuilder.create().uv(21, 0).cuboid(-3.6F, 0.0F, -1.9826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(9.2612F, 0.8F, 1.2F, 0.5733F, -0.5296F, 1.4989F));

		ModelPartData cube_r17 = hair.addChild("cube_r17", ModelPartBuilder.create().uv(21, 0).cuboid(-2.0F, 0.0F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(6.3108F, -1.3196F, 5.1557F, -0.568F, -0.0189F, 0.1288F));

		ModelPartData cube_r18 = hair.addChild("cube_r18", ModelPartBuilder.create().uv(21, 0).cuboid(-2.0F, 1.3F, -1.7826F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(2.242F, -2.9369F, 5.5309F, -0.2884F, -0.3192F, -0.1111F));

		ModelPartData cube_r19 = hair.addChild("cube_r19", ModelPartBuilder.create().uv(21, 0).cuboid(-4.2F, -2.3F, 2.0349F, 4.0F, 0.0F, 3.5651F, new Dilation(0.0F)), ModelTransform.of(6.7025F, 1.272F, 0.8447F, -0.4192F, 0.0076F, -0.0869F));

		ModelPartData body = body_up.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0476F, -3.5829F, -2.5703F));

		ModelPartData body_u = body.addChild("body_u", ModelPartBuilder.create().uv(36, 0).cuboid(-4.5053F, -5.0469F, -2.4465F, 9.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.0146F, 0.0F, 0.0351F));

		ModelPartData wing_r1 = body_u.addChild("wing_r1", ModelPartBuilder.create().uv(45, 27).mirrored().cuboid(-4.5268F, -5.511F, 3.2421F, 9.0F, 15.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-5.5361F, 2.0115F, 1.314F, 0.0425F, 0.5493F, -0.1434F));

		ModelPartData gear = body_u.addChild("gear", ModelPartBuilder.create().uv(46, 49).cuboid(-0.52F, -2.4591F, -0.9863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.3907F, -3.8635F, 3.7378F, 1.3237F, -0.9768F, -0.8021F));

		ModelPartData g8_r1 = gear.addChild("8_r1", ModelPartBuilder.create().uv(46, 49).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(1.1872F, -1.1662F, -0.4863F, 3.1416F, 0.0F, -2.3562F));

		ModelPartData g7_r1 = gear.addChild("7_r1", ModelPartBuilder.create().uv(46, 49).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(1.1872F, 1.248F, -0.4863F, 3.1416F, 0.0F, -0.7854F));

		ModelPartData g6_r1 = gear.addChild("6_r1", ModelPartBuilder.create().uv(46, 49).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(-1.2271F, -1.1662F, -0.4863F, 3.1416F, 0.0F, 2.3562F));

		ModelPartData g5_r1 = gear.addChild("5_r1", ModelPartBuilder.create().uv(46, 49).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(-1.2271F, 1.248F, -0.4863F, 3.1416F, 0.0F, 0.7854F));

		ModelPartData g4_r1 = gear.addChild("4_r1", ModelPartBuilder.create().uv(46, 49).cuboid(-0.375F, -2.625F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.145F, -0.0841F, 0.0137F, 0.0F, 0.0F, 1.5708F));

		ModelPartData g3_r1 = gear.addChild("3_r1", ModelPartBuilder.create().uv(46, 49).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.52F, 0.0409F, -0.4863F, 3.1416F, 0.0F, 1.5708F));

		ModelPartData g2_r1 = gear.addChild("2_r1", ModelPartBuilder.create().uv(46, 49).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.02F, 1.5409F, -0.4863F, 3.1416F, 0.0F, 0.0F));

		ModelPartData arms = body_up.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0476F, -6.82F, -2.5703F));

		ModelPartData R_arm = arms.addChild("R_arm", ModelPartBuilder.create(), ModelTransform.pivot(-4.4949F, -0.3F, -0.0029F));

		ModelPartData R_u_arm = R_arm.addChild("R_u_arm", ModelPartBuilder.create().uv(0, 21).cuboid(-2.2203F, -0.675F, -0.9167F, 2.0F, 9.0F, 2.0F, new Dilation(0.3F)), ModelTransform.pivot(-0.0301F, 0.0243F, -0.0471F));

		ModelPartData R_l_arm = R_arm.addChild("R_l_arm", ModelPartBuilder.create().uv(8, 21).cuboid(-1.138F, 0.3368F, -0.8956F, 2.0F, 7.0F, 2.0F, new Dilation(0.4F)), ModelTransform.pivot(-1.25F, 8.7125F, -0.125F));

		ModelPartData R_hand = R_l_arm.addChild("R_hand", ModelPartBuilder.create(), ModelTransform.pivot(-0.0301F, 7.7418F, 4.405F));

		ModelPartData right_hand_r1 = R_hand.addChild("right_hand_r1", ModelPartBuilder.create().uv(18, 15).cuboid(-1.5F, -0.1F, 0.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.1059F, -0.0418F, -5.6227F, 0.7854F, 0.0F, 0.0F));

		ModelPartData L_arm = arms.addChild("L_arm", ModelPartBuilder.create(), ModelTransform.pivot(4.475F, -0.2F, 3.2518F));

		ModelPartData L_u_arm = L_arm.addChild("L_u_arm", ModelPartBuilder.create().uv(0, 21).mirrored().cuboid(0.2348F, -1.075F, -1.0404F, 2.0F, 9.0F, 2.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.pivot(0.025F, 0.3242F, -3.2518F));

		ModelPartData detail2_r1 = L_u_arm.addChild("detail2_r1", ModelPartBuilder.create().uv(46, 48).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.1372F, 0.6895F, -0.0404F, -0.0319F, -0.303F, 1.626F));

		ModelPartData detail1_r1 = L_u_arm.addChild("detail1_r1", ModelPartBuilder.create().uv(46, 48).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.8372F, -1.9105F, -0.2404F, -3.1373F, -0.4801F, -3.0564F));

		ModelPartData pauldron_r1 = L_u_arm.addChild("pauldron_r1", ModelPartBuilder.create().uv(50, 44).cuboid(-3.0F, -2.0F, -1.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(3.1348F, 0.225F, -1.0404F, 0.0F, 0.0F, 0.0785F));

		ModelPartData L_l_arm = L_arm.addChild("L_l_arm", ModelPartBuilder.create().uv(8, 21).mirrored().cuboid(-0.9832F, 0.425F, -3.8289F, 2.0F, 7.0F, 2.0F, new Dilation(0.4F)).mirrored(false), ModelTransform.pivot(1.3F, 8.5242F, -0.4268F));

		ModelPartData L_hand = L_l_arm.addChild("L_hand", ModelPartBuilder.create(), ModelTransform.pivot(0.1F, 7.8508F, 0.9268F));

		ModelPartData left_hand_r1 = L_hand.addChild("left_hand_r1", ModelPartBuilder.create().uv(18, 15).mirrored().cuboid(-1.6F, -0.1F, -0.1F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0269F, -0.0625F, -4.956F, 0.7854F, 0.0F, 0.0F));

		ModelPartData body_mid = zombie_model.addChild("body_mid", ModelPartBuilder.create().uv(42, 10).cuboid(-3.525F, -4.3163F, -4.2892F, 7.0F, 4.0F, 4.0F, new Dilation(0.3F)), ModelTransform.pivot(0.025F, -16.294F, 2.2892F));

		ModelPartData body_low = zombie_model.addChild("body_low", ModelPartBuilder.create(), ModelTransform.pivot(-0.2978F, -15.0F, 2.225F));

		ModelPartData body_l = body_low.addChild("body_l", ModelPartBuilder.create().uv(44, 18).cuboid(-2.2329F, -1.725F, -1.3327F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.3693F, 0.3826F, -2.8923F));

		ModelPartData legs = body_low.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.125F, 2.1F, -2.125F));

		ModelPartData R_leg = legs.addChild("R_leg", ModelPartBuilder.create(), ModelTransform.pivot(-2.25F, -0.725F, 3.275F));

		ModelPartData R_u_leg = R_leg.addChild("R_u_leg", ModelPartBuilder.create().uv(16, 20).cuboid(-0.5772F, 0.125F, -4.366F, 2.0F, 6.0F, 2.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData R_l_leg = R_leg.addChild("R_l_leg", ModelPartBuilder.create().uv(24, 20).cuboid(-0.2653F, 0.6662F, -4.6078F, 2.0F, 7.0F, 2.0F, new Dilation(0.3F)), ModelTransform.pivot(-0.3119F, 5.6588F, 0.2328F));

		ModelPartData L_leg = legs.addChild("L_leg", ModelPartBuilder.create(), ModelTransform.pivot(1.8728F, -0.975F, 3.525F));

		ModelPartData left_u_leg = L_leg.addChild("left_u_leg", ModelPartBuilder.create().uv(16, 20).mirrored().cuboid(-0.7F, 0.0F, -4.6873F, 2.0F, 6.0F, 2.0F, new Dilation(0.5F)).mirrored(false), ModelTransform.pivot(0.0F, 0.375F, 0.0F));

		ModelPartData left_l_leg = L_leg.addChild("left_l_leg", ModelPartBuilder.create().uv(24, 20).mirrored().cuboid(-0.825F, 0.95F, -3.75F, 2.0F, 7.0F, 2.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.pivot(0.125F, 5.625F, -0.875F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(ScorchedUtopia entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		zombie_model.render(matrices, vertexConsumer, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return null;
	}
}