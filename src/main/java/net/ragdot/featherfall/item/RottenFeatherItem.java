package net.ragdot.featherfall.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.ragdot.featherfall.Featherfall;
import net.ragdot.featherfall.entities.ScorchedUtopia;

public class RottenFeatherItem extends Item {

    public RottenFeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            boolean hadStand = false;

            if (world instanceof ServerWorld serverWorld) {
                // Find all ScorchedUtopia stands owned by this player
                var stands = serverWorld.getEntitiesByClass(
                        ScorchedUtopia.class,
                        user.getBoundingBox().expand(256.0),
                        stand -> {
                            var uuid = stand.getOwnerUuid(); // from base CustomStand
                            return uuid != null && uuid.equals(user.getUuid());
                        }
                );

                // If any exist, despawn them and mark that we had one
                if (!stands.isEmpty()) {
                    hadStand = true;
                    stands.forEach(ScorchedUtopia::discard);
                }
            }

            // Toggle behavior: if player had a stand, we just turned it off
            if (!hadStand) {
                // No existing stand -> summon a new one

                // spawn slightly behind and above the player
                float yaw = user.getYaw();
                double rad = Math.toRadians(yaw);

                double backOffset = 1.9;
                double sideOffset = 0.5;
                double heightOffset = 0.4;

                double backX = -Math.sin(rad);
                double backZ =  Math.cos(rad);
                double rightX =  Math.cos(rad);
                double rightZ =  Math.sin(rad);

                double spawnX = user.getX() + backOffset * backX + sideOffset * rightX;
                double spawnZ = user.getZ() + backOffset * backZ + sideOffset * rightZ;
                double spawnY = user.getY() + heightOffset;

                ScorchedUtopia stand = new ScorchedUtopia(Featherfall.SCORCHED_UTOPIA, world);
                stand.setOwner(user);
                stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, user.getYaw(), 0.0f);
                world.spawnEntity(stand);


                /*if (!user.getAbilities().creativeMode) {
                    stack.decrement(1); // consume only when actually summoning
                }*/
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}

