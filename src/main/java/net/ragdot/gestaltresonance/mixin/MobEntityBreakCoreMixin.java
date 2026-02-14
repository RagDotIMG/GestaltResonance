package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityBreakCoreMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void gestaltresonance$adjustPathfindingPenalties(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        if (self.getWorld().isClient) return;

        if (self.getCommandTags().contains("gestaltresonance$break_core_marked")) {
            // Check if behavior timer has started (timer >= 200)
            int timer = 0;
            for (String tag : self.getCommandTags()) {
                if (tag.startsWith("gestaltresonance$break_core_timer:")) {
                    try {
                        timer = Integer.parseInt(tag.substring("gestaltresonance$break_core_timer:".length()));
                        break;
                    } catch (NumberFormatException ignored) {}
                }
            }

            if (timer >= 200) {
                // Remove avoidance for lava and fire
                self.setPathfindingPenalty(PathNodeType.LAVA, 0.0f);
                self.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0f);
                self.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0f);
                // Also cactus and other hazards
                self.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0f);
                self.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0f);
            }
        }
    }
}
