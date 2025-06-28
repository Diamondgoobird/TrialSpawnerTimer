package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TimerRenderer;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrialSpawnerBlockEntityRenderer.class)
public class TrialSpawnerBlockEntityRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "render(Lnet/minecraft/block/entity/TrialSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V", at = @At("RETURN"))
    public void onRender(TrialSpawnerBlockEntity trialSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d, CallbackInfo ci) {
        TimerRenderer.drawTimer(trialSpawnerBlockEntity.getWorld(), trialSpawnerBlockEntity, matrixStack, vertexConsumerProvider, entityRenderDispatcher);
    }
}
