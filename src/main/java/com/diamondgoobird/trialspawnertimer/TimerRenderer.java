package com.diamondgoobird.trialspawnertimer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

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
    public static boolean drawTimer(Level world1, BlockPos pos, PoseStack matrixStack, int light, SubmitNodeCollector queue, Camera camera) {
        // If the player just quit the game then don't render
        if (Minecraft.getInstance().player == null) {
            return false;
        }
        // Gets the ending time of the cooldown
        Timer ti = TimerHandler.getTimer(world1, pos);
        if (ti == null) {
            // No timer, so return
            return false;
        }
        long end = ti.getTimerEnd();
        long current = world1.getGameTime();
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
        Component t = Component.nullToEmpty(String.format("%02d:%02d", (int) minutes, (int) seconds));

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
     * @param poseStack the 3d transformations used to draw the text
     * @param light the light level this text is being rendered at
     */
    public static void drawTextAboveBlock(Component t, int color, PoseStack poseStack, int light, SubmitNodeCollector submitNodeCollector, Camera camera) {
        poseStack.pushPose();
        float yaw = camera.yRot();
        float pitch = camera.xRot();
        Quaternionf rotation = new Quaternionf();
        rotation.rotationYXZ((float)(-Math.PI) / 180 * DisplayRenderer.transformYRot(yaw), (float)Math.PI / 180 * DisplayRenderer.transformXRot(pitch), 0.0f);
        poseStack.rotate(rotation);

        Matrix4f pose = poseStack.last().pose();
        pose.rotate((float) Math.PI, 0.0F, 1.0F, 0.0F);
        pose.scale(-0.025F, -0.025F, -0.025F);
        int width = Minecraft.getInstance().font.width(t.getString());
        int height = 9;
        pose.translateLocal(0.5f, 1f, 0.5f);
        pose.translate(1.0F - width / 2.0F, -height, 0.0F);

        submitNodeCollector.submitText(
                poseStack,
                0.5f,
                0.5f,
                t.getVisualOrderText(),
                true,
                getRenderType(),
                light,
                color,
                0,
                0
        );
        poseStack.popPose();
    }

    /**
     * Gets the render type based on whether the text was specified to be see-through in the config
     * @return TextLayerType instance to draw text with, either POLYGON_OFFSET or SEE_THROUGH
     */
    public static Font.DisplayMode getRenderType() {
        return TrialSpawnerTimer.getConfig().getSeeThroughWalls() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET;
    }
}
