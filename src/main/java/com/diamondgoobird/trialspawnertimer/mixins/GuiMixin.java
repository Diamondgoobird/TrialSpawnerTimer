package com.diamondgoobird.trialspawnertimer.mixins;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import com.diamondgoobird.trialspawnertimer.config.ConfigScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow public abstract @Nullable Screen screen();

    /**
     * Uses convoluted logic to display our GUI by changing the current screen right before the game tries to extract/render it
     *
     * @return a new config screen if the user typed /trialspawnertimer or the current screen otherwise
     */
    @Redirect(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = Opcodes.GETFIELD, ordinal = 0))
    public Screen redirectScreenField(Gui instance) {
        /*
         Uses convoluted logic to display our GUI when the user runs /trialspawnertimer

         I had to do it this way because if I did:

         Minecraft.getInstance().gui.setScreen();

         inside of the command execution it would either crash or throw an exception because it isn't on the render thread
        */
        if (TrialSpawnerTimer.showGui) {
            TrialSpawnerTimer.showGui = false;
            instance.setScreen(new ConfigScreen(instance.screen()));
        }
        return screen();
    }
}
