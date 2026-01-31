package net.ragdot.gestaltresonance.entities.gestaltframework;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

public class GestaltAnimationHelper {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState guardAnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();
    public final AnimationState grabAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private final GestaltBase gestalt;

    public GestaltAnimationHelper(GestaltBase gestalt) {
        this.gestalt = gestalt;
    }

    public void updateAnimationStates() {
        if (gestalt.getWorld().isClient) {
            PlayerEntity owner = gestalt.getOwner();
            boolean anyOtherAnimationRunning = false;

            if (owner != null) {
                IGestaltPlayer gp = (IGestaltPlayer) owner;
                
                // Guard animation
                if (gp.gestaltresonance$isGuarding()) {
                    this.guardAnimationState.startIfNotRunning(gestalt.age);
                    anyOtherAnimationRunning = true;
                } else {
                    this.guardAnimationState.stop();
                }

                // Grab animation
                if (gp.gestaltresonance$isLedgeGrabbing()) {
                    this.grabAnimationState.startIfNotRunning(gestalt.age);
                    anyOtherAnimationRunning = true;
                } else {
                    this.grabAnimationState.stop();
                }
            }

            // Throw animation
            if (gestalt.getDataTracker().get(GestaltBase.IS_THROWING)) {
                this.throwAnimationState.startIfNotRunning(gestalt.age);
                anyOtherAnimationRunning = true;
            } else {
                this.throwAnimationState.stop();
            }

            // Idle animation handling
            if (anyOtherAnimationRunning) {
                this.idleAnimationState.stop();
                this.idleAnimationTimeout = 0;
            } else {
                this.idleAnimationState.startIfNotRunning(gestalt.age);
            }
        }
    }
}
