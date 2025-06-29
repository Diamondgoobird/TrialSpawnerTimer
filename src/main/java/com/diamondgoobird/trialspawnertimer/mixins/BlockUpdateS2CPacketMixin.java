package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TimerHandler;
import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockUpdateS2CPacket.class)
public abstract class BlockUpdateS2CPacketMixin {
    @Shadow public abstract BlockPos getPos();
    @Shadow public abstract BlockState getState();

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("HEAD"))
    public void onBlockUpdate(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        // We use the client's world because the packet doesn't send us one,
        // and we would only get block updates for the world we're in
        World w = MinecraftClient.getInstance().world;
        assert w != null;
        // Check if our Trial Spawner was just destroyed and turned into air
        if (getState().getBlock() instanceof AirBlock) {
            // If we have a timer at the given position, delete it
            if (TimerHandler.hasTimer(w, getPos())) {
                TimerHandler.deleteTime(w, getPos());
            }
        }
        // If this event fires for a block that's not a TrialSpawner we return
        if (!(getState().getBlock() instanceof TrialSpawnerBlock)) {
            return;
        }
        // Send the block update to see if we need to update our timer
        TrialSpawnerTimer.onSpawnerBlockUpdate(w, getPos(), getState());
    }
}
