package com.diamondgoobird.trialchambertimer.mixins;

import com.diamondgoobird.trialchambertimer.TrialSpawnerTimer;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TrialSpawnerBlockEntityRenderer.class)
public class TrialSpawnerBlockEntityRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "render(Lnet/minecraft/block/entity/TrialSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V", at = @At("RETURN"))
    public void onRender(TrialSpawnerBlockEntity trialSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d, CallbackInfo ci) {
        World world1 = trialSpawnerBlockEntity.getWorld();
        // If the player just quit the game then don't render
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        assert world1 != null;
        // Gets the ending time of the cooldown
        long end = TrialSpawnerTimer.getTime(world1, trialSpawnerBlockEntity.pos);
        long current = world1.getTime();
        // Calculates remaining duration
        long left = end - current;

        // Deletes if the full cooldown has elapsed
        if (left < 0) {
            if (end != 0) {
                TrialSpawnerTimer.deleteTime(world1, trialSpawnerBlockEntity.pos);
            }
            return;
        }

        // Calculates and displays time left
        double minutes = left / 1200.0;
        double seconds = (minutes - Math.floor(minutes)) * 60;
        Text t = Text.of(String.format("%02d:%02d", (int) minutes, (int) seconds));
        TextRenderer r = MinecraftClient.getInstance().textRenderer;

        float width = r.getWidth(t);
        matrixStack.translate(0.5f, 1.25f, 0.5f);
        // Uses player rotation
        matrixStack.multiply(this.entityRenderDispatcher.getRotation());

        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        // Makes the text face the player
        matrix4f.rotate((float) Math.PI, 0.0F, 1.0F, 0.0F);
        matrix4f.scale(-0.025F, -0.025F, -0.025F);

        // -width/2 to center the text
        r.draw(t, -width / 2, 0.0f, Color.MAGENTA.getRGB(), true, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i);
    }
}
