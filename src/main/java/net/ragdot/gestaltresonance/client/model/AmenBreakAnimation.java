package net.ragdot.gestaltresonance.client.model;


import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;


public class AmenBreakAnimation {

	public static final Animation Intro = Animation.Builder.create(0.0F)
			.addBoneAnimation("Head", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-20.289F, -68.3328F, 25.7546F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LowerArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-12.5F, 27.5F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-25.3472F, -12.0675F, 3.284F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("BodyT", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-5.0F, -10.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Torso", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 102.5F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Legs", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 120.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.build();

	public static final Animation Idle = Animation.Builder.create(1.5F).looping()
			.addBoneAnimation("Head", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -35.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(-7.4929F, 0.3262F, -35.0214F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -35.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LowerArmLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-12.6919F, 14.0898F, 33.7658F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(-19.3571F, 4.6286F, 16.9119F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(-12.6919F, 14.0898F, 33.7658F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LowerArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-5.8117F, -42.5915F, 26.3295F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(-21.6563F, -39.2681F, 35.2366F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(-5.8117F, -42.5915F, 26.3295F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(71.5779F, 21.3613F, -56.601F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(17.0266F, -47.9805F, -11.9133F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(11.774F, -54.7236F, -12.8123F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(17.0266F, -47.9805F, -11.9133F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LegLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(51.3616F, 15.8134F, 4.3277F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(50.5811F, 24.7307F, -0.2694F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(51.3616F, 15.8134F, 4.3277F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LegRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(45.2395F, 0.7078F, -53.2278F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(43.1783F, 5.2123F, -62.3924F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(45.2395F, 0.7078F, -53.2278F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LegRight", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.6062F, 0.35F, -0.8F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-0.6062F, 0.35F, -0.8F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("AmenBreak", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 2.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("BodyT", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Torso", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 67.5F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Torso", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-12.3F, 3.8F, -3.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Legs", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 60.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Legs", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-11.7F, 4.0F, -3.5F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Ribbon", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 20.0F), Transformation.Interpolations.CUBIC)
			))
			.build();

	public static final Animation grab = Animation.Builder.create(0.0F)
			.addBoneAnimation("Head", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(21.1828F, -42.8466F, 4.7652F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Head", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.3628F, 1.1F, -0.1337F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LowerArmLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(21.8657F, 35.4718F, 65.1324F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LowerArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(26.7736F, -14.3959F, 30.2597F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(56.1132F, 41.2306F, -1.0368F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmRight", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.1F, -0.1F, -0.6F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(64.7765F, -37.5225F, 25.1481F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("ArmLeft", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.5576F, 0.1F, -0.1805F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LegLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(2.43F, 14.5048F, 1.1148F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LegLeft", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.1F, 0.6F, 0.6F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.2F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LegRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(29.4658F, 3.1649F, -3.369F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LegRight", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.3F, 1.0F, -1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Torso", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 52.5F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Torso", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-4.1F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Legs", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 47.5F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Legs", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-4.2F, 4.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.build();

	public static final Animation Throw = Animation.Builder.create(0.25F)
			.addBoneAnimation("Head", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-15.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("Head", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.2427F, -0.1745F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.872F, -0.4169F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("LowerArmLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("LowerArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(36.048F, -10.553F, 19.9379F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("ArmRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(32.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(114.0656F, 16.2328F, 11.7439F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("ArmRight", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.4F, 0.4585F, 0.0671F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.1758F, 0.0093F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("ArmLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(6.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(98.5F, -17.5F, 10.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("ArmLeft", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.8F, 0.2504F, -0.0571F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createTranslationalVector(-0.8F, -0.0357F, 0.2349F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("LegLeft", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-2.2544F, -6.9816F, 9.1115F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("LegLeft", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -3.2F, 2.2F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("LegRight", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(21.2399F, 2.6878F, -13.0209F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("LegRight", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.2F, -2.8F, 1.3F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("AmenBreak", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 1.1F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("BodyB", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("BodyB", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("BodyT", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("BodyT", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -1.4F, -1.8F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("Torso", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.1F, -5.0F, 0.4F), Transformation.Interpolations.CUBIC)
			))
			.build();

	public static final Animation Guard = Animation.Builder.create(0.1F).looping()
		.build();

	public static final Animation WindUp = Animation.Builder.create(0.5F).looping()
		.build();

	public static final Animation Punch = Animation.Builder.create(0.5F)
		.build();
}