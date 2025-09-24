package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TimerRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer.*;

@Mixin(TrialSpawnerBlockEntityRenderer.class)
public abstract class TrialSpawnerBlockEntityRendererMixin {
    @Shadow @Final private EntityRenderManager entityRenderDispatcher;

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/state/BlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("RETURN"))
    public void onRender(BlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
        World w = MinecraftClient.getInstance().world;

        // If there is no timer rendered, check for updates
        boolean rend = TimerRenderer.drawTimer(w, state.pos, matrices, w.getLightLevel(state.pos), queue, entityRenderDispatcher.camera);
        // If higher sensitivity is on then check for updates
        if (getConfig().isHighSensitivity()) {
            onSpawnerStateUpdate(w, state.pos, state.blockState.get(Properties.TRIAL_SPAWNER_STATE));
        }
    }
}
