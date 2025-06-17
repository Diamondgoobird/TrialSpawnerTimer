package com.diamondgoobird.trialchambertimer.mixins;

import com.diamondgoobird.trialchambertimer.TrialSpawnerTimer;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
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
    public void onApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        if (state.getBlock() instanceof TrialSpawnerBlock && state.getEntries().get(Properties.TRIAL_SPAWNER_STATE) == TrialSpawnerState.COOLDOWN) {
            if (TrialSpawnerTimer.getTime(pos) == 0) {
                assert MinecraftClient.getInstance().world != null;
                TrialSpawnerTimer.insertTime(pos, MinecraftClient.getInstance().world.getTime() + ((TrialSpawnerBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos)).getSpawner().getCooldownLength());
            }
        }
    }
}
