package net.ragdot.gestaltresonance;

import net.minecraft.util.Identifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for Gestalt Tier progressions.
 * Maps:
 *  - Tier 1 Gestalt id -> Tier 2 Gestalt id
 *  - Tier 2 Gestalt id -> Tier 3 Gestalt id
 */
public final class GestaltTiers {
    private GestaltTiers() {}

    private static final Map<Identifier, Identifier> TIER2_MAP = new HashMap<>();
    private static final Map<Identifier, Identifier> TIER3_MAP = new HashMap<>();

    /**
     * Registers a Tier 2 mapping for a given Tier 1 gestalt id.
     */
    public static void registerTier2(Identifier tier1, Identifier tier2) {
        if (tier1 == null || tier2 == null) return;
        TIER2_MAP.put(tier1, tier2);
    }

    /**
     * Returns the Tier 2 gestalt id for the given Tier 1 id, or null if none registered.
     */
    public static Identifier getTier2(Identifier tier1) {
        return TIER2_MAP.get(tier1);
    }

    /**
     * Exposes an immutable view (debug/inspection only).
     */
    public static Map<Identifier, Identifier> getAllTier2() {
        return Collections.unmodifiableMap(TIER2_MAP);
    }

    /**
     * Registers a Tier 3 mapping for a given Tier 2 gestalt id.
     */
    public static void registerTier3(Identifier tier2, Identifier tier3) {
        if (tier2 == null || tier3 == null) return;
        TIER3_MAP.put(tier2, tier3);
    }

    /**
     * Returns the Tier 3 gestalt id for the given Tier 2 id, or null if none registered.
     */
    public static Identifier getTier3(Identifier tier2) {
        return TIER3_MAP.get(tier2);
    }

    /**
     * Exposes an immutable view (debug/inspection only).
     */
    public static Map<Identifier, Identifier> getAllTier3() {
        return Collections.unmodifiableMap(TIER3_MAP);
    }
}
