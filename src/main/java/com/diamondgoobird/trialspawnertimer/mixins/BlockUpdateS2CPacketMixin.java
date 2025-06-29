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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateS2CPacketMixin {
    @Shadow @Final private BlockState state;

    @Shadow @Final private BlockPos pos;

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("HEAD"))
    public void onBlockUpdate(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        // If this event fires for a block that's not a TrialSpawner we return
        World w = MinecraftClient.getInstance().world;
        if (state.getBlock() instanceof AirBlock) {
            if (TimerHandler.hasTimer(w, pos)) {
                TimerHandler.deleteTime(w, pos);
            }
        }
        if (!(state.getBlock() instanceof TrialSpawnerBlock) || w == null) {
            return;
        }
        TrialSpawnerTimer.onSpawnerBlockUpdate(w, pos, state);
    }
}
