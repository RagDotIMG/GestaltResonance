package net.ragdot.gestaltresonance.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.GestaltAssignments;
import net.ragdot.gestaltresonance.GestaltTiers;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;

import java.util.List;

public class SoulEssenceItem extends Item {
    public SoulEssenceItem(Settings settings) {
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

            // Take the first (there should normally be at most one)
            GestaltBase current = gestalts.get(0);
            if (current.getLvl() < 5) {
                user.sendMessage(Text.literal("Your Gestalt is not strong enough yet."), true);
                return TypedActionResult.fail(stack);
            }

            Identifier currentId = current.getGestaltId();
            Identifier nextId = GestaltTiers.getTier2(currentId);
            if (nextId == null) {
                user.sendMessage(Text.literal("Your Gestalt is not strong enough yet."), true);
                return TypedActionResult.fail(stack);
            }

            // Consume item (unless creative)
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            // Update assignment so future summons use Tier 2
            GestaltAssignments.assignGestalt(user, nextId);

            // Initialize Tier 2 starting stats per design: level 5, 0 EXP
            var gp = (net.ragdot.gestaltresonance.util.IGestaltPlayer) user;
            gp.gestaltresonance$setGestaltLvl(nextId, 5);
            gp.gestaltresonance$setGestaltExp(nextId, 0);

            // Despawn current Gestalt; player can re-summon and get Tier 2
            current.despawnWithCleanup();

            user.sendMessage(Text.literal("Your Gestalt grew stronger!"), true);
            return TypedActionResult.success(stack, false);
        }

        return TypedActionResult.pass(stack);
    }
}
