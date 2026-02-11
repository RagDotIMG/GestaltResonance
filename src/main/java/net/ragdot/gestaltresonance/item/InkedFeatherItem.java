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
import net.ragdot.gestaltresonance.Gestaltresonance;

public class InkedFeatherItem extends Item {

    private static final Identifier SPILLWAYS_ID = Identifier.of(Gestaltresonance.MOD_ID, "spillways");

    public InkedFeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            Identifier current = GestaltAssignments.getAssignedGestalt(user);

            if (current != null && current.equals(SPILLWAYS_ID)) {
                GestaltAssignments.clearGestalt(user);
                user.sendMessage(Text.literal("Your bond with Spillways fades."), true);
            } else {
                GestaltAssignments.assignGestalt(user, SPILLWAYS_ID);
                user.sendMessage(Text.literal("You attune to Spillways."), true);

                if (!user.getAbilities().creativeMode && (current == null || !current.equals(SPILLWAYS_ID))) {
                    stack.decrement(1);
                }
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
