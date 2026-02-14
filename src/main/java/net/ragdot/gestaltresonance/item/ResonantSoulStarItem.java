package net.ragdot.gestaltresonance.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.GestaltAssignments;
import net.ragdot.gestaltresonance.GestaltTiers;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;

import java.util.List;

public class ResonantSoulStarItem extends Item {
    public ResonantSoulStarItem(Settings settings) {
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

            // Use the first (there should normally only be one)
            GestaltBase current = gestalts.get(0);
            Identifier currentId = current.getGestaltId();

            // Must be level 8 and have a Tier 3 mapping from current (Tier 2) id
            Identifier nextId = current.getLvl() >= 8 ? GestaltTiers.getTier3(currentId) : null;
            if (nextId == null) {
                // Player has a Gestalt but it doesn't fulfill the requirements
                user.sendMessage(net.minecraft.text.Text.literal("Your Gestalt is not strong enough yet."), true);
                return TypedActionResult.fail(stack);
            }

            // Success: consume only if not in Creative
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            // Update assignment so future summons use Tier 3
            GestaltAssignments.assignGestalt(user, nextId);

            // Preserve level from Tier 2
            int previousLvl = current.getLvl();
            var gp = (net.ragdot.gestaltresonance.util.IGestaltPlayer) user;
            gp.gestaltresonance$setGestaltLvl(nextId, previousLvl);
            gp.gestaltresonance$setGestaltExp(nextId, 0);

            // Despawn current Gestalt; player can re-summon and get Tier 3
            current.despawnWithCleanup();

            user.sendMessage(net.minecraft.text.Text.literal("Your Gestalt grew stronger!"), true);
            return TypedActionResult.success(stack, false);
        }

        return TypedActionResult.pass(stack);
    }
}
