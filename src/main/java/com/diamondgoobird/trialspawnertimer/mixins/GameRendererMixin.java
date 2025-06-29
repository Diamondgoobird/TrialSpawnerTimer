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
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.GETFIELD))
    public Screen redirectGetScreen(MinecraftClient instance) {
        if (TrialSpawnerTimer.showGui) {
            instance.setScreen(new ConfigScreen(instance.currentScreen));
            TrialSpawnerTimer.showGui = false;
        }
        return instance.currentScreen;
    }
}
