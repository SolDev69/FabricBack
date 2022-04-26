package me.reimnop.fabricback.mixin;

import me.reimnop.fabricback.BackState;
import me.reimnop.fabricback.Location;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow @Final public MinecraftServer server;

    @Shadow public abstract ServerWorld getWorld();

    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("RETURN"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PersistentStateManager stateManager = server.getOverworld().getPersistentStateManager();
        BackState state = stateManager.getOrCreate(BackState::fromNbt, BackState::new, BackState.BACK_KEY);
        state.setLastDeathPos(player.getUuid(), Optional.of(new Location(
                getWorld().getRegistryKey().getValue(),
                player.getPos())));
    }
}
