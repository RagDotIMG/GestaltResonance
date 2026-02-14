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
import net.ragdot.gestaltresonance.effect.ModStatusEffects;
import net.ragdot.gestaltresonance.entities.AmenBreak;
import net.ragdot.gestaltresonance.entities.AmenBreakII;
import net.ragdot.gestaltresonance.entities.AmenBreakIII;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;
import net.ragdot.gestaltresonance.entities.ScorchedUtopiaII;
import net.ragdot.gestaltresonance.entities.ScorchedUtopiaIII;
import net.ragdot.gestaltresonance.entities.Spillways;
import net.ragdot.gestaltresonance.entities.SpillwaysII;
import net.ragdot.gestaltresonance.entities.SpillwaysIII;
import net.ragdot.gestaltresonance.item.ModItemGroups;
import net.ragdot.gestaltresonance.block.ModBlocks;
import net.ragdot.gestaltresonance.item.ModItems;
import net.ragdot.gestaltresonance.projectile.PopBud;
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
                    .create(GestaltBase::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("gestalt")
    );

    public static final EntityType<ScorchedUtopia> SCORCHED_UTOPIA = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "scorched_utopia"),
            EntityType.Builder
                    .create(ScorchedUtopia::new, SpawnGroup.MISC)
                    .dimensions(0.75f, 1.75f)
                    .build("scorched_utopia")
    );

    public static final EntityType<ScorchedUtopiaII> SCORCHED_UTOPIA_II = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "scorched_utopia_ii"),
            EntityType.Builder
                    .create(ScorchedUtopiaII::new, SpawnGroup.MISC)
                    .dimensions(0.75f, 1.75f)
                    .build("scorched_utopia_ii")
    );

    public static final EntityType<ScorchedUtopiaIII> SCORCHED_UTOPIA_III = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "scorched_utopia_iii"),
            EntityType.Builder
                    .create(ScorchedUtopiaIII::new, SpawnGroup.MISC)
                    .dimensions(0.75f, 1.75f)
                    .build("scorched_utopia_iii")
    );

    public static final EntityType<AmenBreak> AMEN_BREAK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "amen_break"),
            EntityType.Builder
                    .create(AmenBreak::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("amen_break")
    );

    public static final EntityType<AmenBreakII> AMEN_BREAK_II = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "amen_break_ii"),
            EntityType.Builder
                    .create(AmenBreakII::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("amen_break_ii")
    );

    public static final EntityType<AmenBreakIII> AMEN_BREAK_III = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "amen_break_iii"),
            EntityType.Builder
                    .create(AmenBreakIII::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("amen_break_iii")
    );

    public static final EntityType<Spillways> SPILLWAYS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "spillways"),
            EntityType.Builder
                    .create(Spillways::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("spillways")
    );

    public static final EntityType<SpillwaysII> SPILLWAYS_II = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "spillways_ii"),
            EntityType.Builder
                    .create(SpillwaysII::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("spillways_ii")
    );

    public static final EntityType<SpillwaysIII> SPILLWAYS_III = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "spillways_iii"),
            EntityType.Builder
                    .create(SpillwaysIII::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.6f)
                    .build("spillways_iii")
    );

    public static final EntityType<PopBud> POP_BUD = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "pop_bud"),
            EntityType.Builder
                    .<PopBud>create(PopBud::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .build("pop_bud")
    );

    public static final EntityType<net.ragdot.gestaltresonance.entities.PopSprout> POP_SPROUT = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "pop_sprout"),
            EntityType.Builder
                    .create(net.ragdot.gestaltresonance.entities.PopSprout::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .makeFireImmune()
                    .build("pop_sprout")
    );

    @Override
    public void onInitialize() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(world -> {
            net.ragdot.gestaltresonance.util.BreakCoreManager.tick(world);
        });
        ModStatusEffects.registerStatusEffects();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();
        FabricDefaultAttributeRegistry.register(GESTALT_BASE_ENTITY_TYPE, GestaltBase.createBaseStandAttributes());
        FabricDefaultAttributeRegistry.register(SCORCHED_UTOPIA, ScorchedUtopia.createAttributes());
        FabricDefaultAttributeRegistry.register(SCORCHED_UTOPIA_II, ScorchedUtopia.createAttributes());
        FabricDefaultAttributeRegistry.register(SCORCHED_UTOPIA_III, ScorchedUtopia.createAttributes());
        FabricDefaultAttributeRegistry.register(AMEN_BREAK, AmenBreak.createAttributes());
        FabricDefaultAttributeRegistry.register(AMEN_BREAK_II, AmenBreak.createAttributes());
        FabricDefaultAttributeRegistry.register(AMEN_BREAK_III, AmenBreak.createAttributes());
        FabricDefaultAttributeRegistry.register(SPILLWAYS, Spillways.createAttributes());
        FabricDefaultAttributeRegistry.register(SPILLWAYS_II, Spillways.createAttributes());
        FabricDefaultAttributeRegistry.register(SPILLWAYS_III, Spillways.createAttributes());
        FabricDefaultAttributeRegistry.register(POP_SPROUT, net.minecraft.entity.mob.MobEntity.createMobAttributes());
        // Pop Bud is a projectile (no attributes required)

        // Tier mappings (Tier I -> Tier II, Tier II -> Tier III)
        GestaltTiers.registerTier2(Identifier.of(MOD_ID, "amen_break"), Identifier.of(MOD_ID, "amen_break_ii"));
        GestaltTiers.registerTier3(Identifier.of(MOD_ID, "amen_break_ii"), Identifier.of(MOD_ID, "amen_break_iii"));

        GestaltTiers.registerTier2(Identifier.of(MOD_ID, "scorched_utopia"), Identifier.of(MOD_ID, "scorched_utopia_ii"));
        GestaltTiers.registerTier3(Identifier.of(MOD_ID, "scorched_utopia_ii"), Identifier.of(MOD_ID, "scorched_utopia_iii"));

        GestaltTiers.registerTier2(Identifier.of(MOD_ID, "spillways"), Identifier.of(MOD_ID, "spillways_ii"));
        GestaltTiers.registerTier3(Identifier.of(MOD_ID, "spillways_ii"), Identifier.of(MOD_ID, "spillways_iii"));

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
                                if (player != null) {
                                    summonStand(source.getWorld(), player.getBlockPos(), player);
                                }
                                return Command.SINGLE_SUCCESS;
                            })
            );

            dispatcher.register(
                    CommandManager.literal("dismissStand")
                            .requires(source -> source.hasPermissionLevel(0))
                            .executes(context -> {
                                var source = context.getSource();
                                var player = source.getPlayer();
                                if (player != null) {
                                    dismissStand(source.getWorld(), player);
                                }
                                return Command.SINGLE_SUCCESS;
                            })
            );
        });
    }

    public void summonStand(World world, BlockPos pos, PlayerEntity owner) {
        GestaltBase stand = new GestaltBase(GESTALT_BASE_ENTITY_TYPE, world);
        stand.setOwner(owner);

        stand.refreshPositionAndAngles(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
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