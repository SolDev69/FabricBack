package me.reimnop.fabricback;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.Optional;
import java.util.logging.Logger;

public class FabricBack implements ModInitializer {

    public static final Logger logger = Logger.getLogger("fabricback");

    @Override
    public void onInitialize() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            MutableText text = new LiteralText("You died! ")
                    .formatted(Formatting.BOLD, Formatting.RED)
                    .append(new LiteralText("Use "))
                    .append(new LiteralText("/back").setStyle(Style.EMPTY.withFormatting(Formatting.UNDERLINE, Formatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/back"))))
                    .append(new LiteralText(" to return to your last death point!"));
            newPlayer.sendMessage(text, false);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("back").executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayer();
                BackState state = player.getServer().getOverworld().getPersistentStateManager().get(BackState::fromNbt, BackState.BACK_KEY);
                if (state == null || state.getLastDeathPos(player.getUuid()).isEmpty()) {
                    player.sendMessage(new LiteralText("You haven't died yet, or you have already teleported to your last death point!").formatted(Formatting.RED), false);
                    return 1;
                }
                Location loc = state.getLastDeathPos(player.getUuid()).get();
                MinecraftServer server = player.getServer();
                ServerWorld world = server.getWorld(RegistryKey.of(Registry.WORLD_KEY, loc.world));
                player.teleport(world, loc.position.x, loc.position.y, loc.position.z, 0.0f, 0.0f);
                state.setLastDeathPos(player.getUuid(), Optional.empty());
                return 1;
            }));
        });

        logger.info("Fabric Back initialized!");
    }
}
