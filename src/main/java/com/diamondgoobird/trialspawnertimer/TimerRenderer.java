package com.diamondgoobird.trialspawnertimer;

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
        Timer ti = TimerHandler.getTimer(world1, be.getPos());
        if (ti == null) {
            // No timer, so return
            return;
        }
        long end = ti.getTimerEnd();
        long current = world1.getTime();
        // Calculates remaining duration
        long left = Math.max(end - current, 0);

        // Deletes if the full cooldown has elapsed
        if (left == 0) {
            TimerHandler.deleteTime(world1, be.getPos());
            return;
        }

        // Calculates and displays time left
        double minutes = left / 1200.0;
        double seconds = (minutes - Math.floor(minutes)) * 60;
        Text t = Text.of(String.format("%02d:%02d", (int) minutes, (int) seconds));

        int c = getColor((double) left / ti.getCooldown());

        drawTextAboveBlock(t, c, matrixStack, entityRenderDispatcher, vertexConsumerProvider);
    }

    /**
     * Gets the color that the text should be based on the progress of the
     * timer that is completed
     *
     * @param progress the progress (from 0-1) of the color
     * @return the color representation in integer form
     */
    private static int getColor(double progress) {
        if (TrialSpawnerTimer.getConfig().getChromaTimer()) {
            // blue to red rainbow cycle
            return Color.getHSBColor((float) (progress) / 2, 1.0f, 1.0f).getRGB();
        }
        return Color.MAGENTA.getRGB();
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
        r.draw(t, -width / 2, 0.0f, color, true, matrix4f, vertexConsumerProvider, getRenderType(), 0, /*Color.WHITE.getRGB()*/15728880);
    }

    /**
     * Gets the render type based on whether the text was specified to be see-through in the config
     * @return TextLayerType instance to draw text with, either NORMAL or SEE_THROUGH
     */
    public static TextRenderer.TextLayerType getRenderType() {
        return TrialSpawnerTimer.getConfig().getSeeThroughWalls() ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL;
    }
}
