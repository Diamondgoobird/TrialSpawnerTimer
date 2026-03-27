package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TimerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.TrialSpawnerRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer.*;

@Mixin(TrialSpawnerRenderer.class)
public abstract class TrialSpawnerBlockEntityRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderer;

    @Inject(method = "Lnet/minecraft/client/renderer/blockentity/TrialSpawnerRenderer;submit(Lnet/minecraft/client/renderer/blockentity/state/SpawnerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("RETURN"))
    public void onRender(SpawnerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        Level w = Minecraft.getInstance().level;

        // If there is no timer rendered, check for updates
        boolean rend = TimerRenderer.drawTimer(w, state.blockPos, poseStack, w.getMaxLocalRawBrightness(state.blockPos), submitNodeCollector, entityRenderer.camera);
        // If higher sensitivity is on then check for updates
        if (getConfig().isHighSensitivity()) {
            onSpawnerStateUpdate(w, state.blockPos, state.blockState.getValue(BlockStateProperties.TRIAL_SPAWNER_STATE));
        }
    }
}
