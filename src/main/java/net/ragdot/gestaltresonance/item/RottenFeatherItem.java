package net.ragdot.gestaltresonance.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.GestaltAssignments;

public class RottenFeatherItem extends Item {

    // This feather corresponds to the Scorched Utopia Gestalt
    private static final Identifier SCORCHED_UTOPIA_ID =
            Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia");

    public RottenFeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // What Gestalt is the player currently assigned to?
            Identifier current = GestaltAssignments.getAssignedGestalt(user);

            if (current != null && current.equals(SCORCHED_UTOPIA_ID)) {
                // Already assigned to Scorched Utopia -> unassign
                GestaltAssignments.clearGestalt(user);
                // Optional: feedback (chat message or sound)
                // user.sendMessage(Text.literal("Your bond with Scorched Utopia fades."), true);
            } else {
                // Assigned to something else OR nothing -> assign Scorched Utopia
                GestaltAssignments.assignGestalt(user, SCORCHED_UTOPIA_ID);
                // Optional: feedback
                // user.sendMessage(Text.literal("You attune to Scorched Utopia."), true);

                // Optional: consume the feather only when first assigning
                // if (!user.getAbilities().creativeMode &&
                //     (current == null || !current.equals(SCORCHED_UTOPIA_ID))) {
                //     stack.decrement(1);
                // }
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}


