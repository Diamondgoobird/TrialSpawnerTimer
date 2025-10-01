package com.diamondgoobird.trialspawnertimer;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

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
     * @param pos the position of the trialspawnerblockentity
     * @param matrixStack the 3d transformations used to draw the text
     * @param light the light level this text is being rendered in
     * @return returns false if the timer isn't rendered, true otherwise
     */
    public static boolean drawTimer(World world1, BlockPos pos, MatrixStack matrixStack, int light, OrderedRenderCommandQueue queue, Camera camera) {
        // If the player just quit the game then don't render
        if (MinecraftClient.getInstance().player == null) {
            return false;
        }
        assert world1 != null;
        // Gets the ending time of the cooldown
        Timer ti = TimerHandler.getTimer(world1, pos);
        if (ti == null) {
            // No timer, so return
            return false;
        }
        long end = ti.getTimerEnd();
        long current = world1.getTime();
        // Calculates remaining duration
        long left = Math.max(end - current, 0);

        // Deletes if the full cooldown has elapsed
        if (left == 0) {
            TimerHandler.deleteTime(world1, pos);
            return false;
        }

        // Calculates and displays time left
        double minutes = left / 1200.0;
        double seconds = (minutes - Math.floor(minutes)) * 60;
        Text t = Text.of(String.format("%02d:%02d", (int) minutes, (int) seconds));

        int c = getColor((double) left / ti.getCooldown());

        // Always have light be bright -> block light doesn't work right now
        light = 15728880;

        drawTextAboveBlock(t, c, matrixStack, light, queue, camera);
        return true;
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
     * @param light the light level this text is being rendered at
     */
    public static void drawTextAboveBlock(Text t, int color, MatrixStack matrixStack, int light, OrderedRenderCommandQueue queue, Camera camera) {

        matrixStack.push();
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();
        Quaternionf rotation = new Quaternionf();
        rotation.rotationYXZ((float)(-Math.PI) / 180 * DisplayEntityRenderer.getBackwardsYaw(yaw), (float)Math.PI / 180 * DisplayEntityRenderer.getNegatedPitch(pitch), 0.0f);
        matrixStack.multiply(rotation);

        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        matrix4f.rotate((float) Math.PI, 0.0F, 1.0F, 0.0F);
        matrix4f.scale(-0.025F, -0.025F, -0.025F);
        int m = MinecraftClient.getInstance().textRenderer.getWidth(t.getString());
        int n = 9;
        matrix4f.translateLocal(0.5f, 1f, 0.5f);
        matrix4f.translate(1.0F - m / 2.0F, -n, 0.0F);
        queue.submitText(
                matrixStack,
                0.5f,
                0.5f,
                t.asOrderedText(),
                true,
                getRenderType(),
                light,
                color,
                0,
                0
        );
        matrixStack.pop();
    }

    /**
     * Gets the render type based on whether the text was specified to be see-through in the config
     * @return TextLayerType instance to draw text with, either POLYGON_OFFSET or SEE_THROUGH
     */
    public static TextRenderer.TextLayerType getRenderType() {
        return TrialSpawnerTimer.getConfig().getSeeThroughWalls() ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.POLYGON_OFFSET;
    }
}
