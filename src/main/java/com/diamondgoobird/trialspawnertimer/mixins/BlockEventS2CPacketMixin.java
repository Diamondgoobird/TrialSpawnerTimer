package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.minecraft.block.Block;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEventS2CPacket.class)
public abstract class BlockEventS2CPacketMixin {
    @Shadow public abstract Block getBlock();

    @Shadow public abstract BlockPos getPos();

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("HEAD"))
    public void onPacketApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        if (getBlock() instanceof TrialSpawnerBlock) {
            assert MinecraftClient.getInstance().world != null;
            TrialSpawnerTimer.onSpawnerBlockUpdate(MinecraftClient.getInstance().world, getPos(), MinecraftClient.getInstance().world.getBlockState(getPos()));
        }
    }
}