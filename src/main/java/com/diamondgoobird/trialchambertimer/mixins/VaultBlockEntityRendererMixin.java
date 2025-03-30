package com.diamondgoobird.trialchambertimer.mixins;

import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.VaultBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(VaultBlockEntityRenderer.class)
public class VaultBlockEntityRendererMixin {
    @Unique
    EntityRenderDispatcher dispatcher;


    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstruct(BlockEntityRendererFactory.Context context, CallbackInfo ci) {
        dispatcher = context.getEntityRenderDispatcher();
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/VaultBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V", at = @At("RETURN"))
    public void onRender(VaultBlockEntity vaultBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d, CallbackInfo ci) {
        ItemStack stack;
        BlockPos pos = vaultBlockEntity.pos;

        if (MinecraftClient.getInstance().isInSingleplayer()) {
            ServerWorld world = MinecraftClient.getInstance().getServer().getWorld(vaultBlockEntity.getWorld().getRegistryKey());
            VaultBlockEntity entity = ((VaultBlockEntity) world.getWorldChunk(pos).getBlockEntity(pos));
            if (entity == null) {
                return;
            }
            stack = entity.getConfig().keyItem();
        }
        else {
            return;
        }

        Text t = stack.getFormattedName();
        TextRenderer r = MinecraftClient.getInstance().textRenderer;

        float width = r.getWidth(t);
        matrixStack.translate(0.5f, 1.25f, 0.5f);
        matrixStack.multiply(dispatcher.getRotation());

        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        matrix4f.rotate((float) Math.PI, 0.0F, 1.0F, 0.0F);
        matrix4f.scale(-0.025F, -0.025F, -0.025F);

        r.draw(t, -width / 2, 0.0f, Color.MAGENTA.getRGB(), true, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, i);
    }
}
