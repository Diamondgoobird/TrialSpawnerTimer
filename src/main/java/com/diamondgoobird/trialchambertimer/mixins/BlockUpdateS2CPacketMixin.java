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
    public void onApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        // If this event fires for a block that's not a TrialSpawner we return
        if (!(state.getBlock() instanceof TrialSpawnerBlock)) {
            return;
        }
        World w = MinecraftClient.getInstance().world;
        assert w != null;
        // If the block was destroyed for some reason or updated to a different state then delete the timer
        if (TrialSpawnerTimer.shouldReset((TrialSpawnerState) state.getEntries().get(Properties.TRIAL_SPAWNER_STATE))) {
            TrialSpawnerTimer.deleteTime(w, pos);
            return;
        }
        if (TrialSpawnerTimer.getTime(w, pos) == 0) {
            TrialSpawnerBlockEntity trialSpawnerBlock = (TrialSpawnerBlockEntity) w.getBlockEntity(pos);
            assert trialSpawnerBlock != null;
            assert trialSpawnerBlock.getSpawner() != null;
            TrialSpawnerTimer.insertTime(w, pos, w.getTime() + trialSpawnerBlock.getSpawner().getCooldownLength());
        }
    }
}
