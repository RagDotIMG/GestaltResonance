package net.ragdot.gestaltresonance.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Debug item that resets all power cooldowns for the player's summoned Gestalt.
 */
public class DebugCooldownResetItem extends Item {
    public DebugCooldownResetItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Find all summoned Gestalts owned by the player
            List<GestaltBase> gestalts = world.getEntitiesByClass(
                    GestaltBase.class,
                    user.getBoundingBox().expand(256.0),
                    g -> {
                        var uuid = g.getOwnerUuid();
                        return uuid != null && uuid.equals(user.getUuid());
                    }
            );

            if (!gestalts.isEmpty()) {
                for (GestaltBase gestalt : gestalts) {
                    gestalt.resetCooldowns();
                }
                user.sendMessage(Text.literal("Gestalt power cooldowns reset."), true);

                // Consume item on success unless Creative
                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                return TypedActionResult.success(stack, false);
            } else {
                user.sendMessage(Text.literal("No summoned Gestalt found."), true);
                return TypedActionResult.fail(stack);
            }
        }

        return TypedActionResult.pass(stack);
    }
}
