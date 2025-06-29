package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockUpdateS2CPacket.class)
public abstract class BlockEntityUpdateS2CPacketMixin {
    @Shadow public abstract BlockState getState();

    @Shadow public abstract BlockPos getPos();

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("HEAD"))
    public void onPacketApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        if (this.getState().getBlock() instanceof TrialSpawnerBlock) {
            TrialSpawnerTimer.onSpawnerBlockUpdate(MinecraftClient.getInstance().world, getPos(), getState());
        }
    }
}
