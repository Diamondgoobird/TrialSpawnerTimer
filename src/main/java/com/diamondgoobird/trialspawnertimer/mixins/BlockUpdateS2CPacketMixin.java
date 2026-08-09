package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TimerHandler;
import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundBlockUpdatePacket.class)
public abstract class BlockUpdateS2CPacketMixin {
    @Shadow public abstract BlockPos getPos();
    @Shadow public abstract BlockState getBlockState();

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("HEAD"))
    public void onBlockUpdate(ClientGamePacketListener clientPlayPacketListener, CallbackInfo ci) {
        // We use the client's world because the packet doesn't send us one,
        // and we would only get block updates for the world we're in
        Level w = Minecraft.getInstance().level;
        // If the world doesn't exist then don't update timers
        if (w == null) {
            return;
        }
        // Check if our Trial Spawner was just destroyed and turned into air
        if (getBlockState().getBlock() instanceof AirBlock) {
            // If we have a timer at the given position, delete it
            if (TimerHandler.hasTimer(w, getPos())) {
                TimerHandler.deleteTime(w, getPos());
            }
        }
        // If this event fires for a block that's not a TrialSpawner we return
        if (!(getBlockState().getBlock() instanceof TrialSpawnerBlock)) {
            return;
        }
        // Send the block update to see if we need to update our timer
        TrialSpawnerTimer.onSpawnerBlockUpdate(w, getPos(), getBlockState());
    }
}
