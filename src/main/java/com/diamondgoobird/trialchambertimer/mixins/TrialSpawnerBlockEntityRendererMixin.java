package com.diamondgoobird.trialchambertimer.mixins;

import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TrialSpawnerBlockEntityRenderer.class)
public class TrialSpawnerBlockEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/block/entity/TrialSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("RETURN"))
    public void onRender(TrialSpawnerBlockEntity trialSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        World world1 = trialSpawnerBlockEntity.getWorld();
        if (world1 != null) {
            long end = ((TrialSpawnerDataMixin) trialSpawnerBlockEntity.getSpawner().getData()).getCooldownEnd();
            long current = world1.getTime();
            long left = current - end;
            Text t = Text.of("Yippee!");
            if (left > 0) {
                t = Text.of(String.valueOf(left));
            }
            TextRenderer r = MinecraftClient.getInstance().textRenderer;
            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            matrix4f.rotate(3.1415927F, 0.0F, 1.0F, 0.0F);
            matrix4f.scale(-0.025F, -0.025F, -0.025F);
            r.draw(t, 0.0f, 0.0f, Color.BLUE.getRGB(), false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.SEE_THROUGH, 0, i);
        }
    }
}
