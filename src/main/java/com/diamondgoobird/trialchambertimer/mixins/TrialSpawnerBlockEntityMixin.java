package com.diamondgoobird.trialchambertimer.mixins;

import com.diamondgoobird.trialchambertimer.TrialSpawnerTimer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrialSpawnerBlockEntity.class)
public abstract class TrialSpawnerBlockEntityMixin extends BlockEntity {
    @Shadow public abstract TrialSpawnerLogic getSpawner();

    public TrialSpawnerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "setSpawnerState", at = @At("HEAD"))
    public void onSetSpawnerState(World world, TrialSpawnerState spawnerState, CallbackInfo ci) {
        // Only insert the time at the state when the server calculates the time
        if (spawnerState != TrialSpawnerState.WAITING_FOR_REWARD_EJECTION) {
            return;
        }
        TrialSpawnerTimer.insertTime(world, this.pos, world.getTime() + this.getSpawner().getCooldownLength());
    }
}
