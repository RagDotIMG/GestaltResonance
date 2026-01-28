package net.ragdot.gestaltresonance;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.entities.GestaltBase;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;
import net.ragdot.gestaltresonance.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Gestaltresonance implements ModInitializer {
    public static final String MOD_ID = "gestaltresonance";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<GestaltBase> GESTALT_BASE_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "gestalt"),
            EntityType.Builder
                    .<GestaltBase>create(GestaltBase::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("gestalt")
    );

    public static final EntityType<ScorchedUtopia> SCORCHED_UTOPIA = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "scorched_utopia"),
            EntityType.Builder
                    .<ScorchedUtopia>create(ScorchedUtopia::new, SpawnGroup.MISC)
                    .dimensions(0.75f, 1.75f)
                    .build("scorched_utopia")
    );

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        FabricDefaultAttributeRegistry.register(GESTALT_BASE_ENTITY_TYPE, GestaltBase.createBaseStandAttributes());
        FabricDefaultAttributeRegistry.register(SCORCHED_UTOPIA, ScorchedUtopia.createAttributes());

        registerCommands();
        GestaltNetworking.registerServerReceivers();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("summonStand")
                            .requires(source -> source.hasPermissionLevel(0))
                            .executes(context -> {
                                var source = context.getSource();
                                var player = source.getPlayer(); // server-side
                                var world = source.getWorld();

                                summonStand(world, player.getBlockPos(), player);
                                return Command.SINGLE_SUCCESS;
                            })
            );


            dispatcher.register(
                    CommandManager.literal("dismissStand")
                            .requires(source -> source.hasPermissionLevel(0))
                            .executes(context -> {
                                var source = context.getSource();
                                var player = source.getPlayer();
                                var world = source.getWorld();

                                dismissStand(world, player);
                                return Command.SINGLE_SUCCESS;
                            })
            );

        });
    }

    public void summonStand(World world, BlockPos pos, PlayerEntity owner) {
        GestaltBase stand = new GestaltBase(GESTALT_BASE_ENTITY_TYPE, world);
        stand.setOwner(owner);

        stand.refreshPositionAndAngles(
                owner.getX(),
                owner.getY(),
                owner.getZ(),
                owner.getYaw(),
                0.0f
        );

        world.spawnEntity(stand);
    }

    public void dismissStand(World world, PlayerEntity owner) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return; // commands run on server, but be safe
        }

        List<GestaltBase> stands = serverWorld.getEntitiesByClass(
                GestaltBase.class,
                owner.getBoundingBox().expand(256.0), // big radius around player
                stand -> {
                    // match by live owner reference OR by stored UUID
                    if (stand.getOwner() == owner) return true;
                    return stand.getOwnerUuid() != null
                            && stand.getOwnerUuid().equals(owner.getUuid());
                }
        );

        for (GestaltBase stand : stands) {
            stand.discard();
        }
    }
}