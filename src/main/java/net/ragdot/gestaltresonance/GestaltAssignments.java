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
        // 1) Clear any old Gestalt tags
        clearGestalt(player);

        // 2) Add a new tag with the full id string
        //    e.g. "gestalt:gestaltresonance:scorched_utopia"
        String tag = TAG_PREFIX + gestaltId.toString();
        player.addCommandTag(tag);
    }

    /**
     * Returns the currently assigned Gestalt id, or null if none.
     */
    public static Identifier getAssignedGestalt(PlayerEntity player) {
        Set<String> tags = player.getCommandTags();

        for (String tag : tags) {
            if (tag.startsWith(TAG_PREFIX)) {
                String idStr = tag.substring(TAG_PREFIX.length()); // remove "gestalt:"
                try {
                    return Identifier.of(idStr);
                } catch (IllegalArgumentException ignored) {
                    // invalid id string - ignore and keep looking
                }
            }
        }
        return null;
    }

    /**
     * Clears any Gestalt assignment from this player.
     */
    public static void clearGestalt(PlayerEntity player) {
        // Need a copy because we can't modify the set while iterating
        Set<String> tags = Set.copyOf(player.getCommandTags());

        for (String tag : tags) {
            if (tag.startsWith(TAG_PREFIX)) {
                player.removeCommandTag(tag);
            }
        }
    }

    /**
     * Convenience helper: checks if this player is assigned to a specific Gestalt.
     */
    public static boolean hasGestalt(PlayerEntity player, Identifier gestaltId) {
        Identifier assigned = getAssignedGestalt(player);
        return assigned != null && assigned.equals(gestaltId);
    }
}



