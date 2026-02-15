package net.ragdot.gestaltresonance.entities.gestaltframework;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

public class GestaltAnimationHelper {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState guardAnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();
    public final AnimationState grabAnimationState = new AnimationState();
    public final AnimationState introAnimationState = new AnimationState();
    public final AnimationState windUpAnimationState = new AnimationState();
    public final AnimationState punchAnimationState = new AnimationState();
    private final GestaltBase gestalt;

    public GestaltAnimationHelper(GestaltBase gestalt) {
        this.gestalt = gestalt;
    }

    public void stopAllActionAnimations() {
        this.guardAnimationState.stop();
        this.throwAnimationState.stop();
        this.grabAnimationState.stop();
        this.introAnimationState.stop();
        this.windUpAnimationState.stop();
        this.punchAnimationState.stop();
    }

    public void updateAnimationStates() {
        if (gestalt.getWorld().isClient) {
            boolean anyOtherAnimationRunning = false;
            IGestaltPlayer gp = (IGestaltPlayer) gestalt.getOwner();

            // Guard animation
            if (gp != null && gp.gestaltresonance$isGuarding()) {
                this.guardAnimationState.startIfNotRunning(gestalt.age);
                anyOtherAnimationRunning = true;
            } else {
                this.guardAnimationState.stop();
            }

            // Grab animation (Ledge Grab)
            boolean isGrabbing = gp != null && gp.gestaltresonance$isLedgeGrabbing();
            if (isGrabbing) {
                this.grabAnimationState.startIfNotRunning(gestalt.age);
                anyOtherAnimationRunning = true;
            } else {
                this.grabAnimationState.stop();
            }

            // Throw animation
            if (gp != null && gp.gestaltresonance$isGestaltThrowActive() && !isGrabbing) {
                this.throwAnimationState.startIfNotRunning(gestalt.age);
                anyOtherAnimationRunning = true;
            } else {
                this.throwAnimationState.stop();
            }

            // Intro animation
            if (gestalt.getDataTracker().get(GestaltBase.IS_INTRO)) {
                this.introAnimationState.startIfNotRunning(gestalt.age);
                anyOtherAnimationRunning = true;
            } else {
                this.introAnimationState.stop();
            }

            // WindUp animation
            if (gestalt.getDataTracker().get(GestaltBase.IS_WINDING_UP) && !gestalt.getDataTracker().get(GestaltBase.IS_PUNCHING)) {
                this.windUpAnimationState.startIfNotRunning(gestalt.age);
                anyOtherAnimationRunning = true;
            } else {
                this.windUpAnimationState.stop();
            }

            // Punch animation
            if (gestalt.getDataTracker().get(GestaltBase.IS_PUNCHING)) {
                this.punchAnimationState.startIfNotRunning(gestalt.age);
                this.windUpAnimationState.stop(); // Ensure WindUp stops if Punch starts
                anyOtherAnimationRunning = true;
            } else {
                this.punchAnimationState.stop();
            }

            // Fallback: If we are close to the owner and not in an active action state,
            // force all non-idle animations to stop.
            PlayerEntity owner = gestalt.getOwner();
            if (owner != null && !anyOtherAnimationRunning) {
                double distSq = gestalt.squaredDistanceTo(owner);
                if (distSq < 4.0) { // Within 2 blocks
                    this.stopAllActionAnimations();
                }
            }

            // Idle animation handling
            if (anyOtherAnimationRunning) {
                this.idleAnimationState.stop();
            } else {
                this.idleAnimationState.startIfNotRunning(gestalt.age);
            }
        }
    }
}
