package D7lan.minecartsloadchunks;

import D7lan.minecartsloadchunks.util.LoadsChunksAccessor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class MinecartsLoadChunksCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cartloader")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                    ServerPlayerEntity user = ctx.getSource().getPlayer();
                                    if (user == null) {
                                        ctx.getSource().sendError(Text.literal("You must be a player to use this command!"));
                                        return 0;
                                    }

                                    AbstractMinecartEntity cart = getLookingAtMinecart(user, 5.0D);
                                    if (cart == null) {
                                        ctx.getSource().sendMessage(Text.literal("You must be looking at a minecart to use this command!"));
                                        return 0;
                                    }

                                    LoadsChunksAccessor accessor = (LoadsChunksAccessor) cart;
                                    accessor.setLoadsChunks(enabled);

                                    ctx.getSource().sendMessage(Text.literal("Set loadsChunks for " + cart.getUuid() + " to " + enabled));
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("get")
                        .executes(ctx -> {
                            ServerPlayerEntity user = ctx.getSource().getPlayer();
                            if (user == null) {
                                ctx.getSource().sendError(Text.literal("You must be a player to use this command!"));
                                return 0;
                            }

                            AbstractMinecartEntity cart = getLookingAtMinecart(user, 5.0D);
                            if (cart == null) {
                                ctx.getSource().sendMessage(Text.literal("You must be looking at a minecart to use this command!"));
                                return 0;
                            }

                            LoadsChunksAccessor accessor = (LoadsChunksAccessor) cart;
                            boolean loadsChunks = accessor.getLoadsChunks();

                            ctx.getSource().sendMessage(Text.literal("Minecart " + cart.getUuid() + " loadsChunks value is: " + loadsChunks));
                            return 1;
                        })
                )
        );
    }

    /**
     * Raycast to find a minecart entity in the player's line of sight.
     */
    private static AbstractMinecartEntity getLookingAtMinecart(ServerPlayerEntity player, double maxDistance) {
        HitResult hit = ProjectileUtil.getCollision(player,
                entity -> entity instanceof AbstractMinecartEntity,
                maxDistance
        );

        if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof AbstractMinecartEntity cart) {
                return cart;
            }
        }
        return null;
    }
}
