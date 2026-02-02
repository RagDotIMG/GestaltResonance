package net.ragdot.gestaltresonance.item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PokeyFeatherItem extends Item {
    public PokeyFeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (world.getBlockState(pos).isOf(Blocks.CRYING_OBSIDIAN)) {
            if (!world.isClient) {
                // Drop Nether Tear
                ItemStack netherTear = new ItemStack(ModItems.NETHER_TEAR);
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, netherTear);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);

                // Turn Crying Obsidian into Obsidian
                world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());

                // Play sounds
                world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1.0f, 0.5f);
                world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }
}
