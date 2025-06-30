package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import com.diamondgoobird.trialspawnertimer.config.ConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    /**
     * Uses convoluted logic to display our GUI by changing the current screen right before the game tries to render it
     * @param instance the instance of the MinecraftClient
     * @return if the user just ran /trialspawnertimer then a new ConfigScreen instance, otherwise just the current screen
     */
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.GETFIELD, ordinal = 0))
    public Screen redirectGetScreen(MinecraftClient instance) {
        /*
         Uses convoluted logic to display our GUI when the user runs /trialspawnertimer

         I had to do it this way because if I did:

         MinecraftClient.getInstance().setScreen()

         inside of the command execution it would either crash or throw an exception because it isn't on the render thread
        */
        if (TrialSpawnerTimer.showGui) {
            instance.setScreen(new ConfigScreen(instance.currentScreen));
            TrialSpawnerTimer.showGui = false;
        }
        return instance.currentScreen;
    }
}
