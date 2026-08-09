package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TimerRenderer;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer.*;

@Mixin(TrialSpawnerBlockEntityRenderer.class)
public class TrialSpawnerBlockEntityRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "render(Lnet/minecraft/block/entity/TrialSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("RETURN"))
    public void onRender(TrialSpawnerBlockEntity trialSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        World w = trialSpawnerBlockEntity.getWorld();
        // If the world doesn't exist then don't render
        if (w == null) {
            return;
        }
        // If there is no timer rendered, check for updates
        boolean rend = TimerRenderer.drawTimer(w, trialSpawnerBlockEntity, matrixStack, vertexConsumerProvider, entityRenderDispatcher, i);
        // If higher sensitivity is on then check for updates
        if (getConfig().isHighSensitivity()) {
            onSpawnerStateUpdate(w, trialSpawnerBlockEntity.getPos(), trialSpawnerBlockEntity.getSpawnerState());
        }
    }
}
