package com.diamondgoobird.trialchambertimer;

import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.awt.*;

/**
 * Handles the rendering for the Trial Spawner Timer
 * These methods are called from the mixins that hook into
 * Minecraft's code
 */
public class TimerRenderer {
    /**
     * Draws the cooldown timer above a given TrialSpawnerBlockEntity
     *
     * @param world1 the world the block exists in
     * @param be the trialspawnerblockentity
     * @param matrixStack the 3d transformations used to draw the text
     * @param vertexConsumerProvider handles the layer management of the rendering
     * @param entityRenderDispatcher accounts for the rotation of the camera looking at the text
     */
    public static void drawTimer(World world1, TrialSpawnerBlockEntity be, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, EntityRenderDispatcher entityRenderDispatcher) {
        // If the player just quit the game then don't render
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        assert world1 != null;
        // Gets the ending time of the cooldown
        long end = TimerHandler.getTime(world1, be.pos);
        long current = world1.getTime();
        // Calculates remaining duration
        long left = end - current;

        // Deletes if the full cooldown has elapsed
        if (left < 0) {
            if (end != 0) {
                TimerHandler.deleteTime(world1, be.pos);
            }
            return;
        }

        // Calculates and displays time left
        double minutes = left / 1200.0;
        double seconds = (minutes - Math.floor(minutes)) * 60;
        Text t = Text.of(String.format("%02d:%02d", (int) minutes, (int) seconds));

        drawTextAboveBlock(t, Color.MAGENTA.getRGB(), matrixStack, entityRenderDispatcher, vertexConsumerProvider);
    }

    /**
     * Draws colored text, facing the player, above a block this is inserted to the render code of
     *
     * @param t text to draw
     * @param color color to draw the text
     * @param matrixStack the 3d transformations used to draw the text
     * @param entityRenderDispatcher accounts for the rotation of the camera looking at the text
     * @param vertexConsumerProvider handles the layer management of the rendering
     */
    public static void drawTextAboveBlock(Text t, int color, MatrixStack matrixStack, EntityRenderDispatcher entityRenderDispatcher, VertexConsumerProvider vertexConsumerProvider) {
        TextRenderer r = MinecraftClient.getInstance().textRenderer;

        float width = r.getWidth(t);
        matrixStack.translate(0.5f, 1.25f, 0.5f);
        // Uses player rotation
        matrixStack.multiply(entityRenderDispatcher.getRotation());

        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        // Makes the text face the player
        matrix4f.rotate((float) Math.PI, 0.0F, 1.0F, 0.0F);
        matrix4f.scale(-0.025F, -0.025F, -0.025F);

        // -width/2 to center the text
        r.draw(t, -width / 2, 0.0f, color, true, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }
}
