package net.ragdot.gestaltresonance.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.GestaltAssignments;

public class BlastedFeatherItem extends Item {

    // This feather corresponds to the Amen Break Gestalt
    private static final Identifier AMEN_BREAK_ID =
            Identifier.of(Gestaltresonance.MOD_ID, "amen_break");

    public BlastedFeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // What Gestalt is the player currently assigned to?
            Identifier current = GestaltAssignments.getAssignedGestalt(user);

            if (current != null && current.equals(AMEN_BREAK_ID)) {
                // Already assigned to Amen Break -> unassign
                GestaltAssignments.clearGestalt(user);
                user.sendMessage(Text.literal("Your bond with Amen Break fades."), true);
            } else {
                // Assigned to something else OR nothing -> assign Amen Break
                GestaltAssignments.assignGestalt(user, AMEN_BREAK_ID);
                 user.sendMessage(Text.literal("You attune to Amen Break."), true);

                 if (!user.getAbilities().creativeMode &&
                     (current == null || !current.equals(AMEN_BREAK_ID))) {
                     stack.decrement(1);
                 }
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
