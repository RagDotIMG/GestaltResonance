package net.ragdot.gestaltresonance;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;

public class GestaltAssignments {

    // All our tags will look like: "gestalt:gestaltresonance:scorched_utopia"
    private static final String TAG_PREFIX = "gestalt:";

    /**
     * Assigns a Gestalt to a player, clearing any previous Gestalt assignment.
     */
    public static void assignGestalt(PlayerEntity player, Identifier gestaltId) {
        clearGestalt(player);
        player.addCommandTag(TAG_PREFIX + gestaltId.toString());
    }

    /**
     * Returns the currently assigned Gestalt id, or null if none.
     */
    public static Identifier getAssignedGestalt(PlayerEntity player) {
        for (String tag : player.getCommandTags()) {
            if (tag.startsWith(TAG_PREFIX)) {
                try {
                    return Identifier.of(tag.substring(TAG_PREFIX.length()));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return null;
    }

    /**
     * Clears any Gestalt assignment from this player and resets all Gestalt data.
     */
    public static void clearGestalt(PlayerEntity player) {
        Set.copyOf(player.getCommandTags()).stream()
                .filter(tag -> tag.startsWith(TAG_PREFIX))
                .forEach(player::removeCommandTag);
        
        ((net.ragdot.gestaltresonance.util.IGestaltPlayer) player).gestaltresonance$resetAllGestaltData();
    }

    /**
     * Convenience helper: checks if this player is assigned to a specific Gestalt.
     */
    public static boolean hasGestalt(PlayerEntity player, Identifier gestaltId) {
        Identifier assigned = getAssignedGestalt(player);
        return assigned != null && assigned.equals(gestaltId);
    }
}



