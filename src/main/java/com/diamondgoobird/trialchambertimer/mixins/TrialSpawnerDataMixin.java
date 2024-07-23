package com.diamondgoobird.trialchambertimer.mixins;

import net.minecraft.block.spawner.TrialSpawnerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(TrialSpawnerData.class)
public interface TrialSpawnerDataMixin {
    @Accessor("cooldownEnd")
    public long getCooldownEnd();
    @Accessor("cooldownEnd")
    public void setCooldownEnd(long cooldownEnd);
}
