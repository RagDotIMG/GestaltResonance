package net.ragdot.gestaltresonance.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;

import java.util.List;

/**
 * Testing utility item: grants 600 Gestalt EXP to the currently summoned, owned Gestalt.
 * This is intended to quickly boost a Gestalt to level 5 for upgrade testing.
 */
public class DebugExpBoosterItem extends Item {
    private static final int GRANT_AMOUNT = 600; // Enough for 4 levels at 120 exp/level

    public DebugExpBoosterItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Find currently controlled/summoned Gestalt owned by this user
            List<GestaltBase> gestalts = world.getEntitiesByClass(
                    GestaltBase.class,
                    user.getBoundingBox().expand(256.0),
                    g -> {
                        var uuid = g.getOwnerUuid();
                        return uuid != null && uuid.equals(user.getUuid());
                    }
            );

            if (gestalts.isEmpty()) {
                // Silently fail when no Gestalt is owned/summoned
                return TypedActionResult.fail(stack);
            }

            GestaltBase current = gestalts.get(0);

            int remaining = GRANT_AMOUNT;
            int maxExp = current.getMaxExp();

            // Chain level-ups correctly using GestaltBase.setExp semantics
            while (remaining > 0 && current.getLvl() < 5) {
                int curExp = current.getExp();
                int need = Math.max(0, maxExp - curExp);
                if (need == 0) {
                    // Trigger a level up by passing maxExp or greater
                    current.setExp(maxExp);
                } else if (remaining >= need) {
                    current.setExp(curExp + need); // levels up, resets EXP to 0
                    remaining -= need;
                } else {
                    current.setExp(curExp + remaining); // partial fill, no level up
                    remaining = 0;
                }
            }

            // If we still have remaining after reaching level 5, fill EXP bar to max
            if (current.getLvl() >= 5) {
                current.setExp(maxExp);
            }

            // Consume item on success unless Creative
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            user.sendMessage(Text.literal("Granted 600 Gestalt EXP."), true);
            return TypedActionResult.success(stack, false);
        }

        return TypedActionResult.pass(stack);
    }
}
